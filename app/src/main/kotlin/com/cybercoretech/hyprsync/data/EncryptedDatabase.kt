// EncryptedDatabase.kt – SQLCipher database whose passphrase is protected by the Android Keystore.
package com.cybercoretech.hyprsync.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * The SQLCipher passphrase: 32 random bytes, generated once.
 *
 * Android Keystore keys can't be exported (their `encoded` bytes are null), so the
 * passphrase can't be the Keystore key itself. Instead the random passphrase is
 * encrypted ("wrapped") with an AES-256-GCM Keystore key, and only the wrapped copy
 * and its IV are stored, in app-private SharedPreferences.
 */
object DatabaseKey {
    private const val KEY_ALIAS = "hyprsync_db_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_BITS = 128
    private const val PASSPHRASE_BYTES = 32
    private const val PREFS = "hyprsync_db_key"
    private const val PREF_WRAPPED = "wrapped"
    private const val PREF_IV = "iv"

    fun passphrase(context: Context): ByteArray {
        val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val wrapped = prefs.getString(PREF_WRAPPED, null)
        val iv = prefs.getString(PREF_IV, null)
        if (wrapped != null && iv != null) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, keystoreKey(), GCMParameterSpec(GCM_TAG_BITS, decode(iv)))
            return cipher.doFinal(decode(wrapped))
        }

        val passphrase = ByteArray(PASSPHRASE_BYTES).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keystoreKey()) // the Keystore picks a fresh IV
        val sealed = cipher.doFinal(passphrase)
        prefs
            .edit()
            .putString(PREF_WRAPPED, encode(sealed))
            .putString(PREF_IV, encode(cipher.iv))
            .apply()
        return passphrase
    }

    /** The AES-256-GCM wrapping key, created in the Keystore on first use. */
    private fun keystoreKey(): SecretKey {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (ks.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val spec =
            KeyGenParameterSpec
                .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        return KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            .apply { init(spec) }
            .generateKey()
    }

    private fun encode(bytes: ByteArray) = Base64.encodeToString(bytes, Base64.NO_WRAP)

    private fun decode(text: String) = Base64.decode(text, Base64.NO_WRAP)
}

/**
 * SQLiteOpenHelper that opens the database encrypted with the passphrase from [DatabaseKey].
 */
class EncryptedDatabaseHelper(
    context: Context,
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val appContext = context.applicationContext

    companion object {
        private const val DATABASE_NAME = "hyprsync.db"
        private const val DATABASE_VERSION = 1
    }

    init {
        // Load the native libraries for SQLCipher.
        SQLiteDatabase.loadLibs(context)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Basic Contact table – matches the Contact data class.
        db.execSQL(
            """
            CREATE TABLE contacts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                avatarUrl TEXT,
                notes TEXT,
                tags TEXT,
                strength REAL,
                lastInteraction INTEGER
            );
            """.trimIndent(),
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int,
    ) {
        // Simple migration strategy for now – drop and recreate.
        db.execSQL("DROP TABLE IF EXISTS contacts")
        onCreate(db)
    }

    /** Opens the encrypted DB with the Keystore-protected passphrase. */
    fun getWritableEncryptedDb(): SQLiteDatabase = getWritableDatabase(DatabaseKey.passphrase(appContext))

    fun getReadableEncryptedDb(): SQLiteDatabase = getReadableDatabase(DatabaseKey.passphrase(appContext))
}

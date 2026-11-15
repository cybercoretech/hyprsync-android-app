// EncryptedDatabase.kt – provides an SQLiteOpenHelper using SQLCipher and Android Keystore for encryption key.
package com.cybercoretech.hyprsync.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object SecureKeyProvider {
    private const val KEY_ALIAS = "hyprsync_db_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    /**
     * Returns a 256‑bit secret key stored in the Android Keystore. If the key does not exist, it is generated.
     */
    fun getOrCreateKey(): ByteArray {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE)
        ks.load(null)
        if (!ks.containsAlias(KEY_ALIAS)) {
            val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGen.init(spec)
            keyGen.generateKey()
        }
        val secretKey = ks.getKey(KEY_ALIAS, null) as SecretKey
        // The raw bytes of the secret key are used as the password for SQLCipher.
        return secretKey.encoded
    }
}

/**
 * SQLiteOpenHelper that opens the database encrypted with the key from SecureKeyProvider.
 */
class EncryptedDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Simple migration strategy for now – drop and recreate.
        db.execSQL("DROP TABLE IF EXISTS contacts")
        onCreate(db)
    }

    /** Opens the encrypted DB with the keystore‑derived password. */
    fun getWritableEncryptedDb(): SQLiteDatabase {
        val passphrase = SecureKeyProvider.getOrCreateKey()
        return getWritableDatabase(passphrase)
    }

    fun getReadableEncryptedDb(): SQLiteDatabase {
        val passphrase = SecureKeyProvider.getOrCreateKey()
        return getReadableDatabase(passphrase)
    }
}

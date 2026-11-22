package com.cybercoretech.hyprsync

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.cybercoretech.hyprsync.data.DatabaseKey
import com.cybercoretech.hyprsync.data.EncryptedDatabaseHelper
import net.zetetic.database.sqlcipher.SQLiteDatabase
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptedDatabaseTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("hyprsync.db")
        System.loadLibrary("sqlcipher")
    }

    @Test
    fun passphraseIs32BytesAndStable() {
        val first = DatabaseKey.passphrase(context)
        val second = DatabaseKey.passphrase(context)
        assertEquals(32, first.size)
        assertArrayEquals(first, second)
    }

    @Test
    fun contactSurvivesReopen() {
        EncryptedDatabaseHelper(context).getWritableEncryptedDb().use { db ->
            db.execSQL("INSERT INTO contacts (name, strength) VALUES (?, ?)", arrayOf<Any>("Alice", 0.5))
        }
        EncryptedDatabaseHelper(context).getReadableEncryptedDb().use { db ->
            db.rawQuery("SELECT name FROM contacts", arrayOf<String>()).use { c ->
                assertEquals(1, c.count)
                c.moveToFirst()
                assertEquals("Alice", c.getString(0))
            }
        }
    }

    @Test
    fun fileIsNotPlainSqlite() {
        EncryptedDatabaseHelper(context).getWritableEncryptedDb().close()
        val header = context.getDatabasePath("hyprsync.db").readBytes().copyOf(16)
        assertFalse("database header is plaintext", String(header, Charsets.US_ASCII).startsWith("SQLite format 3"))
    }

    @Test(expected = SQLiteException::class)
    fun wrongPassphraseIsRejected() {
        EncryptedDatabaseHelper(context).getWritableEncryptedDb().close()
        val path = context.getDatabasePath("hyprsync.db").path
        SQLiteDatabase.openDatabase(path, "not-the-key", null, SQLiteDatabase.OPEN_READWRITE, null).use { db ->
            db.rawQuery("SELECT count(*) FROM contacts", arrayOf<String>()).use { it.moveToFirst() }
        }
    }
}

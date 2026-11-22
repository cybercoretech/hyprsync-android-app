package com.cybercoretech.hyprsync

import com.cybercoretech.hyprsync.data.Contact
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterContactsTest {
    private val contacts =
        listOf(
            Contact(id = 1, name = "Alice Johnson", tags = listOf("Conference")),
            Contact(id = 2, name = "Bob Smith", tags = listOf("College")),
            Contact(id = 3, name = "Carol Lee", tags = listOf("Work")),
        )

    private fun ids(query: String) = filterContacts(contacts, query).map { it.id }

    @Test
    fun blankQueryKeepsEveryone() {
        assertEquals(listOf(1L, 2L, 3L), ids(""))
        assertEquals(listOf(1L, 2L, 3L), ids("   "))
    }

    @Test
    fun matchesNameCaseInsensitively() {
        assertEquals(listOf(2L), ids("bOb"))
    }

    @Test
    fun matchesTags() {
        assertEquals(listOf(3L), ids("work"))
        assertEquals(listOf(1L, 2L), ids("co")) // Conference, College
    }

    @Test
    fun noMatchGivesEmptyList() {
        assertEquals(emptyList<Long>(), ids("zzz"))
    }
}

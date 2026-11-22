// ContactListScreen.kt – contacts list with search
package com.cybercoretech.hyprsync

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.cybercoretech.hyprsync.data.Contact

@Composable
fun ContactListScreen(onContactClick: (Long) -> Unit) {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    val contacts = remember { mutableStateListOf<Contact>() }
    // Sample data – replace with DB fetch later
    LaunchedEffect(Unit) {
        contacts.addAll(sampleContacts())
    }
    val filtered = filterContacts(contacts, query.text)
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search contacts") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filtered) { contact ->
                ContactRow(contact, onClick = { onContactClick(contact.id) })
                HorizontalDivider()
            }
        }
    }
}

/** Contacts whose name or any tag contains [query] (case-insensitive); all of them when it's blank. */
fun filterContacts(
    contacts: List<Contact>,
    query: String,
): List<Contact> =
    if (query.isBlank()) {
        contacts
    } else {
        contacts.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
        }
    }

@Composable
fun ContactRow(
    contact: Contact,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            // Placeholder avatar
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(contact.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${(contact.strength * 100).toInt()}% strength",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
        IconButton(onClick = { /* TODO: QR share */ }) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share QR",
            )
        }
    }
}

fun sampleContacts(): List<Contact> =
    listOf(
        Contact(1, "Alice Johnson", null, "Met at conference", listOf("Conference"), 0.78f, System.currentTimeMillis()),
        Contact(2, "Bob Smith", null, "College friend", listOf("College"), 0.55f, System.currentTimeMillis()),
        Contact(3, "Carol Lee", null, "Work colleague", listOf("Work"), 0.92f, System.currentTimeMillis()),
    )

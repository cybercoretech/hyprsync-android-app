// ContactListScreen.kt – contacts list with search
package com.cybercoretech.hyprsync

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.cybercoretech.hyprsync.data.Contact

@Composable
fun ContactListScreen(onContactClick: (Long) -> Unit) {
    var query by remember { mutableStateOf(TextFieldValue(""))
    val contacts = remember { mutableStateListOf<Contact>() }
    // Sample data – replace with DB fetch later
    LaunchedEffect(Unit) {
        contacts.addAll(sampleContacts())
    }
    val filtered = if (query.text.isBlank()) contacts else contacts.filter {
        it.name.contains(query.text, ignoreCase = true) || it.tags.any { tag -> tag.contains(query.text, ignoreCase = true) }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search contacts") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filtered) { contact ->
                ContactRow(contact, onClick = { onContactClick(contact.id) })
                Divider()
            }
        }
    }
}

@Composable
fun ContactRow(contact: Contact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            // Placeholder avatar
            Image(
                painter = painterResource(id = android.R.drawable.sym_def_app_icon),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(contact.name, style = MaterialTheme.typography.titleMedium)
                Text("${contact.strength * 100}% strength", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        IconButton(onClick = { /* TODO: QR share */ }) {
            Icon(painter = painterResource(id = android.R.drawable.ic_menu_share), contentDescription = "Share QR")
        }
    }
}

fun sampleContacts(): List<Contact> = listOf(
    Contact(1, "Alice Johnson", null, "Met at conference", listOf("Conference"), 0.78f, System.currentTimeMillis()),
    Contact(2, "Bob Smith", null, "College friend", listOf("College"), 0.55f, System.currentTimeMillis()),
    Contact(3, "Carol Lee", null, "Work colleague", listOf("Work"), 0.92f, System.currentTimeMillis())
)
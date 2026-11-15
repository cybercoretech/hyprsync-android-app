// Contact data model with Room entity
package com.cybercoretech.hyprsync.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatarUrl: String? = null,
    val notes: String = "",
    val tags: List<String> = emptyList(),
    val strength: Float = 0f,
    val lastInteraction: Long = 0L // epoch ms
)
package com.cybercoretech.hyprsync.data

data class Contact(
    val id: Long = 0,
    val name: String,
    val avatarUrl: String? = null,
    val notes: String = "",
    val tags: List<String> = emptyList(),
    val strength: Float = 0f,
    val lastInteraction: Long = 0L // epoch ms
)
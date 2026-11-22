// Contact data model
package com.cybercoretech.hyprsync.data

import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    val id: Long = 0,
    val name: String,
    val avatarUrl: String? = null,
    val notes: String = "",
    val tags: List<String> = emptyList(),
    // 0..1
    val strength: Float = 0f,
    // epoch ms
    val lastInteraction: Long = 0L,
)

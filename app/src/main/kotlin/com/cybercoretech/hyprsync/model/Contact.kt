// Contact data model
package com.cybercoretech.hyprsync.model

data class Contact(
    val id: Long,
    val name: String,
    val avatarUrl: String?,
    val strength: Int, // 0..100
    val notes: String?,
    val tags: List<String>
)
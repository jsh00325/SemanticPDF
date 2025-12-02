package com.pdf.semantic.data.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.Date

@Entity
data class FolderEntity(
    @Id var id: Long = 0L,
    var parentId: Long? = null,
    var parentAbsolutePath: String = "/",
    var name: String = "",
    var createdAt: Date = Date(),
)

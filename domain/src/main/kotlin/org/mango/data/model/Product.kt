package org.mango.data.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class Product(
    val name: String,
    val price: BigDecimal,
    val id: UUID = UUID.randomUUID(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

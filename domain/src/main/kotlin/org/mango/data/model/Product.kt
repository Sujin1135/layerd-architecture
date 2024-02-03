package org.mango.data.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Product(
    val name: String,
    val price: BigDecimal,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

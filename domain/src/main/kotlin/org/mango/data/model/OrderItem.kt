package org.mango.data.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class OrderItem(
    val price: BigDecimal,
    val productId: UUID,
    val id: UUID = UUID.randomUUID(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

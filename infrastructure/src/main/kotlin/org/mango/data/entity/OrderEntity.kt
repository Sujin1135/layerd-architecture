package org.mango.data.entity

import org.mango.data.enum.OrderStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table(name = "orders")
data class OrderEntity(
    val uuid: String,
    @Id val id: Long? = null,
    val price: BigDecimal,
    val status: OrderStatus,
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
    @Column("updated_at")
    val updatedAt: LocalDateTime? = null,
)

@Table(name = "order_items")
data class OrderItemEntity(
    val uuid: String,
    @Id val id: Long? = null,
    @Column("order_id")
    val orderId: Long,
    val price: BigDecimal,
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
    @Column("updated_at")
    val updatedAt: LocalDateTime? = null,
)

@Table(name = "products")
data class ProductEntity(
    val uuid: String,
    @Id val id: Long? = null,
    val price: BigDecimal,
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
    @Column("updated_at")
    val updatedAt: LocalDateTime? = null,
)

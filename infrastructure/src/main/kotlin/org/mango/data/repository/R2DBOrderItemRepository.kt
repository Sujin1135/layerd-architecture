package org.mango.data.repository

import org.mango.data.entity.OrderItemEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface R2DBOrderItemRepository : CoroutineCrudRepository<OrderItemEntity, UInt> {
    @Query("SELECT * FROM order_items WHERE order_items.order_id = :orderId")
    suspend fun findByOrderId(orderId: Long): List<OrderItemEntity>

    @Query(
        "INSERT INTO order_items (uuid, order_id, price) VALUES (:uuid, :orderId, :price) " +
            "ON DUPLICATE KEY UPDATE order_items.price = :price",
    )
    suspend fun upsert(
        uuid: String,
        orderId: Long,
        price: BigDecimal,
    )
}

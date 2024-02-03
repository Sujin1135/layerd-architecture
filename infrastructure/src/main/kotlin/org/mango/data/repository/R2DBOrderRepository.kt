package org.mango.data.repository

import org.mango.data.entity.OrderEntity
import org.mango.data.enum.OrderStatus
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface R2DBOrderRepository : CoroutineCrudRepository<OrderEntity, ULong> {
    @Query("SELECT * FROM orders WHERE orders.uuid = :uuid")
    suspend fun findByUUID(uuid: String): OrderEntity?

    @Query(
        "INSERT INTO orders (uuid, price, status) VALUES (:uuid, :price, :status) " +
            "ON DUPLICATE KEY UPDATE orders.price = :price, orders.status = :status",
    )
    suspend fun upsert(
        uuid: String,
        price: BigDecimal,
        status: OrderStatus,
    )
}

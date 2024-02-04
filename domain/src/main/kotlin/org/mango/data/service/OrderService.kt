package org.mango.data.service

import org.mango.data.model.OrderItem
import java.util.UUID

interface OrderService {
    suspend fun createOrder(product: OrderItem): UUID

    suspend fun addOrderItem(
        id: UUID,
        orderItem: OrderItem,
    )

    suspend fun completeOrder(id: UUID)

    suspend fun deleteOrderItem(
        id: UUID,
        orderItemId: UUID,
    )
}

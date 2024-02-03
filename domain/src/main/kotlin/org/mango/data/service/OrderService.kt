package org.mango.data.service

import org.mango.data.model.Product
import java.util.UUID

interface OrderService {
    suspend fun createOrder(product: Product): UUID

    suspend fun addProduct(
        id: UUID,
        product: Product,
    )

    suspend fun completeOrder(id: UUID)

    suspend fun deleteProduct(
        id: UUID,
        productId: UUID,
    )
}

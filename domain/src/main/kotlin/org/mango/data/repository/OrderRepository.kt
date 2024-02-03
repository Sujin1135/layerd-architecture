package org.mango.data.repository

import org.mango.data.model.Order
import java.util.UUID

interface OrderRepository {
    suspend fun findById(id: UUID): Order?

    suspend fun save(order: Order)
}

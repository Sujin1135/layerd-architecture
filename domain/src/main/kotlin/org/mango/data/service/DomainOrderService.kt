package org.mango.data.service

import org.mango.data.exception.NotFoundException
import org.mango.data.model.Order
import org.mango.data.model.Product
import org.mango.data.repository.OrderRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DomainOrderService(private val repository: OrderRepository) : OrderService {
    override suspend fun createOrder(product: Product): UUID {
        val order = Order()
        order.addOrder(product)
        repository.save(order)

        return order.id
    }

    override suspend fun addProduct(
        id: UUID,
        product: Product,
    ) {
        val order = getOrder(id)
        order.addOrder(product)

        repository.save(order)
    }

    override suspend fun completeOrder(id: UUID) {
        val order = getOrder(id)
        order.complete()

        repository.save(order)
    }

    override suspend fun deleteProduct(
        id: UUID,
        productId: UUID,
    ) {
        val order = getOrder(id)
        order.removeOrder(productId)

        repository.save(order)
    }

    private suspend fun getOrder(id: UUID): Order {
        return repository.findById(id) ?: throw NotFoundException("Can not found order by id($id)")
    }
}

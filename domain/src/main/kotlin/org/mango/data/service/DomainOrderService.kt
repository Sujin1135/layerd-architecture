package org.mango.data.service

import org.mango.data.exception.NotFoundException
import org.mango.data.model.Order
import org.mango.data.model.OrderItem
import org.mango.data.repository.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.util.UUID

@Service
class DomainOrderService(
    private val repository: OrderRepository,
    transactionManager: ReactiveTransactionManager,
) : OrderService {
    private val transactionalOperator = TransactionalOperator.create(transactionManager)

    override suspend fun createOrder(orderItem: OrderItem): UUID {
        val order = Order()
        order.addOrder(orderItem)
        repository.save(order)

        return order.id
    }

    override suspend fun addOrderItem(
        id: UUID,
        orderItem: OrderItem,
    ) {
        transactionalOperator.executeAndAwait {
            val order = getOrder(id)
            order.addOrder(orderItem)

            repository.save(order)
        }
    }

    override suspend fun completeOrder(id: UUID) {
        val order = getOrder(id)
        order.complete()

        repository.save(order)
    }

    override suspend fun deleteOrderItem(
        id: UUID,
        orderItemId: UUID,
    ) {
        transactionalOperator.executeAndAwait {
            val order = getOrder(id)
            order.removeOrderItem(orderItemId)

            repository.save(order)
        }
    }

    private suspend fun getOrder(id: UUID): Order {
        return repository.findById(id) ?: throw NotFoundException("Can not found order by id($id)")
    }
}

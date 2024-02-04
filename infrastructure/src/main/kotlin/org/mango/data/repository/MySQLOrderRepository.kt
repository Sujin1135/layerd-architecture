package org.mango.data.repository

import org.mango.data.exception.NotFoundException
import org.mango.data.model.Order
import org.mango.data.model.OrderItem
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MySQLOrderRepository(
    private val repository: R2DBOrderRepository,
    private val itemRepository: R2DBOrderItemRepository,
) : OrderRepository {
    override suspend fun findById(id: UUID): Order? {
        val entity = repository.findByUUID(id.toString()) ?: throw NotFoundException("It is not existed order by uuid($id)")
        val orderItems = itemRepository.findByOrderId(entity.id!!)
        val order = Order(UUID.fromString(entity.uuid))
        order.price = entity.price
        order.orderItems =
            orderItems.map {
                OrderItem(
                    it.price,
                    UUID.fromString(it.uuid),
                    it.createdAt!!,
                    it.updatedAt!!,
                )
            }.toMutableList()
        return order
    }

    override suspend fun save(order: Order) {
        repository.upsert(order.id.toString(), order.price, order.status)

        val entity = repository.findByUUID(order.id.toString()) ?: throw NotFoundException("It is not existed order by uuid($order.id)")

        itemRepository.deleteByNotInUUIDs(order.orderItems.map { it.id.toString() })

        order.orderItems.forEach {
            itemRepository.upsert(
                it.id.toString(),
                entity.id!!,
                it.price,
            )
        }
    }
}

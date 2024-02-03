package org.mango.data.model

import org.mango.data.enum.OrderStatus
import org.mango.data.exception.BadRequestException
import org.mango.data.exception.NotFoundException
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class Order(
    val product: Product,
    val id: UUID = UUID.randomUUID(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    var price: BigDecimal = product.price
    var status: OrderStatus? = OrderStatus.CREATED
    var orderItems: MutableList<OrderItem> = mutableListOf(OrderItem(product.price, product.id))

    fun complete() {
        validateState()
        status = OrderStatus.COMPLETED
    }

    fun addOrder(product: Product) {
        validateState()
        orderItems.addLast(OrderItem(product.price, product.id))
        price += product.price
    }

    fun removeOrder(id: UUID) {
        validateState()
        val orderItem = getOrderItem(id)
        orderItems.remove(orderItem)

        price = price.subtract(orderItem.price)
    }

    private fun getOrderItem(id: UUID): OrderItem {
        return orderItems.firstOrNull { it.id == id } ?: throw NotFoundException("Can not found order item by id($id)")
    }

    private fun validateState() {
        if (OrderStatus.COMPLETED == status) {
            throw BadRequestException("The order is in completed state")
        }
    }
}

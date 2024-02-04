package org.mango.data.service

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.just
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mango.data.DomainTestConfig
import org.mango.data.enum.OrderStatus
import org.mango.data.exception.BadRequestException
import org.mango.data.exception.NotFoundException
import org.mango.data.model.Order
import org.mango.data.model.OrderItem
import org.mango.data.repository.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@SpringBootTest(classes = [DomainTestConfig::class])
@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
class OrderServiceTest(
    @Autowired @Qualifier("OrderService") private val service: OrderService,
    @Autowired private val repository: OrderRepository,
) {
    private val responseOrder = Order(UUID.randomUUID())

    @BeforeEach
    fun setup(): Unit =
        runBlocking {
            MockKAnnotations.init(this@OrderServiceTest)

            responseOrder.addOrder(OrderItem(BigDecimal(25000)))

            coEvery { repository.findById(responseOrder.id) } returns responseOrder
            coEvery { repository.save(any()) } just Runs
        }

    @Test
    fun `주문받은 상품에 대한 주문 데이터 생성 요청 후, 상품의 가격에 해당하는 데이터가 생성 되어야 한다`() =
        runBlocking {
            val price = BigDecimal(30000)
            val orderItem = OrderItem(price)
            val createdId = service.createOrder(orderItem)
            val order = Order()
            order.addOrder(orderItem)

            coEvery { repository.findById(createdId) } returns order

            val sut = repository.findById(createdId)

            assertEquals(sut?.price, price)
        }

    @Test
    fun `진행중인 주문에 상품을 추가하면, 추가한 상품 가격이 주문 가격에 반영 되어야 한다`() =
        runBlocking {
            val orderItem = OrderItem(BigDecimal(23000))
            val beforeResponseOrderPrice = responseOrder.price

            service.addOrderItem(responseOrder.id, orderItem)

            assertEquals(responseOrder.price, beforeResponseOrderPrice + orderItem.price)
        }

    @Test
    fun `이미 완료된 주문에 상품을 추가하면, 올바른 메시지와 함께 BadRequestException이 발생해야 한다`(): Unit =
        runBlocking {
            responseOrder.status = OrderStatus.COMPLETED

            val exception =
                assertFailsWith<BadRequestException> {
                    service.addOrderItem(responseOrder.id, OrderItem(BigDecimal(25000)))
                }

            assertEquals(exception.message, "The order is in completed state")
        }

    @Test
    fun `진행중인 주문에 상품을 추가하면, 추가한 상품 가격이 주문 아이템에 반영 되어야 한다`() =
        runBlocking {
            val orderItem = OrderItem(BigDecimal(23000))

            service.addOrderItem(responseOrder.id, orderItem)

            assertEquals(responseOrder.orderItems.last().price, orderItem.price)
        }

    @Test
    fun `주문 완료 요청 시, 주문 상태는 완료 처리가 되어야 한다`() =
        runBlocking {
            assertEquals(responseOrder.status, OrderStatus.CREATED)

            service.completeOrder(responseOrder.id)

            assertEquals(responseOrder.status, OrderStatus.COMPLETED)
        }

    @Test
    fun `이미 완료된 주문에 대하여 주문 완료 요청 시, 올바른 메시지와 함께 BadRequestException이 발생해야 한다`(): Unit =
        runBlocking {
            responseOrder.status = OrderStatus.COMPLETED

            val exception =
                assertFailsWith<BadRequestException> {
                    service.completeOrder(responseOrder.id)
                }

            assertEquals(exception.message, "The order is in completed state")
        }

    @Test
    fun `진행중인 주문건에 등록된 주문 아이템 제거 요청 시, 제거한 상품의 가격만큼 주문 가격이 차감 되어야 한다`() =
        runBlocking {
            service.deleteOrderItem(responseOrder.id, responseOrder.orderItems.last().id)

            assertEquals(responseOrder.price, BigDecimal(0))
        }

    @Test
    fun `이미 완료된 주문에 대하여 아이템 제거 요청 시, 올바른 메시지와 함께 BadRequestException이 발생해야 한다`(): Unit =
        runBlocking {
            responseOrder.status = OrderStatus.COMPLETED

            val exception =
                assertFailsWith<BadRequestException> {
                    service.deleteOrderItem(responseOrder.id, responseOrder.orderItems.last().id)
                }

            assertEquals(exception.message, "The order is in completed state")
        }

    @Test
    fun `진행중인 주문건에 등록되지 않은 아이템 제거 요청 시, 올바른 메시지와 함께 NotFoundException 에러가 발생해야 한다`(): Unit =
        runBlocking {
            val invalidOrderItemId = UUID.randomUUID()

            val exception =
                assertFailsWith<NotFoundException> {
                    service.deleteOrderItem(responseOrder.id, invalidOrderItemId)
                }

            assertEquals(exception.message, "Can not found order item by id($invalidOrderItemId)")
        }
}

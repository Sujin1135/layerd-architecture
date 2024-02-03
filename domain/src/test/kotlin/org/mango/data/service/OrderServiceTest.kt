package org.mango.data.service

import io.github.serpro69.kfaker.Faker
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
import org.mango.data.model.Product
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
    private val faker: Faker = Faker()
    private val responseOrder = Order(UUID.randomUUID())

    @BeforeEach
    fun setup(): Unit =
        runBlocking {
            MockKAnnotations.init(this@OrderServiceTest)

            responseOrder.addOrder(Product(faker.name.name(), BigDecimal(25000)))

            coEvery { repository.findById(responseOrder.id) } returns responseOrder
            coEvery { repository.save(any()) } just Runs
        }

    @Test
    fun `주문받은 상품에 대한 주문 데이터 생성 요청 후, 상품의 가격에 해당하는 데이터가 생성 되어야 한다`() =
        runBlocking {
            val price = BigDecimal(30000)
            val product = Product(faker.name.name(), price)
            val createdId = service.createOrder(product)
            val order = Order()
            order.addOrder(product)

            coEvery { repository.findById(createdId) } returns order

            val sut = repository.findById(createdId)

            assertEquals(sut?.price, price)
        }

    @Test
    fun `진행중인 주문에 상품을 추가하면, 추가한 상품 가격이 주문 가격에 반영 되어야 한다`() =
        runBlocking {
            val price = BigDecimal(23000)
            val product = Product(faker.name.name(), price)
            val beforeResponseOrderPrice = responseOrder.price

            service.addProduct(responseOrder.id, product)

            assertEquals(responseOrder.price, beforeResponseOrderPrice + product.price)
        }

    @Test
    fun `이미 완료된 주문에 상품을 추가하면, 올바른 메시지와 함께 BadRequestException이 발생해야 한다`(): Unit =
        runBlocking {
            responseOrder.status = OrderStatus.COMPLETED

            val exception =
                assertFailsWith<BadRequestException> {
                    service.addProduct(responseOrder.id, Product(faker.name.name(), BigDecimal(25000)))
                }

            assertEquals(exception.message, "The order is in completed state")
        }

    @Test
    fun `진행중인 주문에 상품을 추가하면, 추가한 상품 가격이 주문 아이템에 반영 되어야 한다`() =
        runBlocking {
            val price = BigDecimal(23000)
            val product = Product(faker.name.name(), price)

            service.addProduct(responseOrder.id, product)

            assertEquals(responseOrder.orderItems.last().price, product.price)
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
            service.deleteProduct(responseOrder.id, responseOrder.orderItems.last().id)

            assertEquals(responseOrder.price, BigDecimal(0))
        }

    @Test
    fun `이미 완료된 주문에 대하여 아이템 제거 요청 시, 올바른 메시지와 함께 BadRequestException이 발생해야 한다`(): Unit =
        runBlocking {
            responseOrder.status = OrderStatus.COMPLETED

            val exception =
                assertFailsWith<BadRequestException> {
                    service.deleteProduct(responseOrder.id, responseOrder.orderItems.last().id)
                }

            assertEquals(exception.message, "The order is in completed state")
        }

    @Test
    fun `진행중인 주문건에 등록되지 않은 아이템 제거 요청 시, 올바른 메시지와 함께 NotFoundException 에러가 발생해야 한다`(): Unit =
        runBlocking {
            val invalidOrderItemId = UUID.randomUUID()

            val exception =
                assertFailsWith<NotFoundException> {
                    service.deleteProduct(responseOrder.id, invalidOrderItemId)
                }

            assertEquals(exception.message, "Can not found order item by id($invalidOrderItemId)")
        }
}

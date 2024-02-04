package org.mango.data.repository

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mango.data.exception.NotFoundException
import org.mango.data.model.Order
import org.mango.data.model.OrderItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.math.BigDecimal
import java.util.UUID

@TestPropertySource(properties = ["spring.r2dbc.initialization-mode=never"])
@Testcontainers
@SpringBootTest
class MySQLOrderRepositoryTest(
    @Autowired private val repository: MySQLOrderRepository,
) {
    private var initOrder = Order()

    companion object {
        val dockerServiceName = "mysql-test"
        val dockerServicePort = 3306
        val databaseName = "mango"
        val container =
            DockerComposeContainer(File("src/test/resources/db/migration/docker-compose.yaml"))
                .withExposedService(dockerServiceName, dockerServicePort)
                .apply { start() }

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                "r2dbc:mysql://${container.getServiceHost(
                    "mysql-test",
                    dockerServicePort,
                )}:${container.getServicePort(dockerServiceName, dockerServicePort)}/$databaseName"
            }
            registry.add("spring.r2dbc.username") { "root" }
            registry.add("spring.r2dbc.password") { "root" }
        }
    }

    @BeforeEach
    fun setup(): Unit =
        runBlocking {
            initOrder.addOrder(OrderItem(BigDecimal.TWO))
            repository.save(initOrder)
        }

    @Test
    fun `기존 저장된 주문 데이터 조회 시, 저장된 객체와 동일한 값이 반환 되어야 한다`() =
        runBlocking {
            val sut = findOneByOrderId(initOrder.id)

            assertEquals(sut.price, initOrder.price)
            assertEquals(sut.orderItems.size, initOrder.orderItems.size)

            sut.orderItems.forEachIndexed { index, it -> assertEquals(initOrder.orderItems[index].price, it.price) }
        }

    @Test
    fun `초기 주문 데이터를 저장하면 uuid, 가격 정보가 저장되고 해당하는 아이템 객체가 추가 되어야 하고 uuid로 조회 시 저장한 데이터가 반환 되어야 한다`() =
        runBlocking {
            val order = Order()
            order.addOrder(OrderItem(BigDecimal(5000)))

            repository.save(order)

            val sut = findOneByOrderId(order.id)

            assertEquals(sut.price, order.price)
            assertEquals(sut.orderItems.size, 1)
            assertEquals(sut.orderItems[0].price, order.price)
        }

    @Test
    fun `기존 저장된 데이터를 다시 저장하면 원하는대로 데이터가 수정 되어야 한다`() =
        runBlocking {
            initOrder.addOrder(OrderItem(BigDecimal.TEN))

            repository.save(initOrder)

            val sut = findOneByOrderId(initOrder.id)

            assertEquals(sut.price, initOrder.price)
            assertEquals(sut.orderItems.size, initOrder.orderItems.size)
            assertEquals(sut.orderItems.last().price, initOrder.orderItems.last().price)
        }

    private suspend fun findOneByOrderId(id: UUID) =
        repository.findById(id) ?: throw NotFoundException(
            "failed to find order entity by uuid($id)",
        )
}

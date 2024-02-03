package org.mango.data.repository

import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mango.data.entity.KeyValueEntity
import org.mango.data.model.KeyValueInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File

@TestPropertySource(properties = ["spring.r2dbc.initialization-mode=never"])
@Testcontainers
@SpringBootTest
class MySQLValueRepositoryTest(
    @Autowired private val repository: MySQLKeyValueRepository,
    @Autowired private val r2dbRepository: R2DBKeyValueRepository,
) {
    private val faker: Faker = Faker()
    private val key = faker.name.nameWithMiddle()
    private val value = faker.address.streetAddress()

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
            r2dbRepository.delete(KeyValueEntity(key, value))
            repository.upsert(KeyValueInput(key, value))
        }

    @Test
    fun `저장 되어있는 데이터의 key값으로 상세조회 요청 시, 생성된 데이터값과 일치하는 데이터가 조회 되어야 한다`() =
        runBlocking {
            val sut = repository.findOne(key)

            assertEquals(sut?.key, key)
            assertEquals(sut?.value, value)
        }

    @Test
    fun `저장 되어있지 않은 객체 조회 시, null값이 반환 되어야 한다`() =
        runBlocking {
            val sut = repository.findOne("invalid_key")

            assertNull(sut)
        }

    @Test
    fun `저장 되어있는 key값을 다른 value로 upsert 요청 시, 다른 value로 update 되어야 한다`() =
        runBlocking {
            val newValue = faker.address.streetAddress()
            val sut = repository.upsert(KeyValueInput(key, newValue))

            assertEquals(sut.key, key)
            assertNotEquals(sut.value, value)
            assertEquals(sut.value, newValue)
        }
}

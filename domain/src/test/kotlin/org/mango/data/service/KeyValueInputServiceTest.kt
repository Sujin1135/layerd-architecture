package org.mango.data.service

import io.github.serpro69.kfaker.Faker
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mango.data.DomainTestConfig
import org.mango.data.model.KeyValueInput
import org.mango.data.model.KeyValueResponse
import org.mango.data.repository.KeyValueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [DomainTestConfig::class])
@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
class KeyValueInputServiceTest(
    @Autowired @Qualifier("keyValueService") private val service: KeyValueService,
    @Autowired private val repository: KeyValueRepository,
) {
    private val faker: Faker = Faker()
    private val key = faker.name.nameWithMiddle()
    private val value = faker.address.streetAddress()
    private val keyValueResponse = KeyValueResponse(key, value)
    private val keyValueInput = KeyValueInput(key, value)

    @BeforeEach
    fun setup(): Unit =
        runBlocking {
            MockKAnnotations.init(this@KeyValueInputServiceTest)

            coEvery { repository.findOne(key) } returns keyValueResponse
            coEvery { repository.upsert(keyValueInput) } answers { keyValueResponse }
        }

    @Test
    fun `입력받은 key에 해당하는 key, value 데이터가 반환된다`() =
        runBlocking {
            val sut = service.get(key)

            assertEquals(sut.key, key)
        }

    @Test
    fun `입력받은 key에 해당하는 데이터가 없을 경우 입력한 key값과 null value가 반환된다`() =
        runBlocking {
            val invalidKey = "invalid_key"

            coEvery { repository.findOne(invalidKey) } returns null

            val sut = service.get(invalidKey)

            assertEquals(sut.key, invalidKey)
            assertEquals(sut.value, null)
        }

    @Test
    fun `입력받은 key, value 데이터 저장 이후, 요청했던 key, value 데이터가 반환 되어야 한다`() =
        runBlocking {
            val sut = service.save(keyValueInput)

            assertEquals(sut.key, keyValueInput.key)
            assertEquals(sut.value, keyValueInput.value)
        }

    @Test
    fun `이미 생성되어있는 key 데이터에 대해 다른 value값으로 중복 save 요청 시, 마지막 요청한 value 값이 반환된다`() =
        runBlocking {
            service.save(keyValueInput)

            val value2 = "test_value2"
            val input2 = KeyValueInput(key, value2)

            coEvery { repository.upsert(input2) } answers { KeyValueResponse(input2.key, input2.value) }

            val sut = service.save(input2)

            assertEquals(input2.key, sut.key)
            assertEquals(input2.value, sut.value)
        }
}

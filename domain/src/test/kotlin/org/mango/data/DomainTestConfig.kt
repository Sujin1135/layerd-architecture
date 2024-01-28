package org.mango.data

import io.mockk.mockk
import org.mango.data.repository.KeyValueRepository
import org.mango.data.service.KeyValueService
import org.mango.data.service.UserKeyValueService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean


@TestConfiguration
class DomainTestConfig {
    @Bean
    fun keyValueRepository(): KeyValueRepository {
        return mockk(relaxed = true)
    }

    @Bean
    fun keyValueService(repository: KeyValueRepository): KeyValueService {
        return UserKeyValueService(repository)
    }
}

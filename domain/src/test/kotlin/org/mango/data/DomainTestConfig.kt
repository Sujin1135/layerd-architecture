package org.mango.data

import io.mockk.mockk
import org.mango.data.repository.KeyValueRepository
import org.mango.data.repository.OrderRepository
import org.mango.data.service.DomainOrderService
import org.mango.data.service.KeyValueService
import org.mango.data.service.OrderService
import org.mango.data.service.UserKeyValueService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.transaction.ReactiveTransactionManager

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

    @Bean
    fun OrderRepository(): OrderRepository {
        return mockk(relaxed = true)
    }

    @Bean
    fun ReactiveTransactionManager(): ReactiveTransactionManager {
        return mockk(relaxed = true)
    }

    @Bean
    fun OrderService(
        repository: OrderRepository,
        transactionManager: ReactiveTransactionManager,
    ): OrderService {
        return DomainOrderService(repository, transactionManager)
    }
}

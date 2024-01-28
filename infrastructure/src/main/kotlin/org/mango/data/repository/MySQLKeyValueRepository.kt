package org.mango.data.repository

import org.mango.data.entity.KeyValueEntity
import org.mango.data.model.KeyValueInput
import org.mango.data.model.KeyValueResponse
import org.springframework.stereotype.Service

@Service
class MySQLKeyValueRepository(private val repository: R2DBKeyValueRepository) : KeyValueRepository {
    override suspend fun findOne(key: String): KeyValueResponse? {
        val entity: KeyValueEntity? = repository.findOneByKey(key)

        return if (entity != null) KeyValueResponse(entity.key, entity.value) else null
    }

    override suspend fun upsert(keyValue: KeyValueInput): KeyValueResponse {
        try {
            repository.upsert(keyValue.key, keyValue.value)
            return KeyValueResponse(keyValue.key, keyValue.value)
        } catch (e: Exception) {
            println(e.message)
            throw e
        }
    }
}
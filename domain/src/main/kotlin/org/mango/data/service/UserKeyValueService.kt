package org.mango.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mango.data.model.KeyValueInput
import org.mango.data.model.KeyValueResponse
import org.mango.data.repository.KeyValueRepository
import org.springframework.stereotype.Service

@Service
class UserKeyValueService(private val repository: KeyValueRepository) : KeyValueService {
    override suspend fun get(key: String): KeyValueResponse {
        val result =
            withContext(Dispatchers.IO) {
                repository.findOne(key)
            }
        return if (result != null) {
            KeyValueResponse(result.key, result.value)
        } else {
            KeyValueResponse(key, null)
        }
    }

    override suspend fun save(data: KeyValueInput): KeyValueResponse {
        return withContext(Dispatchers.IO) {
            repository.upsert(data)
        }
    }
}

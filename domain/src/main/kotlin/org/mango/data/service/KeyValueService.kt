package org.mango.data.service

import org.mango.data.model.KeyValueInput
import org.mango.data.model.KeyValueResponse

interface KeyValueService {
    suspend fun get(key: String): KeyValueResponse
    suspend fun save(data: KeyValueInput): KeyValueResponse
}
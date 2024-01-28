package org.mango.data.repository

import org.mango.data.model.KeyValueInput
import org.mango.data.model.KeyValueResponse

interface KeyValueRepository {
    suspend fun findOne(key: String): KeyValueResponse?
    suspend fun upsert(keyValue: KeyValueInput): KeyValueResponse
}
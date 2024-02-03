package org.mango.data.repository

import org.mango.data.entity.KeyValueEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface R2DBKeyValueRepository : CoroutineCrudRepository<KeyValueEntity, UInt> {
    @Query("SELECT * FROM key_values WHERE key_values.key = :key")
    suspend fun findOneByKey(key: String): KeyValueEntity?

    @Query(
        "INSERT INTO key_values (key_values.key, key_values.value) VALUES (:key, :value) ON DUPLICATE KEY UPDATE key_values.value = :value",
    )
    suspend fun upsert(
        key: String,
        value: String,
    )
}

package org.mango.data.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.mango.data.GetRequest
import org.mango.data.GetResponse
import org.mango.data.KeyValueServiceGrpcKt
import org.mango.data.SaveRequest
import org.mango.data.SaveResponse
import org.mango.data.model.KeyValueInput
import org.mango.data.service.KeyValueService

@GrpcService
class KeyValueGrpcService(private val service: KeyValueService) : KeyValueServiceGrpcKt.KeyValueServiceCoroutineImplBase() {
    override suspend fun get(request: GetRequest): GetResponse {
        val keyValue = service.get(request.key)
        return GetResponse.newBuilder().apply {
            key = keyValue.key
            if (keyValue.value != null) {
                value = keyValue.value
            }
        }.build()
    }

    override suspend fun save(request: SaveRequest): SaveResponse {
        val keyValue = service.save(KeyValueInput(request.key, request.value))
        return SaveResponse.newBuilder().apply {
            key = keyValue.key
            value = keyValue.value
        }.build()
    }
}
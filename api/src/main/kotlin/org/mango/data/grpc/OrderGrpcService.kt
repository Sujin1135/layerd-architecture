package org.mango.data.grpc

import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import org.mango.data.AddProductRequest
import org.mango.data.CompleteOrderRequest
import org.mango.data.CreateOrderResponse
import org.mango.data.DeleteProductRequest
import org.mango.data.OrderServiceGrpcKt
import org.mango.data.ProductRequest
import org.mango.data.model.Product
import org.mango.data.service.OrderService
import java.math.BigDecimal
import java.util.UUID

@GrpcService
class OrderGrpcService(private val service: OrderService) : OrderServiceGrpcKt.OrderServiceCoroutineImplBase() {
    override suspend fun createOrder(request: ProductRequest): CreateOrderResponse {
        val product = Product(request.name, BigDecimal(request.price))
        val uuidStr = service.createOrder(product).toString()

        return CreateOrderResponse.newBuilder().apply {
            id = uuidStr
        }.build()
    }

    override suspend fun addProduct(request: AddProductRequest): Empty {
        service.addProduct(
            UUID.fromString(request.id),
            Product(request.product.name, BigDecimal(request.product.price)),
        )

        return Empty.newBuilder().build()
    }

    override suspend fun completeOrder(request: CompleteOrderRequest): Empty {
        service.completeOrder(UUID.fromString(request.id))

        return Empty.newBuilder().build()
    }

    override suspend fun deleteProduct(request: DeleteProductRequest): Empty {
        service.deleteProduct(UUID.fromString(request.id), UUID.fromString(request.productId))

        return Empty.newBuilder().build()
    }
}

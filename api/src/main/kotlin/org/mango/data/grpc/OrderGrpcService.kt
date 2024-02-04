package org.mango.data.grpc

import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import org.mango.data.AddOrderItemRequest
import org.mango.data.CompleteOrderRequest
import org.mango.data.CreateOrderResponse
import org.mango.data.DeleteOrderItemRequest
import org.mango.data.OrderItemRequest
import org.mango.data.OrderServiceGrpcKt
import org.mango.data.model.OrderItem
import org.mango.data.service.OrderService
import java.math.BigDecimal
import java.util.UUID

@GrpcService
class OrderGrpcService(private val service: OrderService) : OrderServiceGrpcKt.OrderServiceCoroutineImplBase() {
    override suspend fun createOrder(request: OrderItemRequest): CreateOrderResponse {
        val orderItem = OrderItem(BigDecimal(request.price))
        val uuidStr = service.createOrder(orderItem).toString()

        return CreateOrderResponse.newBuilder().apply {
            id = uuidStr
        }.build()
    }

    override suspend fun addOrderItem(request: AddOrderItemRequest): Empty {
        service.addOrderItem(
            UUID.fromString(request.id),
            OrderItem(BigDecimal(request.orderItem.price)),
        )

        return Empty.newBuilder().build()
    }

    override suspend fun completeOrder(request: CompleteOrderRequest): Empty {
        service.completeOrder(UUID.fromString(request.id))

        return Empty.newBuilder().build()
    }

    override suspend fun deleteOrderItem(request: DeleteOrderItemRequest): Empty {
        service.deleteOrderItem(UUID.fromString(request.id), UUID.fromString(request.orderItemId))

        return Empty.newBuilder().build()
    }
}

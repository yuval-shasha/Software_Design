package il.ac.technion.cs.sd.buy.lib

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Product(val type: String,
                   val id: String,
                   val price: Int) {}

@Serializable
open class Order(open val type: String,
                 @SerialName("order-id") open val orderId: String,
                 @SerialName("user-id") open val userId: String?,
                 @SerialName("product-id") open val productId: String?,
                 open val amount: Int?) {}

data class CreateOrder(
    override val type: String,
    override val orderId: String,
    override val userId: String,
    override val productId: String,
    override val amount: Int)
    : Order(type, orderId, userId, productId, amount) {}

data class ModifyOrder(
    override val type: String,
    override val orderId: String,
    override val amount: Int)
    : Order(type, orderId, null, null, amount) {}

data class CancelOrder(
    override val type: String,
    override val orderId: String)
    : Order(type, orderId, null, null, null) {}

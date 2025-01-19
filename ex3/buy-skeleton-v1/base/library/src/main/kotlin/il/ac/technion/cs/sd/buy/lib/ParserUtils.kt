package il.ac.technion.cs.sd.buy.lib

import com.gitlab.mvysny.konsumexml.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

interface Order {
    val type: String
    @SerialName("order-id") val id: String
    @SerialName("user-id") val idUser: String?
    @SerialName("product-id") val idProduct: String?
    val amountProduct: Int?
}

@Serializable
data class Product(val type: String, val id: String, val price: Int) : KeyValueElement(type, id, price) {

}

@Serializable
data class CreateOrder(@SerialName("type") val orderType: String, @SerialName("order-id") val orderId: String, @SerialName("user-id") val userId: String, @SerialName("product-id") val productId: String, val amount: Int) : KeyThreeValuesElement(orderType, orderId, userId, productId, amount), Order {
    override val type: String
        get() = orderType
    override val id: String
        get() = orderId
    override val idUser: String
        get() = userId
    override val idProduct: String
        get() = productId
    override val amountProduct: Int
        get() = amount
    
//    override fun json(jsonString: String): CreateOrder {
//        // TODO: implement
//    }
}

@Serializable
data class ModifyOrder(val orderType: String, val orderId: String, val amount: Int) : KeyValueElement(orderType, orderId, amount), Order {
    override val type: String
        get() = orderType
    override val id: String
        get() = orderId
    override val idUser: String?
        get() = null
    override val idProduct: String?
        get() = null
    override val amountProduct: Int
        get() = amount

//    override fun json(jsonString: String): ModifyOrder {
//        // TODO: implement
//    }
}

@Serializable
data class CancelOrder(val orderType: String, val orderId: String) : KeyElement(orderType, orderId), Order {
    override val type: String
        get() = orderType
    override val id: String
        get() = orderId
    override val idUser: String?
        get() = null
    override val idProduct: String?
        get() = null
    override val amountProduct: Int?
        get() = null

//    override fun json(jsonString: String): CancelOrder {
//        // TODO: implement
//    }
}

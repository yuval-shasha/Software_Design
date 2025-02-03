package il.ac.technion.cs.sd.buy.app

import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataLists
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val CREATE_ORDER_TYPE = "order"
const val MODIFY_ORDER_TYPE = "modify-order"
const val CANCEL_ORDER_AMOUNT = "-1"

@Serializable
@Polymorphic
open class Order(open val type: String,
                 @SerialName("order-id") open val orderId: String,
                 @SerialName("user-id") open var userId: String,
                 @SerialName("product-id") open var productId: String,
                 open var amount: Int) {

    fun isCreated(ordersList: List<Order>) : Boolean {
        val lastCreatedOrder = ordersList
            .findLast { it.orderId == this.orderId && it.type == CREATE_ORDER_TYPE }
        return lastCreatedOrder != null
    }

    fun getAmountHistory(ordersList: List<Order>) : List<String> {
        val amountHistoryList = mutableListOf<String>()
        val ordersWithSameId = ordersList.filter { it.orderId == this.orderId }
        val lastCreatedOrderIndex = ordersWithSameId.indexOf(ordersWithSameId.findLast { it.type == CREATE_ORDER_TYPE })

        amountHistoryList.addLast(ordersWithSameId[lastCreatedOrderIndex].amount.toString())

        for (index in lastCreatedOrderIndex + 1 until ordersWithSameId.size) {
            if (ordersWithSameId[index].type == MODIFY_ORDER_TYPE) {
                if (amountHistoryList.last() == CANCEL_ORDER_AMOUNT) {
                    amountHistoryList.removeLast()
                }
                amountHistoryList.addLast(ordersWithSameId[index].amount.toString())
            }
            else if (amountHistoryList.last() != CANCEL_ORDER_AMOUNT) {
                amountHistoryList.addLast(CANCEL_ORDER_AMOUNT)
            }
        }
        return amountHistoryList
    }

    fun isOrderValid(ordersList: List<Order>, productsList: List<Product>) : Boolean {
        val product = productsList.find { it.id == productId }
        return isCreated(ordersList) && (product != null)
    }

    fun getOrderAsStorageLibraryElement(ordersList: List<Order>) : KeyWithTwoDataLists {
        val orderData = "$userId $productId"
        val orderHistoryList = getAmountHistory(ordersList)
        return KeyWithTwoDataLists(orderId, orderData, orderHistoryList)
    }
}


class CreateOrder(override val type: String,
                  @SerialName("order-id") override val orderId: String,
                  @SerialName("user-id") override var userId: String,
                  @SerialName("product-id") override var productId: String,
                  @SerialName("amount") override var amount: Int)
    : Order(type, orderId, userId, productId, amount)


class ModifyOrder(override val type: String,
                  @SerialName("order-id") override val orderId: String,
                  @SerialName("user-id") override var userId: String,
                  @SerialName("product-id") override var productId: String,
                  @SerialName("amount") override var amount: Int)
    : Order(type, orderId, "", "", amount)


class CancelOrder(override var type: String,
                  @SerialName("order-id") override val orderId: String,
                  @SerialName("user-id") override var userId: String,
                  @SerialName("product-id") override var productId: String,
                  @SerialName("amount") override var amount: Int)
    : Order(type, orderId, "", "", 0)
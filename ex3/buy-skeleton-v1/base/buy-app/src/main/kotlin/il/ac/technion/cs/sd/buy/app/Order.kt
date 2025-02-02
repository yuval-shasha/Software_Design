package il.ac.technion.cs.sd.buy.app

import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataLists
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val CREATE_ORDER_TYPE = "order"
const val MODIFY_ORDER_TYPE = "modify-order"
const val CANCEL_ORDER_TYPE = "cancel-order"
const val CANCEL_ORDER_AMOUNT = "-1"
const val CANCELLED_ORDER_MODE = "C"
const val CREATED_ORDER_MODE = "I"
const val MODIFIED_ORDER_MODE = "M"
const val MODIFIED_CANCELLED_ORDER_MODE = "MC"

@Serializable
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

    fun isCancelled(ordersList: List<Order>) : Boolean {
        val lastOrderWithSameId = ordersList
            .findLast { it.orderId == this.orderId }

        return lastOrderWithSameId != null && lastOrderWithSameId.type == CANCEL_ORDER_TYPE
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
        val userProductList = listOf(userId, productId)
        val orderHistoryList = getAmountHistory(ordersList)
        return KeyWithTwoDataLists(orderId, userProductList, orderHistoryList)
    }

    fun getOrderMode(ordersList: List<Order>) : String {
        val amountHistoryList = getAmountHistory(ordersList)
        if (amountHistoryList.last() == CANCEL_ORDER_AMOUNT && amountHistoryList.size == 2) {
            return CANCELLED_ORDER_MODE
        }
        else if (amountHistoryList.last() == CANCEL_ORDER_AMOUNT && amountHistoryList.size > 2) {
            return MODIFIED_CANCELLED_ORDER_MODE
        }
        else if (amountHistoryList.last() != CANCEL_ORDER_AMOUNT && amountHistoryList.size == 1) {
            return CREATED_ORDER_MODE
        }
        else {
            return MODIFIED_ORDER_MODE
        }
    }

    fun getProductPrice(productsList: List<Product>) : Int? {
        return productsList.find { it.id == this.productId }?.price
    }
}

class CreateOrder(override val type: String,
                  override val orderId: String,
                  override var userId: String,
                  override var productId: String,
                  override var amount: Int)
    : Order(type, orderId, userId, productId, amount)

class ModifyOrder(override val type: String,
                  override val orderId: String,
                  override var amount: Int)
    : Order(type, orderId, "", "", amount)

class CancelOrder(override var type: String,
                  override var orderId: String)
    : Order(type, orderId, "", "", 0)
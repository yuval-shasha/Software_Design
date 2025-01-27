package il.ac.technion.cs.sd.buy.app

import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataLists
import kotlinx.serialization.Serializable
import kotlin.collections.component1
import kotlin.collections.component2

@Serializable
class Product(val type: String, val id: String, val price: Int) {
    private fun getOrdersOfProductWithAmountAsMap(ordersList: List<Order>) : Map<Order, Int> {
        return ordersList
            .asSequence()
            .filter { it.productId == this.id }
            .associateWith { order ->
                if (order.isCancelled(ordersList)) -1 else order.amount
            }
    }

    private fun getUserIdsOrderedProductWithAmountAsMap(ordersList: List<Order>) : Map<String, Int> {
        return getOrdersOfProductWithAmountAsMap(ordersList)
            .asSequence()
            .filter { !it.key.isCancelled(ordersList) }
            .map { (order, amount) -> order.userId to amount }
            .toMap()
    }

    fun getProductAsStorageLibraryElement(ordersList: List<Order>) : KeyWithTwoDataLists {
        val usersOrderedProductWithAmountList = getUserIdsOrderedProductWithAmountAsMap(ordersList)
            .map { (userId, amount) ->
                "$userId $amount"
            }
            .toList()

        val ordersOfProductWithAmountList = getOrdersOfProductWithAmountAsMap(ordersList)
            .map { (order, amount) ->
                "${order.orderId} $amount"
            }
            .toList()

        return KeyWithTwoDataLists(id, usersOrderedProductWithAmountList, ordersOfProductWithAmountList)
    }
}
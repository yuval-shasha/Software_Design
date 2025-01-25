package il.ac.technion.cs.sd.buy.app

import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataLists
import kotlinx.serialization.Serializable
import kotlin.collections.component1
import kotlin.collections.component2

@Serializable
class Product(val type: String, val id: String, val price: Int) {
    fun getProductAsStorageLibraryElement(ordersList: List<Order>) : KeyWithTwoDataLists {
        val usersOrderedProductWithAmountList = getUserIdsOrderedProductWithAmountAsMap(ordersList)
            .map { (userId, amount) ->
                "$userId $amount"
            }
            .toList()

        val ordersOfProductWithAmountList = getOrderIdsOfProductWithAmountAsMap(ordersList)
            .map { (orderId, amount) ->
                "$orderId $amount"
            }
            .toList()

        return KeyWithTwoDataLists(id, usersOrderedProductWithAmountList, ordersOfProductWithAmountList)
    }

    private fun getOrderIdsOfProductWithAmountAsMap(ordersList: List<Order>) : Map<String, Int> {
        TODO("Create a map of order ids that contain the product (including cancelled ones) + amount (-1 for cancelled orders")
    }

    private fun getUserIdsOrderedProductWithAmountAsMap(ordersList: List<Order>) : Map<String, Int> {
        TODO("Create a map of user ids that ordered the product (not including cancelled orderes) + amount")
    }
}
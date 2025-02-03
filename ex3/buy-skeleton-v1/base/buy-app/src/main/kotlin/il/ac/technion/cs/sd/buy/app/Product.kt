package il.ac.technion.cs.sd.buy.app

import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataLists
import kotlinx.serialization.Serializable

@Serializable
class Product(val type: String, val id: String, val price: Int) {

    fun getProductAsStorageLibraryElement(ordersList: List<Order>) : KeyWithTwoDataLists {
        val idsOfOrdersContainingProduct = ordersList
            .asSequence()
            .filter { it.productId == this.id }
            .map { order -> order.orderId }
            .toList()

        return KeyWithTwoDataLists(id, price.toString(), idsOfOrdersContainingProduct)
    }
}
package il.ac.technion.cs.sd.buy.app

import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataElements
import kotlinx.serialization.Serializable

@Serializable
class Product(val type: String, val id: String, val price: Int) {

    fun getProductAsStorageLibraryElement(ordersList: List<Order>) : KeyWithTwoDataElements {
        val createdOrders = ordersList
            .asSequence()
            .filter { it.type == "order" }
            .toList()

        if (createdOrders.isEmpty())
        {
            return KeyWithTwoDataElements(id, price.toString(), emptyList())
        }
        val idsOfOrdersContainingProduct = createdOrders
            .associateBy { it.orderId }
            .map { (orderId, order) -> order }
            .filter { it.productId == this.id }
            .map { order -> order.orderId }
            .toList()

        return KeyWithTwoDataElements(id, price.toString(), idsOfOrdersContainingProduct)
    }
}
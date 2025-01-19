package il.ac.technion.cs.sd.buy.lib

import com.gitlab.mvysny.konsumexml.*

class XMLParser
{
    companion object
    {
        fun parseXMLFileToProductList(xmlString: String) : List<Product>
        {
            return xmlString.konsumeXml().use { konsumer ->
                konsumer.child("Root") {
                    this.children("Product") {
                        Product("product", konsumer.childText("id"), konsumer.childText("price").toInt())
                    }
                }
            }
        }

        fun parseXMLFileToOrderList(xmlString: String) : List<Order>
        {
            return xmlString.konsumeXml().use { konsumer ->
                konsumer.child("Root") {
                    while (hasNext())
                    {
                        val newOrder = when (this.tagName) {
                            "Order" -> CreateOrder(
                                "order",
                                konsumer.childText("order-id"),
                                konsumer.childText("user-id"),
                                konsumer.childText("product-id"),
                                konsumer.childText("amount").toInt())

                            "ModifyOrder" -> ModifyOrder(
                                "modify-order",
                                konsumer.childText("order-id"),
                                konsumer.childText("new-amount").toInt())

                            "CancelOrder" -> CancelOrder(
                                "cancel-order",
                                konsumer.childText("order-id"))

                            else -> null
                        }
                    }
                }
            }
        }
    }
}
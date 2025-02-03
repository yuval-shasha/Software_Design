package il.ac.technion.cs.sd.buy.app

import com.gitlab.mvysny.konsumexml.Names
import com.gitlab.mvysny.konsumexml.allChildrenAutoIgnore
import com.gitlab.mvysny.konsumexml.konsumeXml

class XMLParser : Parser {
    override fun parseFileToProductsList(xmlString: String): List<Product> {
        val productsList = mutableListOf<Product>()

        xmlString.konsumeXml().use { konsumer ->
            konsumer.child("Root") {
                allChildrenAutoIgnore(Names.Companion.of("Product")) {
                    productsList.add(
                        Product(
                            type = "product",
                            id = childText("id"),
                            price = childText("price").toInt()
                        )
                    )
                }
            }
        }

        return productsList
    }

    override fun parseFileToOrdersList(xmlString: String): List<Order> {
        val ordersList = mutableListOf<Order>()

        xmlString.konsumeXml().use { konsumer ->
            konsumer.child("Root") {
                allChildrenAutoIgnore(Names.Companion.of("Order", "ModifyOrder", "CancelOrder")) {
                    when (localName) {
                        "Order" -> ordersList.add(
                            CreateOrder(
                                type = "order",
                                userId = childText("user-id"),
                                orderId = childText("order-id"),
                                productId = childText("product-id"),
                                amount = childText("amount").toInt()
                            )
                        )

                        "ModifyOrder" -> ordersList.add(
                            ModifyOrder(
                                type = "modify-order",
                                userId = "",
                                orderId = childText("order-id"),
                                productId = "",
                                amount = childText("new-amount").toInt()
                            )
                        )

                        "CancelOrder" -> ordersList.add(
                            CancelOrder(
                                type = "cancel-order",
                                userId = "",
                                orderId = childText("order-id"),
                                productId = "",
                                amount = 0
                            )
                        )
                    }
                }
            }
        }

        return ordersList
    }
}
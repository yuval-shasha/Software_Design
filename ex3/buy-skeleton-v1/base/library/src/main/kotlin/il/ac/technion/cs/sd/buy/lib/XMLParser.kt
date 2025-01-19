package il.ac.technion.cs.sd.buy.lib

import com.gitlab.mvysny.konsumexml.*

class XMLParser
{
    companion object {
        fun parseXMLFileToProductList(xmlString: String): List<Product> {
            val productsList = mutableListOf<Product>()

            xmlString.konsumeXml().use { konsumer ->
                konsumer.child("Root") {
                    allChildrenAutoIgnore(Names.of("Product")) {
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

        fun parseXMLFileToOrderList(xmlString: String): List<Order> {
            val ordersList = mutableListOf<Order>()

            xmlString.konsumeXml().use { konsumer ->
                konsumer.child("Root") {
                    allChildrenAutoIgnore(Names.of("Order", "ModifyOrder", "CancelOrder")) {
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
                                    orderId = childText("order-id"),
                                    amount = childText("new-amount").toInt()
                                )
                            )

                            "CancelOrder" -> ordersList.add(
                                CancelOrder(
                                    type = "cancel-order",
                                    orderId = childText("order-id")
                                )
                            )
                        }
                    }
                }
            }

            return ordersList
        }
    }
}
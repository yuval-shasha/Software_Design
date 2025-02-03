package il.ac.technion.cs.sd.buy.app

import kotlinx.serialization.json.*

class JSONParser : Parser {
    override fun parseFileToProductsList(jsonString : String) : List<Product> {
        val json = Json {ignoreUnknownKeys  = true}
        val elements = json.decodeFromString<List<JsonElement>>(jsonString)
        val products = mutableListOf<Product>()
        for (element in elements) {
            val jsonObject = element.jsonObject
            when(jsonObject["type"]?.jsonPrimitive?.content) {
                "product" -> {
                    val product = json.decodeFromJsonElement<Product>(jsonObject)
                    products.add(product)
                }
            }
        }
        return products
    }

    override fun parseFileToOrdersList(jsonString : String) : List<Order> {
        val json = Json {ignoreUnknownKeys  = true}
        val elements = json.decodeFromString<List<JsonElement>>(jsonString)
        val orders = mutableListOf<Order>()
        for (element in elements) {
            val jsonObject = element.jsonObject
            when(jsonObject["type"]?.jsonPrimitive?.content) {
                "order" -> {
                    val order = CreateOrder(jsonObject["type"]!!.jsonPrimitive.content,
                        jsonObject["order-id"]!!.jsonPrimitive.content,
                        jsonObject["user-id"]!!.jsonPrimitive.content,
                        jsonObject["product-id"]!!.jsonPrimitive.content,
                        jsonObject["amount"]!!.jsonPrimitive.int)
                    orders.add(order)
                }
                "modify-order" -> {
                    val order = ModifyOrder(jsonObject["type"]!!.jsonPrimitive.content,
                        jsonObject["order-id"]!!.jsonPrimitive.content,
                        "",
                        "",
                        jsonObject["amount"]!!.jsonPrimitive.int)
                    orders.add(order)
                }
                "cancel-order" -> {
                    val order = CancelOrder(jsonObject["type"]!!.jsonPrimitive.content,
                        jsonObject["order-id"]!!.jsonPrimitive.content,
                        "",
                        "",
                        0)
                    orders.add(order)
                }
            }
        }
        return orders
    }
}
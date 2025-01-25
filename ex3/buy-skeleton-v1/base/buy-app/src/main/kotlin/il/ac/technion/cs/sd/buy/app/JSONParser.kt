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
                    val order = json.decodeFromJsonElement<CreateOrder>(jsonObject)
                    orders.add(order)
                }
                "modifyOrder" -> {
                    val order = json.decodeFromJsonElement<ModifyOrder>(jsonObject)
                    orders.add(order)
                }
                "cancelOrder" -> {
                    val order = json.decodeFromJsonElement<CancelOrder>(jsonObject)
                    orders.add(order)
                }
            }
        }
        return orders
    }
}
package il.ac.technion.cs.sd.buy.lib

import kotlinx.serialization.json.*
import il.ac.technion.cs.sd.buy.lib.Product

class JSONParser {
    companion object {
        fun parseJSONFileToProductList (jsonString : String) : List<Product>
        {
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

        fun parseJSONFileToOrdersList (jsonString : String) : List<Order>
        {
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
}
package il.ac.technion.cs.sd.buy.app

import com.google.inject.Inject
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataLists
import il.ac.technion.cs.sd.buy.lib.StorageLibrary

class BuyProductInitializerImpl @Inject constructor(suspendLineStorageFactory: SuspendLineStorageFactory) : BuyProductInitializer {
    private var productsDB = StorageLibrary(suspendLineStorageFactory, "products")
    private var ordersDB = StorageLibrary(suspendLineStorageFactory, "orders")
    private var usersDB = StorageLibrary(suspendLineStorageFactory, "users")

    private fun createProductsDB(productsList : List<Product>, ordersList : List<Order>) {
        val productsData = mutableListOf<KeyWithTwoDataLists>()

        productsList
            .forEach { product ->
                val elementForOrdersData = product.getProductAsStorageLibraryElement(ordersList)
                productsData.add(elementForOrdersData)
            }

        productsDB.initializeDatabase(productsData)
    }

    private fun getAllValidOrders(productsList : List<Product>, ordersList : List<Order>) : List<Order> {
        return ordersList
            .asSequence()
            .filter { it.isOrderValid(ordersList, productsList) && it.type == CREATE_ORDER_TYPE }
            .associateBy { it.orderId }
            .map { (_, order) -> order }
            .toList()
    }

    private fun createOrdersDB(productsList : List<Product>, ordersList : List<Order>) {
        val ordersData = mutableListOf<KeyWithTwoDataLists>()

        getAllValidOrders(productsList, ordersList)
            .forEach { order ->
                val elementForOrdersData = order.getOrderAsStorageLibraryElement(ordersList)
                ordersData.add(elementForOrdersData)
        }

        ordersDB.initializeDatabase(ordersData)
    }

    private fun getUserAsStorageLibraryElement(userId: String, originalOrdersList: List<Order>, validOrdersList: List<Order>, productsList: List<Product>) : KeyWithTwoDataLists {
        val orderIdAndOrderModeList = validOrdersList
            .map { order ->
                "${order.orderId} ${order.getOrderMode(originalOrdersList)} " }
            .toList()

        val orderDetailsList = validOrdersList
            .map { order ->
                val productPrice = order.getProductPrice(productsList).toString()
                "${order.productId} ${order.amount} $productPrice " }
            .toList()

        return KeyWithTwoDataLists(userId, orderIdAndOrderModeList, orderDetailsList)
    }

    private fun createUsersDB(productsList : List<Product>, ordersList : List<Order>) {
        val usersData = mutableListOf<KeyWithTwoDataLists>()

        getAllValidOrders(productsList, ordersList)
            .asSequence()
            .groupBy { it.userId }
            .forEach { (userId, validOrders) ->
                val elementForUsersData = getUserAsStorageLibraryElement(userId, ordersList, validOrders, productsList)
                usersData.add(elementForUsersData)
            }

        usersDB.initializeDatabase(usersData)
    }

    private fun setup(data: String, parser: Parser) {
        val productsList = parser.parseFileToProductsList(data)
        val ordersList = parser.parseFileToOrdersList(data)
        createProductsDB(productsList, ordersList)
        createOrdersDB(productsList, ordersList)
        createUsersDB(productsList, ordersList)
    }

    /** for testing */
    fun getProductsDB() : StorageLibrary {
        return productsDB
    }

    /** for testing */
    fun getOrdersDB() : StorageLibrary {
        return ordersDB
    }

    /** for testing */
    fun getUsersDB() : StorageLibrary {
        return usersDB
    }

    /** Saves the XML data persistently, so that it could be queried using BuyProductReader */
    override suspend fun setupXml(xmlData: String) {
        setup(xmlData, XMLParser())
    }

    /** Saves the JSON data persistently, so that it could be queried using BuyProductReader */
    override suspend fun setupJson(jsonData: String) {
        setup(jsonData, JSONParser())
    }
}
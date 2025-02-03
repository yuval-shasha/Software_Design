package il.ac.technion.cs.sd.buy.app

import com.google.inject.Inject
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataLists
import il.ac.technion.cs.sd.buy.lib.StorageLibrary

class BuyProductInitializerImpl @Inject constructor(suspendLineStorageFactory: SuspendLineStorageFactory) : BuyProductInitializer {
    /**
     * first line: product id
     * second line: product price
     * third line: list of order ids containing the product
     */
    private var productsDB = StorageLibrary(suspendLineStorageFactory, "products")

    /**
     * first line: order id
     * second line: id of user who made the order and id of product in order
     * third line: list of amount history
     */
    private var ordersDB = StorageLibrary(suspendLineStorageFactory, "orders")

    /**
     * first line: user id
     * second line: empty
     * third line: sorted list of ids of orders that the user made
     */
    private var usersDB = StorageLibrary(suspendLineStorageFactory, "users")

    private suspend fun createProductsDB(productsList : List<Product>, ordersList : List<Order>) {
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

    private suspend fun createOrdersDB(productsList : List<Product>, ordersList : List<Order>) {
        val ordersData = mutableListOf<KeyWithTwoDataLists>()

        getAllValidOrders(productsList, ordersList)
            .forEach { order ->
                val elementForOrdersData = order.getOrderAsStorageLibraryElement(ordersList)
                ordersData.add(elementForOrdersData)
        }

        ordersDB.initializeDatabase(ordersData)
    }

    private fun getUserAsStorageLibraryElement(userId: String, validOrdersByUserList: List<Order>) : KeyWithTwoDataLists {
        val orderIdsByUserList = validOrdersByUserList
            .map { order -> order.orderId }
            .toList()

        return KeyWithTwoDataLists(userId, "", orderIdsByUserList)
    }

    private suspend fun createUsersDB(productsList : List<Product>, ordersList : List<Order>) {
        val usersData = mutableListOf<KeyWithTwoDataLists>()

        getAllValidOrders(productsList, ordersList)
            .asSequence()
            .groupBy { it.userId }
            .forEach { (userId, validOrdersByUserList) ->
                val validOrdersByUserSortedList = validOrdersByUserList.sortedBy { order -> order.orderId }
                val elementForUsersData = getUserAsStorageLibraryElement(userId, validOrdersByUserSortedList)
                usersData.add(elementForUsersData)
            }

        usersDB.initializeDatabase(usersData)
    }

    private suspend fun setup(data: String, parser: Parser) {
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
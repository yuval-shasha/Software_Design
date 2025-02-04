package il.ac.technion.cs.sd.buy.app

import com.google.inject.Inject
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import il.ac.technion.cs.sd.buy.lib.KeyWithTwoDataElements
import il.ac.technion.cs.sd.buy.lib.StorageLibrary

class BuyProductInitializerImpl @Inject constructor(suspendLineStorageFactory: SuspendLineStorageFactory) : BuyProductInitializer {
    /**
     * key line: product id
     * main data line: product price
     * list data line: list of order ids containing the product
     */
    private val productsStorageLibrary = StorageLibrary(suspendLineStorageFactory, "products")

    /**
     * key line: order id
     * main data line: id of user who made the order and id of product in order
     * list data line: list of amount history
     */
    private val ordersStorageLibrary = StorageLibrary(suspendLineStorageFactory, "orders")

    /**
     * key line: user id
     * main data line: empty
     * list data line: sorted list of ids of orders that the user made
     */
    private val usersStorageLibrary = StorageLibrary(suspendLineStorageFactory, "users")

    private suspend fun createProductsStorageLibrary(productsList : List<Product>, ordersList : List<Order>) {
        val productsData = ArrayList<KeyWithTwoDataElements>()

        productsList
            .associateBy { it.id }
            .map { (id , product) -> product }
            .forEach { product ->
                val elementForOrdersData = product.getProductAsStorageLibraryElement(ordersList)
                productsData.add(elementForOrdersData)
            }

        productsStorageLibrary.initializeDatabase(productsData)
    }

    private fun getAllValidOrders(productsList : List<Product>, ordersList : List<Order>) : List<Order> {
        return ordersList
            .asSequence()
            .filter { it.isOrderValid(ordersList, productsList) && it.type == CREATE_ORDER_TYPE }
            .associateBy { it.orderId }
            .map { (_, order) -> order }
            .toList()
    }

    private suspend fun createOrdersStorageLibrary(productsList : List<Product>, ordersList : List<Order>) {
        val ordersData = ArrayList<KeyWithTwoDataElements>()

        getAllValidOrders(productsList, ordersList)
            .forEach { order ->
                val elementForOrdersData = order.getOrderAsStorageLibraryElement(ordersList)
                ordersData.add(elementForOrdersData)
        }

        ordersStorageLibrary.initializeDatabase(ordersData)
    }

    private fun getUserAsStorageLibraryElement(userId: String, validOrdersByUserList: List<Order>) : KeyWithTwoDataElements {
        val orderIdsByUserList = validOrdersByUserList
            .map { order -> order.orderId }
            .toList()

        return KeyWithTwoDataElements(userId, "", orderIdsByUserList)
    }

    private suspend fun createUsersStorageLibrary(productsList : List<Product>, ordersList : List<Order>) {
        val usersData = ArrayList<KeyWithTwoDataElements>()

        getAllValidOrders(productsList, ordersList)
            .asSequence()
            .groupBy { it.userId }
            .forEach { (userId, validOrdersByUserList) ->
                val validOrdersByUserSortedList = validOrdersByUserList.sortedBy { order -> order.orderId }
                val elementForUsersData = getUserAsStorageLibraryElement(userId, validOrdersByUserSortedList)
                usersData.add(elementForUsersData)
            }

        usersStorageLibrary.initializeDatabase(usersData)
    }

    private suspend fun setup(data: String, parser: Parser) {
        val productsList = parser.parseFileToProductsList(data)
        val ordersList = parser.parseFileToOrdersList(data)
        createProductsStorageLibrary(productsList, ordersList)
        createOrdersStorageLibrary(productsList, ordersList)
        createUsersStorageLibrary(productsList, ordersList)
    }

    /** for testing */
    suspend fun getProductsStorageLibraryAsList() : List<String> {
        return productsStorageLibrary.getDatabaseAsList()
    }

    /** for testing */
    suspend fun getOrdersStorageLibraryAsList() : List<String> {
        return ordersStorageLibrary.getDatabaseAsList()
    }

    /** for testing */
    suspend fun getUsersStorageLibraryAsList() : List<String> {
        return usersStorageLibrary.getDatabaseAsList()
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
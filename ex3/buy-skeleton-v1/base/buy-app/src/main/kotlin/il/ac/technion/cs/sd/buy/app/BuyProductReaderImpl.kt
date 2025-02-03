package il.ac.technion.cs.sd.buy.app

import com.google.inject.Inject
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import il.ac.technion.cs.sd.buy.lib.StorageLibrary

class BuyProductReaderImpl @Inject constructor(suspendLineStorageFactory: SuspendLineStorageFactory) : BuyProductReader {
    private var productsDB = StorageLibrary(suspendLineStorageFactory, "products")
    private var ordersDB = StorageLibrary(suspendLineStorageFactory, "orders")
    private var usersDB = StorageLibrary(suspendLineStorageFactory, "users")


    /** Returns true iff the given ID is that of a valid (possibly canceled) order. */
    override suspend fun isValidOrderId(orderId: String): Boolean {
        return ordersDB.getDataListsFromSuspendLineStorage(orderId) != null
    }

    /** Returns true iff the given ID is that of a valid and canceled order. */
    override suspend fun isCanceledOrder(orderId: String): Boolean {
        val orderLists = ordersDB.getDataListsFromSuspendLineStorage(orderId) ?: return false
        return orderLists[0].contains("C")
    }

    /** Returns true iff the given ID is that of a valid order that was modified */
    override suspend fun isModifiedOrder(orderId: String): Boolean {
        val orderLists = ordersDB.getDataListsFromSuspendLineStorage(orderId) ?: return false
        return orderLists[0].contains("M")
    }

    /**
     * Returns the number of products that were ordered with the given order ID. If the order was modified, returns the
     * current number. If the order was cancelled, returns the **negation** of the last number. If the order ID is not
     * found, returns null.
     */
    override suspend fun getNumberOfProductOrdered(orderId: String): Int? {
        val orderLists = ordersDB.getDataListsFromSuspendLineStorage(orderId) ?: return null
        val amount = orderLists[1].split(" ").last().toInt()
        if (isCanceledOrder(orderId)) return -amount
        return amount
    }

    /**
     * Returns the history of products ordered with the given order ID, from first to last. If the order was cancelled,
     * appends -1 to the list. If the order ID is invalid, returns an empty list.
     */
    override suspend fun getHistoryOfOrder(orderId: String): List<Int> {
        val orderLists = ordersDB.getDataListsFromSuspendLineStorage(orderId) ?: return emptyList()
        val amounts = orderLists[1].split(" ").map { it.toInt() }
        return amounts
    }

    /**
     * Returns the order IDs of all orders made by the given user (including cancelled orders), lexicographically ordered.
     * If the user is not found, returns an empty list.
     */
    override suspend fun getOrderIdsForUser(userId: String): List<String> {
        val orderList = usersDB.getDataListsFromSuspendLineStorage(userId) ?: return emptyList()
        return orderList[0].split(" ")
    }

    /**
     * Returns the total amount of money spent by the user, i.e., the sum of (cost of each product * items purchased).
     * If the user is not found, returns 0. Canceled orders are not included in this sum.
     */
    override suspend fun getTotalAmountSpentByUser(userId: String): Long {
        TODO("search for user id, read the 3rd line and return the sum of the products")
    }

    /**
     * Returns the list of user IDs that purchased this product. If the product ID isn't found, return an empty list.
     * Users who only made a purchase that was later canceled do not appear in this list.
     */
    override suspend fun getUsersThatPurchased(productId: String): List<String> {
        TODO("search for product id, read the 2nd line and return the list of user ids")
    }

    /**
     * Returns a list of order IDs that contained this product, including canceled orders.
     * If the product is not found, returns an empty list.
     */
    override suspend fun getOrderIdsThatPurchased(productId: String): List<String> {
        TODO("search for product id, read the 3rd line and return the list of order ids")
    }

    /**
     * Returns the total count of purchased items of the given product ID. Canceled orders do not contribute to this
     * sum. If the product ID is not found, returns null.
     */
    override suspend fun getTotalNumberOfItemsPurchased(productId: String): Long? {
        TODO("search for product id, read the 3rd line and return the sum of the numbers")
    }

    /**
     * Returns the average number of purchased items of the give product ID. Canceled orders do not contribute to this
     * average. If the product ID is not found, or it is found only in canceled orders, returns null.
     */
    override suspend fun getAverageNumberOfItemsPurchased(productId: String): Double? {
        TODO("search for product id, read the 3rd line and return the average of the numbers")
    }

    /**
     * Returns the ratio of canceled orders to total orders, e.g., if the user made a total of 10 orders and 6 of them
     * were canceled, returns 0.6. If the user ID is not found, returns null.
     */
    override suspend fun getCancelRatioForUser(userId: String): Double? {
        TODO("search for user id, read the 2nd line of the user where there is a mark of C or MC")
    }

    /**
     * Returns the ratio of modified orders to total orders, e.g., if the user made a total of 10 orders and 6 of them
     * were modified, returns 0.6. Modified orders that were later canceled are included. If the user ID is not found,
     * returns null.
     */
    override suspend fun getModifyRatioForUser(userId: String): Double? {
        TODO("search for user id, read the 2nd line of the user where there is a mark of M or MC")
    }

    /**
     * Returns a map from product IDs to the total number of items that were purchased, across all orders. Canceled
     * orders are not included in this total. If the user ID is not found, returns an empty map.
     */
    override suspend fun getAllItemsPurchased(userId: String): Map<String, Long> {
        TODO("search for user id, read the 3rd line of the user")
    }

    /**
     * Returns a map from user IDs to the total number of items that the user purchased. Canceled orders are not
     * included in this total. If the product ID is not found, returns an empty map.
     */
    override suspend fun getItemsPurchasedByUsers(productId: String): Map<String, Long> {
        TODO("search for product id, read 2nd line of the product")
    }
}
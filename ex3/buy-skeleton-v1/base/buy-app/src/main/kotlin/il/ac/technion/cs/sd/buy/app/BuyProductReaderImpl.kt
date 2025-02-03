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
        return ordersDB.getDataFromSuspendLineStorage(orderId) != null
    }

    /** Returns true iff the given ID is that of a valid and canceled order. */
    override suspend fun isCanceledOrder(orderId: String): Boolean {
        val orderLists = ordersDB.getDataFromSuspendLineStorage(orderId) ?: return false
        return orderLists[1].contains(CANCEL_ORDER_AMOUNT)
    }

    /** Returns true iff the given ID is that of a valid order that was modified */
    override suspend fun isModifiedOrder(orderId: String): Boolean {
        val orderLists = ordersDB.getDataFromSuspendLineStorage(orderId) ?: return false
        return orderLists[1].length > 1
    }

    /**
     * Returns the number of products that were ordered with the given order ID. If the order was modified, returns the
     * current number. If the order was cancelled, returns the **negation** of the last number. If the order ID is not
     * found, returns null.
     */
    override suspend fun getNumberOfProductOrdered(orderId: String): Int? {
        val orderLists = ordersDB.getDataFromSuspendLineStorage(orderId) ?: return null
        val amountHistory = orderLists[1].split(" ")
        val indexOfLastAmount = amountHistory.size - 1
        val amount = amountHistory[indexOfLastAmount].toInt()
        if (amount < 0) {
            return -(amountHistory[indexOfLastAmount - 1].toInt())
        }
        else {
            return amount
        }
    }

    /**
     * Returns the history of products ordered with the given order ID, from first to last. If the order was cancelled,
     * appends -1 to the list. If the order ID is invalid, returns an empty list.
     */
    override suspend fun getHistoryOfOrder(orderId: String): List<Int> {
        val orderLists = ordersDB.getDataFromSuspendLineStorage(orderId) ?: return emptyList()
        val amounts = orderLists[1].split(" ").map { it.toInt() }
        return amounts
    }

    /**
     * Returns the order IDs of all orders made by the given user (including cancelled orders), lexicographically ordered.
     * If the user is not found, returns an empty list.
     */
    override suspend fun getOrderIdsForUser(userId: String): List<String> {
        val userLists = usersDB.getDataFromSuspendLineStorage(userId) ?: return emptyList()
        return userLists[1].split(" ")
    }

    /**
     * Returns the total amount of money spent by the user, i.e., the sum of (cost of each product * items purchased).
     * If the user is not found, returns 0. Canceled orders are not included in this sum.
     */
    override suspend fun getTotalAmountSpentByUser(userId: String): Long {
        val userList = usersDB.getDataFromSuspendLineStorage(userId) ?: return 0
        var sum : Long = 0
        userList[1].split(" ")
            .filter { !isCanceledOrder(it) }
            .forEach { orderId ->
                val orderDetails = ordersDB.getDataFromSuspendLineStorage(orderId)?.get(0)
                val productId = orderDetails?.split(" ")[1]
                var numberOfProducts = getNumberOfProductOrdered(orderId)?.toLong()
                if (numberOfProducts != null && numberOfProducts < 0) {
                    numberOfProducts = 0
                }
                val price = productsDB.getDataFromSuspendLineStorage(productId.toString())?.get(0)?.toLong()

                if (numberOfProducts != null && price != null) {
                    sum += (numberOfProducts * price)
                }
            }
        return sum
    }

    /**
     * Returns the list of user IDs that purchased this product. If the product ID isn't found, return an empty list.
     * Users who only made a purchase that was later canceled do not appear in this list.
     */
    override suspend fun getUsersThatPurchased(productId: String): List<String> {
        val productOrdersIds = productsDB.getDataFromSuspendLineStorage(productId)?.get(1)?.split(" ") ?: return emptyList()
        val notCanceledOrderIds = productOrdersIds.filter { !isCanceledOrder(it) }
        return notCanceledOrderIds
            .map { orderId ->
                ordersDB.getDataFromSuspendLineStorage(orderId)?.get(0)?.split(" ")?.get(0) ?: ""
            }
            .toList()
    }

    /**
     * Returns a list of order IDs that contained this product, including canceled orders.
     * If the product is not found, returns an empty list.
     */
    override suspend fun getOrderIdsThatPurchased(productId: String): List<String> {
        val productOrderIds = productsDB.getDataFromSuspendLineStorage(productId)?.get(1)?.split(" ") ?: return emptyList()
        return productOrderIds
    }

    /**
     * Returns the total count of purchased items of the given product ID. Canceled orders do not contribute to this
     * sum. If the product ID is not found, returns null.
     */
    override suspend fun getTotalNumberOfItemsPurchased(productId: String): Long? {
        val productOrderIds = productsDB.getDataFromSuspendLineStorage(productId)?.get(1)?.split(" ") ?: return null
        var count : Long = 0
        productOrderIds.forEach { orderId ->
            val amountOrdered = getNumberOfProductOrdered(orderId)?.toInt()
            if (amountOrdered != null) {
                if (amountOrdered >= 0) {
                    count += amountOrdered
                }
            }
        }
        return count
    }

    /**
     * Returns the average number of purchased items of the give product ID. Canceled orders do not contribute to this
     * average. If the product ID is not found, or it is found only in canceled orders, returns null.
     */
    override suspend fun getAverageNumberOfItemsPurchased(productId: String): Double? {
        val productOrderIds = productsDB.getDataFromSuspendLineStorage(productId)?.get(1)?.split(" ") ?: return null
        productOrderIds
            .map { orderId ->
                getHistoryOfOrder(orderId).last()
            }
            .filter { it != CANCEL_ORDER_AMOUNT.toInt() }
            .average()
    }

    /**
     * Returns the ratio of canceled orders to total orders, e.g., if the user made a total of 10 orders and 6 of them
     * were canceled, returns 0.6. If the user ID is not found, returns null.
     */
    override suspend fun getCancelRatioForUser(userId: String): Double? {
        val orderIdsOfUser = getOrderIdsForUser(userId)
        if (orderIdsOfUser.isEmpty()) {
            return null
        }
        val totalNumOrdersOfUser = orderIdsOfUser.size.toDouble()
        val canceledOrdersNum = orderIdsOfUser.filter { !isCanceledOrder(it) }.size.toDouble()
        return canceledOrdersNum / totalNumOrdersOfUser
    }

    /**
     * Returns the ratio of modified orders to total orders, e.g., if the user made a total of 10 orders and 6 of them
     * were modified, returns 0.6. Modified orders that were later canceled are included. If the user ID is not found,
     * returns null.
     */
    override suspend fun getModifyRatioForUser(userId: String): Double? {
        val orderIdsOfUser = getOrderIdsForUser(userId)
        if (orderIdsOfUser.isEmpty()) {
            return null
        }
        val totalNumOrdersOfUser = orderIdsOfUser.size.toDouble()
        val modifiedOrdersNum = orderIdsOfUser.filter { !isModifiedOrder(it) }.size.toDouble()
        return modifiedOrdersNum / totalNumOrdersOfUser
    }

    /**
     * Returns a map from product IDs to the total number of items that were purchased, across all orders. Canceled
     * orders are not included in this total. If the user ID is not found, returns an empty map.
     */
    override suspend fun getAllItemsPurchased(userId: String): Map<String, Long> {
        val orderIdsOfUser = getOrderIdsForUser(userId).filter { !isCanceledOrder(it) }

        orderIdsOfUser.forEach { orderId ->
            val productId = ordersDB.getDataFromSuspendLineStorage(orderId)?.get(0)?.split(" ")[1]

        }
    }

    /**
     * Returns a map from user IDs to the total number of items that the user purchased. Canceled orders are not
     * included in this total. If the product ID is not found, returns an empty map.
     */
    override suspend fun getItemsPurchasedByUsers(productId: String): Map<String, Long> {
        TODO("search for product id, read 2nd line of the product")
    }
}
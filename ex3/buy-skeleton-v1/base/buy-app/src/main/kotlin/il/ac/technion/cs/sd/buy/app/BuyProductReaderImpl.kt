package il.ac.technion.cs.sd.buy.app

import com.google.inject.Inject
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import il.ac.technion.cs.sd.buy.lib.StorageLibrary
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap

class BuyProductReaderImpl @Inject constructor(private val suspendLineStorageFactory: SuspendLineStorageFactory) : BuyProductReader {
    private val productsStorageLibrary = StorageLibrary(suspendLineStorageFactory, "products")
    private val ordersStorageLibrary = StorageLibrary(suspendLineStorageFactory, "orders")
    private val usersStorageLibrary = StorageLibrary(suspendLineStorageFactory, "users")


    /** Returns true if the given ID is that of a valid (possibly canceled) order. */
    override suspend fun isValidOrderId(orderId: String): Boolean {
        return ordersStorageLibrary.getMainDataFromSuspendLineStorage(orderId) != null
    }

    /** Returns true if the given ID is that of a valid and canceled order. */
    override suspend fun isCanceledOrder(orderId: String): Boolean {
        return ordersStorageLibrary.getListDataFromSuspendLineStorage(orderId)
            ?.contains(CANCEL_ORDER_AMOUNT) == true
    }

    /** Returns true if the given ID is that of a valid order that was modified */
    override suspend fun isModifiedOrder(orderId: String): Boolean {
        val orderHistory = ordersStorageLibrary.getListDataFromSuspendLineStorage(orderId)?.split(" ")
        return if (orderHistory == null) {
            false
        } else if (orderHistory.contains(CANCEL_ORDER_AMOUNT) == true) {
            orderHistory.size > 2
        } else {
            orderHistory.size > 1
        }
    }

    /**
     * Returns the number of products that were ordered with the given order ID. If the order was modified, returns the
     * current number. If the order was cancelled, returns the **negation** of the last number. If the order ID is not
     * found, returns null.
     */
    override suspend fun getNumberOfProductOrdered(orderId: String): Int? {
        val amountHistory = ordersStorageLibrary
            .getListDataFromSuspendLineStorage(orderId)
            ?.split(" ")
            ?: return null

        val indexOfLastAmount = amountHistory.size - 1
        val amount = amountHistory[indexOfLastAmount].toInt()
        return if (amount < 0) {
            -(amountHistory[indexOfLastAmount - 1].toInt())
        } else {
            amount
        }
    }

    /**
     * Returns the history of products ordered with the given order ID, from first to last. If the order was cancelled,
     * appends -1 to the list. If the order ID is invalid, returns an empty list.
     */
    override suspend fun getHistoryOfOrder(orderId: String): List<Int> {
        return ordersStorageLibrary
            .getListDataFromSuspendLineStorage(orderId)
            ?.split(" ")
            ?.map { it.toInt() }
            ?: emptyList()
    }

    /**
     * Returns the order IDs of all orders made by the given user (including cancelled orders), lexicographically ordered.
     * If the user is not found, returns an empty list.
     */
    override suspend fun getOrderIdsForUser(userId: String): List<String> {
        return usersStorageLibrary
            .getListDataFromSuspendLineStorage(userId)
            ?.split(" ")
            ?: return emptyList()
    }

    /**
     * Returns the total amount of money spent by the user, i.e., the sum of (cost of each product * items purchased).
     * If the user is not found, returns 0. Canceled orders are not included in this sum.
     */
    override suspend fun getTotalAmountSpentByUser(userId: String): Long = coroutineScope {
        return@coroutineScope usersStorageLibrary
            .getListDataFromSuspendLineStorage(userId)
            ?.split(" ")
            ?.sumOf { orderId ->
                val productId = async { ordersStorageLibrary
                    .getMainDataFromSuspendLineStorage(orderId)
                    ?.split(" ")[1]
                    ?: "" }

                val numberOfItemsPurchased = async {
                    var numberOfProductOrdered = getNumberOfProductOrdered(orderId)?.toLong()
                    if (numberOfProductOrdered == null || numberOfProductOrdered < 0)
                    {
                        numberOfProductOrdered = 0L
                    }
                    numberOfProductOrdered
                }

                val price = async { productsStorageLibrary
                    .getMainDataFromSuspendLineStorage(productId.await())
                    ?.toLong()
                    ?: 0L }

                numberOfItemsPurchased.await().times(price.await())
            }
            ?: 0L
    }

    /**
     * Returns the list of user IDs that purchased this product. If the product ID isn't found, return an empty list.
     * Users who only made a purchase that was later canceled do not appear in this list.
     */
    override suspend fun getUsersThatPurchased(productId: String): List<String> = coroutineScope {
        return@coroutineScope productsStorageLibrary
            .getListDataFromSuspendLineStorage(productId)
            ?.split(" ")
            ?.filter { !isCanceledOrder(it) }
            ?.map { orderId ->
                async {
                    ordersStorageLibrary
                        .getMainDataFromSuspendLineStorage(orderId)
                        ?.split(" ")
                        ?.get(0)
                        ?: ""
                }
            }
            ?.awaitAll()
            ?.toList()
            ?: emptyList()
    }

    /**
     * Returns a list of order IDs that contained this product, including canceled orders.
     * If the product is not found, returns an empty list.
     */
    override suspend fun getOrderIdsThatPurchased(productId: String): List<String> {
        return productsStorageLibrary
            .getListDataFromSuspendLineStorage(productId)
            ?.split(" ")
            ?: emptyList()
    }

    /**
     * Returns the total count of purchased items of the given product ID. Canceled orders do not contribute to this
     * sum. If the product ID is not found, returns null.
     */
    override suspend fun getTotalNumberOfItemsPurchased(productId: String): Long? = coroutineScope {
        return@coroutineScope productsStorageLibrary
            .getListDataFromSuspendLineStorage(productId)
            ?.split(" ")
            ?.map { orderId ->
                async {
                    val amountOrdered = getNumberOfProductOrdered(orderId)?.toLong()
                    if (amountOrdered != null && amountOrdered >= 0) {
                        amountOrdered
                    } else {
                        0L
                    }
                }
            }
            ?.awaitAll()
            ?.sum()
    }

    /**
     * Returns the average number of purchased items of the given product ID. Canceled orders do not contribute to this
     * average. If the product ID is not found, or it is found only in canceled orders, returns null.
     */
    override suspend fun getAverageNumberOfItemsPurchased(productId: String): Double? = coroutineScope {
        val purchasedItemsForEachOrder = productsStorageLibrary
            .getListDataFromSuspendLineStorage(productId)
            ?.split(" ")
            ?.map { orderId ->
                async {
                    getHistoryOfOrder(orderId).last()
                }
            }
            ?.awaitAll()
            ?.filter { it != CANCEL_ORDER_AMOUNT.toInt() }

            if (purchasedItemsForEachOrder == null || purchasedItemsForEachOrder.isEmpty()) {
                return@coroutineScope null
            }
            else {
                return@coroutineScope purchasedItemsForEachOrder.average()
            }
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
        val canceledOrdersNum = orderIdsOfUser
            .filter { isCanceledOrder(it) }
            .size.toDouble()

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
        val modifiedOrdersNum = orderIdsOfUser
            .filter { isModifiedOrder(it) && !isCanceledOrder(it) }
            .size.toDouble()

        return modifiedOrdersNum / totalNumOrdersOfUser
    }

    /**
     * Returns a map from product IDs to the total number of items that were purchased, across all orders. Canceled
     * orders are not included in this total. If the user ID is not found, returns an empty map.
     */
    override suspend fun getAllItemsPurchased(userId: String): Map<String, Long> = coroutineScope {
        val productToAmountMap = ConcurrentHashMap<String, Long>()

        val deferredResults = getOrderIdsForUser(userId)
            .map { orderId ->
                async {
                    val productIdInOrder = ordersStorageLibrary
                        .getMainDataFromSuspendLineStorage(orderId)
                        ?.split(" ")
                        ?.get(1)

                    val amountInOrder = getNumberOfProductOrdered(orderId)?.toLong()

                    if (amountInOrder != null && productIdInOrder != null && amountInOrder >= 0) {
                        productToAmountMap[productIdInOrder] =
                            productToAmountMap.merge(productIdInOrder, amountInOrder, Long::plus) as Long
                    }
                }
            }

        deferredResults.awaitAll()
        return@coroutineScope productToAmountMap
    }

    /**
     * Returns a map from user IDs to the total number of items that the user purchased. Canceled orders are not
     * included in this total. If the product ID is not found, returns an empty map.
     */
    override suspend fun getItemsPurchasedByUsers(productId: String): Map<String, Long> = coroutineScope {
        val userToAmountMap = ConcurrentHashMap<String, Long>()

        val deferredResults = getOrderIdsThatPurchased(productId)
            .filter { orderId -> !isCanceledOrder(orderId) }
            .map { orderId ->
                async {
                    val userIdOfOrder = ordersStorageLibrary
                        .getMainDataFromSuspendLineStorage(orderId)
                        ?.split(" ")
                        ?.get(0)
                        ?: ""

                    val amountOrderedByUser = getAllItemsPurchased(userIdOfOrder)[productId]?.toLong()

                    if (amountOrderedByUser != null) {
                        userToAmountMap[userIdOfOrder] =
                            userToAmountMap.merge(userIdOfOrder, amountOrderedByUser, Long::plus) as Long
                    }
                }
            }

        deferredResults.awaitAll()
        return@coroutineScope userToAmountMap
    }
}
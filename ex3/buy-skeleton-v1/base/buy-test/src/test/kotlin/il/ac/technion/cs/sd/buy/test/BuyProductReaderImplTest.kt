package il.ac.technion.cs.sd.buy.test

import com.google.inject.Guice
import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer
import il.ac.technion.cs.sd.buy.app.BuyProductModule
import il.ac.technion.cs.sd.buy.app.BuyProductReader
import il.ac.technion.cs.sd.buy.external.LineStorageModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import kotlin.time.Duration.Companion.seconds

class BuyProductReaderImplTest {
    private suspend fun setupAndGetInjector(fileName: String): Injector {
        val fileContents: String =
            javaClass.getResource(fileName)?.readText() ?:
            throw FileNotFoundException("Could not open file $fileName")

        val injector = Guice.createInjector(BuyProductModule(), LineStorageModule())
        val buyProductInitializer = injector.getInstance<BuyProductInitializer>()
        if (fileName.endsWith("xml"))
            buyProductInitializer.setupXml(fileContents)
        else {
            assert(fileName.endsWith("json"))
            buyProductInitializer.setupJson(fileContents)
        }
        return injector
    }

    @Test
    fun `isValidOrderId should return true for a valid not canceled order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertTrue(buyProductReader.isValidOrderId("2"))
        }
    }

    @Test
    fun `isValidOrderId should return true for a valid canceled order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertTrue(buyProductReader.isValidOrderId("4"))
        }
    }

    @Test
    fun `isValidOrderId should return false for an invalid order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("order_not_created_product.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isValidOrderId("1"))
        }
    }

    @Test
    fun `isValidOrderId should return false for an order that does not exist` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isValidOrderId("abc"))
        }
    }

    @Test
    fun `isCanceledOrder should return true for a valid and canceled order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertTrue(buyProductReader.isCanceledOrder("4"))
        }
    }

    @Test
    fun `isCanceledOrder should return false for a valid and not canceled order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isCanceledOrder("2"))
        }
    }

    @Test
    fun `isCanceledOrder should return false for an invalid order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("order_not_created_product.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isCanceledOrder("1"))
        }
    }

    @Test
    fun `isCanceledOrder should return false for an order that does not exist` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isCanceledOrder("abc"))
        }
    }

    @Test
    fun `isModifiedOrder should return true for a valid and modified order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertTrue(buyProductReader.isModifiedOrder("2"))
        }
    }

    @Test
    fun `isModifiedOrder should return true for a valid, modified and canceled order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("two_instances_of_each_element.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertTrue(buyProductReader.isModifiedOrder("1"))
        }
    }

    @Test
    fun `isModifiedOrder should return false for a valid and only created order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("modify_or_cancel_order_before_create.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isModifiedOrder("2"))
        }
    }

    @Test
    fun `isModifiedOrder should return false for a valid and only canceled order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isModifiedOrder("4"))
        }
    }

    @Test
    fun `isModifiedOrder should return false for an invalid and modified order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("modify_or_cancel_order_no_create.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isModifiedOrder("2"))
        }
    }

    @Test
    fun `isModifiedOrder should return false for an order that does not exist` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertFalse(buyProductReader.isModifiedOrder("abc"))
        }
    }

    @Test
    fun `getNumberOfProductOrdered should return the number of products that were ordered in an order that was not modified or canceled` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("modify_or_cancel_order_before_create.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(5, buyProductReader.getNumberOfProductOrdered("2"))
        }
    }

    @Test
    fun `getNumberOfProductOrdered should return the last number of products that were ordered in an order that was only modified` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(10, buyProductReader.getNumberOfProductOrdered("2"))
        }
    }

    @Test
    fun `getNumberOfProductOrdered should return the negation of the last number of products that were ordered in an order that was canceled` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(-10, buyProductReader.getNumberOfProductOrdered("4"))
        }
    }

    @Test
    fun `getNumberOfProductOrdered should return null for an order that does not exist` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertNull(buyProductReader.getNumberOfProductOrdered("abc"))
        }
    }

    @Test
    fun `getNumberOfProductOrdered should return null for an invalid order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("modify_or_cancel_order_no_create.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertNull(buyProductReader.getNumberOfProductOrdered("2"))
        }
    }

    @Test
    fun `getHistoryOfOrder should return a list of the history of amounts ordered for an only modified order from first to last` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("multiple_modifications.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val historyOfOrder = buyProductReader.getHistoryOfOrder("2")
            assertEquals(5, historyOfOrder[0])
            assertEquals(10, historyOfOrder[1])
            assertEquals(15, historyOfOrder[2])
            assertEquals(20, historyOfOrder[3])
            assertEquals(4, historyOfOrder.size)
        }
    }

    @Test
    fun `getHistoryOfOrder should return a list of the history of amounts ordered for a canceled order that was later modified from first to last - should override -1` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("modify_order_after_cancel.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val historyOfOrder = buyProductReader.getHistoryOfOrder("2")
            assertEquals(5, historyOfOrder[0])
            assertEquals(10, historyOfOrder[1])
            assertEquals(2, historyOfOrder.size)
        }
    }

    @Test
    fun `getHistoryOfOrder should return a list of the history of amounts ordered for a canceled order from first to last where last is -1` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("cancel_after_multiple_modifications.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val historyOfOrder = buyProductReader.getHistoryOfOrder("2")
            assertEquals(5, historyOfOrder[0])
            assertEquals(10, historyOfOrder[1])
            assertEquals(15, historyOfOrder[2])
            assertEquals(20, historyOfOrder[3])
            assertEquals(-1, historyOfOrder[4])
            assertEquals(5, historyOfOrder.size)
        }
    }

    @Test
    fun `getHistoryOfOrder should return an empty list for an invalid order` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("modify_or_cancel_order_no_create.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val historyOfOrder = buyProductReader.getHistoryOfOrder("2")
            assertEquals(0, historyOfOrder.size)
        }
    }

    @Test
    fun `getHistoryOfOrder should return an empty list for an order that does not exist` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val historyOfOrder = buyProductReader.getHistoryOfOrder("abc")
            assertEquals(0, historyOfOrder.size)
        }
    }

    @Test
    fun `getOrderIdsForUser should return a list of the order IDs of all orders made by a valid user, including canceled orders, lexicographically ordered` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("one_user_multiple_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val orderIds = buyProductReader.getOrderIdsForUser("1")
            assertEquals("androidOrder", orderIds[0])
            assertEquals("iphoneOrder", orderIds[1])
            assertEquals("iphoneOrder2", orderIds[2])
            assertEquals("macOrder", orderIds[3])
            assertEquals(4, orderIds.size)
        }
    }

    @Test
    fun `getOrderIdsForUser should return an empty list for an invalid user` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val orderIds = buyProductReader.getOrderIdsForUser("abc")
            assertEquals(0, orderIds.size)
        }
    }

    @Test
    fun `getTotalAmountSpentByUser should return the total amount of money spent by a valid user, not including canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("one_user_multiple_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(54500, buyProductReader.getTotalAmountSpentByUser("1"))
        }
    }

    @Test
    fun `getTotalAmountSpentByUser should return 0 for a valid user that has only canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(0, buyProductReader.getTotalAmountSpentByUser("3"))
        }
    }

    @Test
    fun `getTotalAmountSpentByUser should return 0 for an invalid user` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(0, buyProductReader.getTotalAmountSpentByUser("abc"))
        }
    }

    @Test
    fun `getUsersThatPurchased should return a list of user IDs that purchased a valid product, not including canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val userIds = buyProductReader.getUsersThatPurchased("iphone")
            assertEquals("1", userIds[0])
            assertEquals(1, userIds.size)
        }
    }

    @Test
    fun `getUsersThatPurchased should return an empty list for a valid product with only canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_product_only_canceled_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val userIds = buyProductReader.getUsersThatPurchased("iphone")
            assertEquals(0, userIds.size)
        }
    }

    @Test
    fun `getUsersThatPurchased should return an empty list for an invalid product` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val userIds = buyProductReader.getUsersThatPurchased("abc")
            assertEquals(0, userIds.size)
        }
    }

    @Test
    fun `getOrderIdsThatPurchased should return a list of order IDs that contained a valid product, including canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val userIds = buyProductReader.getOrderIdsThatPurchased("iphone")
            assertEquals("2", userIds[0])
            assertEquals("4", userIds[1])
            assertEquals(2, userIds.size)
        }
    }

    @Test
    fun `getOrderIdsThatPurchased should return an empty list for an invalid product` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val userIds = buyProductReader.getOrderIdsThatPurchased("hi")
            assertEquals(0, userIds.size)
        }
    }

    @Test
    fun `getTotalNumberOfItemsPurchased should return the total count of purchased items of a valid product, not including canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_product_multiple_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(410, buyProductReader.getTotalNumberOfItemsPurchased("iphone"))
        }
    }

    @Test
    fun `getTotalNumberOfItemsPurchased should return 0 for a valid product with only canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_product_only_canceled_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(0, buyProductReader.getTotalNumberOfItemsPurchased("iphone"))
        }
    }

    @Test
    fun `getTotalNumberOfItemsPurchased should return null for an invalid product` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertNull(buyProductReader.getTotalNumberOfItemsPurchased("abc"))
        }
    }

    @Test
    fun `getAverageNumberOfItemsPurchased should return the average number of purchased items of a valid product, not including canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_product_multiple_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(410.0 / 3.0, buyProductReader.getAverageNumberOfItemsPurchased("iphone"))
        }
    }

    @Test
    fun `getAverageNumberOfItemsPurchased should return null for a valid product with only canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_product_only_canceled_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertNull(buyProductReader.getAverageNumberOfItemsPurchased("iphone"))
        }
    }

    @Test
    fun `getAverageNumberOfItemsPurchased should return null for an invalid product` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertNull(buyProductReader.getAverageNumberOfItemsPurchased("abc"))
        }
    }

    @Test
    fun `getCancelRatioForUser should return the ratio of canceled orders to total orders for a valid user` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_product_multiple_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(1.0 / 4.0, buyProductReader.getCancelRatioForUser("1"))
        }
    }

    @Test
    fun `getCancelRatioForUser should return null for an invalid user` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertNull(buyProductReader.getCancelRatioForUser("abc"))
        }
    }

    @Test
    fun `getModifyRatioForUser should return the ratio of modified orders to total orders of a valid user, including canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_product_multiple_orders.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertEquals(1.0 / 4.0, buyProductReader.getModifyRatioForUser("1"))
        }
    }

    @Test
    fun `getModifyRatioForUser should return null for an invalid user` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            assertNull(buyProductReader.getModifyRatioForUser("abc"))
        }
    }

    @Test
    fun `getAllItemsPurchased should return a map from product IDs to the total number of items that were purchased across all orders for a valid user, not including canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_user_multiple_products.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val resultMap = buyProductReader.getAllItemsPurchased("1")
            assertEquals(5, resultMap["android"])
            assertEquals(5, resultMap["cool"])
            assertEquals(405, resultMap["iphone"])
        }
    }

    @Test
    fun `getAllItemsPurchased should return an empty map for an invalid user` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val resultMap = buyProductReader.getAllItemsPurchased("abc")
            assertEquals(0, resultMap.size)
        }
    }

    @Test
    fun `getItemsPurchasedByUsers should return a map from user IDs to the total number of items that the user purchased for a valid product, not including canceled orders` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("single_product_multiple_users.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val resultMap = buyProductReader.getItemsPurchasedByUsers("iphone")
            assertEquals(5, resultMap["1"])
            assertEquals(5, resultMap["2"])
            assertEquals(400, resultMap["3"])
        }
    }

    @Test
    fun `getItemsPurchasedByUsers should return an empty map for an invalid product` () = runTest(timeout = 6.seconds) {
        val injector = setupAndGetInjector("no_edge_cases.xml")
        val buyProductReader = injector.getInstance<BuyProductReader>()

        launch(Dispatchers.Default) {
            val resultMap = buyProductReader.getItemsPurchasedByUsers("abc")
            assertEquals(0, resultMap.size)
        }
    }
}
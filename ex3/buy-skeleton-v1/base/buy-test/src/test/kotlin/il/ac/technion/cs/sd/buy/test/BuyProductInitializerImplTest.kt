package il.ac.technion.cs.sd.buy.test

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import il.ac.technion.cs.sd.buy.external.LineStorageModule
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

class BuyProductInitializerImplTest {
    private val suspendLineStorageFactory = Guice.createInjector(LineStorageModule()).getInstance<SuspendLineStorageFactory>()

    private fun getFileContents(fileName: String): String {
        return javaClass.getResource(fileName)?.readText() ?:
        throw FileNotFoundException("Could not open file $fileName")
    }

    @Test
    fun `sanity test`() = runTest {
        val fileContents = getFileContents("no_edge_cases.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("iphone", productsDB[0])
        Assertions.assertEquals("1 10", productsDB[1])
        Assertions.assertEquals("2 10 4 -1", productsDB[2])

        Assertions.assertEquals("2", ordersDB[0])
        Assertions.assertEquals("1 iphone", ordersDB[1])
        Assertions.assertEquals("5 10", ordersDB[2])
        Assertions.assertEquals("4", ordersDB[3])
        Assertions.assertEquals("3 iphone", ordersDB[4])
        Assertions.assertEquals("10 -1", ordersDB[5])

        Assertions.assertEquals("1", usersDB[6])
        Assertions.assertEquals("2 M", usersDB[7])
        Assertions.assertEquals("iphone 10 1000", usersDB[8])
        Assertions.assertEquals("3", usersDB[9])
        Assertions.assertEquals("4 C", usersDB[10])
        Assertions.assertEquals("iphone 10 1000", usersDB[11])
    }

    @Test
    fun `test where all different elements have the same id - should not change anything`() = runTest {
        val fileContents = getFileContents("same_id.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("1", productsDB[0])
        Assertions.assertEquals("", productsDB[1])
        Assertions.assertEquals("1 -1", productsDB[2])

        Assertions.assertEquals("1", ordersDB[0])
        Assertions.assertEquals("1 1", ordersDB[1])
        Assertions.assertEquals("5 10 -1", ordersDB[2])

        Assertions.assertEquals("1", usersDB[0])
        Assertions.assertEquals("1 MC", usersDB[1])
        Assertions.assertEquals("1 -1 1000", usersDB[2])
    }

    @Test
    fun `test where the same product is created multiple times with different amounts - only the last price should count`() = runTest {
        val fileContents = getFileContents("same_product_id.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("iphone", productsDB[0])
        Assertions.assertEquals("", productsDB[1])
        Assertions.assertEquals("1 -1", productsDB[2])

        Assertions.assertEquals("1", ordersDB[0])
        Assertions.assertEquals("John iphone", ordersDB[1])
        Assertions.assertEquals("5 10 -1", ordersDB[2])

        Assertions.assertEquals("John", usersDB[0])
        Assertions.assertEquals("1 MC", usersDB[1])
        Assertions.assertEquals("iphone -1 40", usersDB[2])
    }

    @Test
    fun `test where a product is ordered before it is created - the order that contains this product should be valid`() = runTest {
        val fileContents = getFileContents("order_product_before_create_product.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("iphone", productsDB[0])
        Assertions.assertEquals("", productsDB[1])
        Assertions.assertEquals("1 -1", productsDB[2])

        Assertions.assertEquals("1", ordersDB[0])
        Assertions.assertEquals("John iphone", ordersDB[1])
        Assertions.assertEquals("5 10 -1", ordersDB[2])

        Assertions.assertEquals("John", usersDB[0])
        Assertions.assertEquals("1 MC", usersDB[1])
        Assertions.assertEquals("iphone -1 1000", usersDB[2])
    }

    @Test
    fun `test where a product is ordered but is never created - the order that contains this product should not be valid`() = runTest {
        val fileContents = getFileContents("order_not_created_product.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals(0, productsDB.size)

        Assertions.assertEquals(0, ordersDB.size)

        Assertions.assertEquals(0, usersDB.size)
    }

    @Test
    fun `test where the same order is created multiple times with different details - only the last order and the modifications or cancellations after it should count`() = runTest {
        val fileContents = getFileContents("same_order_id.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("android", productsDB[0])
        Assertions.assertEquals("John 50", productsDB[1])
        Assertions.assertEquals("200 50", productsDB[2])
        Assertions.assertEquals("iphone", productsDB[3])
        Assertions.assertEquals("Mimi 1", productsDB[4])
        Assertions.assertEquals("100 1", productsDB[5])

        Assertions.assertEquals("100", ordersDB[0])
        Assertions.assertEquals("Mimi iphone", ordersDB[1])
        Assertions.assertEquals("20 1", ordersDB[2])
        Assertions.assertEquals("200", ordersDB[3])
        Assertions.assertEquals("John android", ordersDB[4])
        Assertions.assertEquals("50", ordersDB[5])

        Assertions.assertEquals("John", usersDB[0])
        Assertions.assertEquals("200 I", usersDB[1])
        Assertions.assertEquals("android 50 500", usersDB[2])
        Assertions.assertEquals("Mimi", usersDB[3])
        Assertions.assertEquals("100 M", usersDB[4])
        Assertions.assertEquals("iphone 1 1000", usersDB[5])
    }

    @Test
    fun `test where an order is modified or cancelled before it is created - should dismiss all cancellations or modifications of the order before the order is created`() = runTest {
        val fileContents = getFileContents("modify_or_cancel_order_before_create.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("iphone", productsDB[0])
        Assertions.assertEquals("1 5", productsDB[1])
        Assertions.assertEquals("2 5", productsDB[2])

        Assertions.assertEquals("2", ordersDB[0])
        Assertions.assertEquals("1 iphone", ordersDB[1])
        Assertions.assertEquals("5", ordersDB[2])

        Assertions.assertEquals("1", usersDB[0])
        Assertions.assertEquals("2 I", usersDB[1])
        Assertions.assertEquals("iphone 5 1000", usersDB[2])
    }

    @Test
    fun `test where an order is never created, only modified or cancelled - the order should not be valid`() = runTest {
        val fileContents = getFileContents("modify_or_cancel_order_no_create.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("iphone", productsDB[0])
        Assertions.assertEquals("", productsDB[1])
        Assertions.assertEquals("", productsDB[2])

        Assertions.assertEquals(0, ordersDB.size)

        Assertions.assertEquals(0, usersDB.size)
    }

    @Test
    fun `test where an order is modified after it was cancelled - should override the cancellation`() = runTest {
        val fileContents = getFileContents("modify_order_after_cancel.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("iphone", productsDB[0])
        Assertions.assertEquals("1 10", productsDB[1])
        Assertions.assertEquals("2 10", productsDB[2])

        Assertions.assertEquals("2", ordersDB[0])
        Assertions.assertEquals("1 iphone", ordersDB[1])
        Assertions.assertEquals("5 10", ordersDB[2])

        Assertions.assertEquals("1", usersDB[0])
        Assertions.assertEquals("2 M", usersDB[1])
        Assertions.assertEquals("iphone 10 1000", usersDB[2])
    }

    @Test
    fun `test where an order is cancelled multiple times - should appear as just one cancellation`() = runTest {
        val fileContents = getFileContents("multiple_cancellations.xml")
        val buyProductInitializerImpl = BuyProductInitializerImpl(suspendLineStorageFactory)

        buyProductInitializerImpl.setupXml(fileContents)

        val productsDB = buyProductInitializerImpl.getProductsDB().getDatabaseAsList()
        val ordersDB = buyProductInitializerImpl.getOrdersDB().getDatabaseAsList()
        val usersDB = buyProductInitializerImpl.getUsersDB().getDatabaseAsList()

        Assertions.assertEquals("iphone", productsDB[0])
        Assertions.assertEquals("", productsDB[1])
        Assertions.assertEquals("2 -1", productsDB[2])

        Assertions.assertEquals("2", ordersDB[0])
        Assertions.assertEquals("1 iphone", ordersDB[1])
        Assertions.assertEquals("5 10 -1", ordersDB[2])

        Assertions.assertEquals("1", usersDB[0])
        Assertions.assertEquals("2 MC", usersDB[1])
        Assertions.assertEquals("iphone -1 1000", usersDB[2])
    }
}
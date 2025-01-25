package il.ac.technion.cs.sd.buy.test

import il.ac.technion.cs.sd.buy.app.XMLParser
import il.ac.technion.cs.sd.buy.app.Order
import il.ac.technion.cs.sd.buy.app.Product
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

class XMLParserTest {
    private val xmlParser = XMLParser()

    @Test
    fun `XMLParser should create a list of 2 products`() {
        val fileContents: String =
            javaClass.getResource("two_instances_of_each_element.xml")?.readText() ?:
            throw FileNotFoundException("Could not open file")

        val productsList: List<Product> = xmlParser.parseFileToProductsList(fileContents)

        Assertions.assertEquals(2, productsList.size)

        Assertions.assertEquals(productsList[0].type, "product")
        Assertions.assertEquals(productsList[0].id, "iphone")
        Assertions.assertEquals(productsList[0].price, 1000)

        Assertions.assertEquals(productsList[1].type, "product")
        Assertions.assertEquals(productsList[1].id, "android")
        Assertions.assertEquals(productsList[1].price, 500)
    }

    @Test
    fun `XMLParser should create a list of 2 orders of each type`() {
        val fileContents: String =
            javaClass.getResource("two_instances_of_each_element.xml")?.readText() ?:
            throw FileNotFoundException("Could not open file")

        val ordersList: List<Order> = xmlParser.parseFileToOrdersList(fileContents)

        Assertions.assertEquals(6, ordersList.size)

        var currentOrder = ordersList[0]
        Assertions.assertEquals(currentOrder.type, "order")
        Assertions.assertEquals(currentOrder.orderId, "1")
        Assertions.assertEquals(currentOrder.userId, "1")
        Assertions.assertEquals(currentOrder.productId, "android")
        Assertions.assertEquals(currentOrder.amount, 5)

        currentOrder = ordersList[1]
        Assertions.assertEquals(currentOrder.type, "modify-order")
        Assertions.assertEquals(currentOrder.orderId, "1")
        Assertions.assertNull(currentOrder.userId)
        Assertions.assertNull(currentOrder.productId)
        Assertions.assertEquals(currentOrder.amount, 10)

        currentOrder = ordersList[2]
        Assertions.assertEquals(currentOrder.type, "cancel-order")
        Assertions.assertEquals(currentOrder.orderId, "1")
        Assertions.assertNull(currentOrder.userId)
        Assertions.assertNull(currentOrder.productId)
        Assertions.assertNull(currentOrder.amount)

        currentOrder = ordersList[3]
        Assertions.assertEquals(currentOrder.type, "cancel-order")
        Assertions.assertEquals(currentOrder.orderId, "2")
        Assertions.assertNull(currentOrder.userId)
        Assertions.assertNull(currentOrder.productId)
        Assertions.assertNull(currentOrder.amount)

        currentOrder = ordersList[4]
        Assertions.assertEquals(currentOrder.type, "order")
        Assertions.assertEquals(currentOrder.orderId, "5")
        Assertions.assertEquals(currentOrder.userId, "2")
        Assertions.assertEquals(currentOrder.productId, "iphone")
        Assertions.assertEquals(currentOrder.amount, 10)

        currentOrder = ordersList[5]
        Assertions.assertEquals(currentOrder.type, "modify-order")
        Assertions.assertEquals(currentOrder.orderId, "2")
        Assertions.assertNull(currentOrder.userId)
        Assertions.assertNull(currentOrder.productId)
        Assertions.assertEquals(currentOrder.amount, 400)
    }
}
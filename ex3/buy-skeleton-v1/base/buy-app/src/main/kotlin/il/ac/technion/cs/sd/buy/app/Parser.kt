package il.ac.technion.cs.sd.buy.app

interface Parser {
    fun parseFileToProductsList(xmlString: String): List<Product>
    fun parseFileToOrdersList(xmlString: String): List<Order>
}

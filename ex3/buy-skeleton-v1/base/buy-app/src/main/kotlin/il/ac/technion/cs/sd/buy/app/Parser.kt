package il.ac.technion.cs.sd.buy.app

interface Parser {
    fun parseFileToProductsList(fileName: String): List<Product>
    fun parseFileToOrdersList(fileName: String): List<Order>
}

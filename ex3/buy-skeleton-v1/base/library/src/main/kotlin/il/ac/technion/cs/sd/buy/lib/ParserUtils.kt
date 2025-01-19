package il.ac.technion.cs.sd.buy.lib

import com.gitlab.mvysny.konsumexml.*
import kotlinx.serialization.Serializable

@Serializable
data class Product(val type: String, val id: String, val price: Int) : KeyValueElement(id, price)
{
    companion object
    {
        fun xml(konsumer: Konsumer): Product
        {
            konsumer.checkCurrent("Product")
            val productId = konsumer.childText("id")
            val productPrice = konsumer.childText("price").toInt()
            return Product(productId, productPrice)
        }
    }
}

Json.decodeFromString<Product>(some string)

data

data class Reviewer(val id: String, val reviews: List<BookWithScore>) : KeyListOfValuesElement(id, reviews)
{
    companion object
    {
        fun xml(konsumer: Konsumer): Reviewer
        {
            konsumer.checkCurrent("Reviewer")
            val reviewerId = konsumer.attributes["Id"]
            val reviews = konsumer.children("Review")
            {
                BookWithScore.xml(this)
            }

            return Reviewer(reviewerId, reviews)
        }
    }
}
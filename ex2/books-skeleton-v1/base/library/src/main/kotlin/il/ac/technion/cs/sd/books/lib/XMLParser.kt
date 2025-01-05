package il.ac.technion.cs.sd.books.lib

import com.gitlab.mvysny.konsumexml.*

data class BookWithScore(val name: String, val score: Int) : KeyValueElement(name, score)
{
    companion object
    {
        fun xml(konsumer: Konsumer): BookWithScore
        {
            konsumer.checkCurrent("Review")
            val bookName = konsumer.childText("Id")
            val bookScore = konsumer.childText("Score").toInt()
            return BookWithScore(bookName, bookScore)
        }
    }
}

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

class XMLParser
{
    companion object
    {
        fun parseXMLFileToReviewsListByReviewer(xmlString: String) : List<Reviewer>
        {
            return xmlString.konsumeXml().use { konsumer ->
                konsumer.child("Root") {
                    this.children("Reviewer") {
                        Reviewer.xml(this)
                    }
                }
            }
        }
    }
}
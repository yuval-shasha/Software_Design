package il.ac.technion.cs.sd.books.lib

import com.gitlab.mvysny.konsumexml.*

data class BookWithScore(val name: String, val score: Int) : KeyValueElement(name, score)
{
    companion object
    {
        fun xml(konsumer: Konsumer) : BookWithScore
        {
            konsumer.checkCurrent("Review")
            return BookWithScore(konsumer.childText("Id"), konsumer.childText("Score").toInt())
        }
    }
}

data class Reviewer(val id: String, val reviews: List<BookWithScore>) : KeyListOfValuesElement(id, reviews)
{
    companion object
    {
        fun xml(konsumer: Konsumer) : Reviewer
        {
            konsumer.checkCurrent("Reviewer")
            return Reviewer(konsumer.attributes["id"], konsumer.children("Review") { BookWithScore.xml(this) })
        }
    }
}

class XMLParser
{
    companion object
    {
        fun parseXMLFileToReviewsListByReviewer(xmlString: String) : List<Reviewer>
        {
            return xmlString.konsumeXml().use { konsumer -> konsumer.child("Root") { konsumer.children("Reviewer") { Reviewer.xml(this) } } }
        }
    }
}
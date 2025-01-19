package il.ac.technion.cs.sd.buy.lib

import com.gitlab.mvysny.konsumexml.*

class XMLParser
{
    companion object
    {
        fun parseXMLFileToReviewsListByReviewer(xmlString: String) : List<Reviewer>
        {
            return xmlString.konsumeXml().use { konsumer ->
                konsumer.child("Root") {
                    this.children("Product") {
                        Reviewer.xml(this)
                    }
                }
            }
        }
    }
}
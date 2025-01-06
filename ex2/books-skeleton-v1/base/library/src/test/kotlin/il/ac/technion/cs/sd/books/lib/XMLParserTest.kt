package il.ac.technion.cs.sd.books.lib

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

class XMLParserTest
{
    @Test
    fun `XMLParser should create a list of 2 reviewers where every reviewer has only one reviewed book`()
    {
        val fileContents: String =
            StorageLibrary::class.java.classLoader.getResource("one_review_per_reviewer.xml")?.readText() ?:
            throw FileNotFoundException("Could not open file")

        val reviewersList: List<Reviewer> = XMLParser.parseXMLFileToReviewsListByReviewer(fileContents)

        Assertions.assertEquals(2, reviewersList.size)
        for (reviewer in reviewersList)
        {
            Assertions.assertEquals(1, reviewer.reviews.size)
        }
    }

    @Test
    fun `check correctness of data parsed by XMLParser where every reviewer has only one reviewed book`()
    {
        val fileContents: String =
            StorageLibrary::class.java.classLoader.getResource("one_review_per_reviewer.xml")?.readText() ?:
            throw FileNotFoundException("Could not open file")

        val reviewersList: List<Reviewer> = XMLParser.parseXMLFileToReviewsListByReviewer(fileContents)

        Assertions.assertEquals(reviewersList[0].reviews[0].name, "Foobar")
        Assertions.assertEquals(reviewersList[0].reviews[0].score, 10)
        Assertions.assertEquals(reviewersList[1].reviews[0].name, "Boobar")
        Assertions.assertEquals(reviewersList[1].reviews[0].score, 5)
    }

    @Test
    fun `XMLParser should create a list of 2 reviewers where every reviewer has multiple reviewed books`()
    {
        val fileContents: String =
            StorageLibrary::class.java.classLoader.getResource("multiple_reviews_per_reviewer.xml")?.readText() ?:
            throw FileNotFoundException("Could not open file")

        val reviewersList: List<Reviewer> = XMLParser.parseXMLFileToReviewsListByReviewer(fileContents)

        Assertions.assertEquals(2, reviewersList.size)
        Assertions.assertEquals(2, reviewersList[0].reviews.size)
        Assertions.assertEquals(3, reviewersList[1].reviews.size)
    }

    @Test
    fun `check correctness of data parsed by XMLParser where every reviewer has multiple reviewed books`()
    {
        val fileContents: String =
            StorageLibrary::class.java.classLoader.getResource("multiple_reviews_per_reviewer.xml")?.readText() ?:
            throw FileNotFoundException("Could not open file")

        val reviewersList: List<Reviewer> = XMLParser.parseXMLFileToReviewsListByReviewer(fileContents)

        Assertions.assertEquals(reviewersList[0].reviews[0].name, "Foobar")
        Assertions.assertEquals(reviewersList[0].reviews[0].score, 10)
        Assertions.assertEquals(reviewersList[0].reviews[1].name, "Boobar")
        Assertions.assertEquals(reviewersList[0].reviews[1].score, 4)
        Assertions.assertEquals(reviewersList[1].reviews[0].name, "Moobar")
        Assertions.assertEquals(reviewersList[1].reviews[0].score, 5)
        Assertions.assertEquals(reviewersList[1].reviews[1].name, "Koobar")
        Assertions.assertEquals(reviewersList[1].reviews[1].score, 7)
        Assertions.assertEquals(reviewersList[1].reviews[2].name, "Soobar")
        Assertions.assertEquals(reviewersList[1].reviews[2].score, 8)
    }
}
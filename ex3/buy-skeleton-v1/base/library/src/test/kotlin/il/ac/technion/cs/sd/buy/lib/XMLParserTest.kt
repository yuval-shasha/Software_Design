package il.ac.technion.cs.sd.buy.lib

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

class XMLParserTest
{
    @Test
    fun `XMLParser should create a list of 2 products`()
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
    fun `check correctness of data parsed by XMLParser for 2 products`()
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
    fun `XMLParser should create a list of 2 orders`()
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
    fun `check correctness of data parsed by XMLParser for 2 orders`()
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
    fun `XMLParser should create a list of 2 modified orders`()
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
    fun `check correctness of data parsed by XMLParser for 2 modified orders`()
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
    fun `XMLParser should create a list of 2 cancelled orders`()
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
    fun `check correctness of data parsed by XMLParser for 2 cancelled orders`()
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
}
package il.ac.technion.cs.sd.books.test

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.sd.books.app.BookScoreInitializer
import il.ac.technion.cs.sd.books.app.BookScoreModule
import il.ac.technion.cs.sd.books.app.BookScoreReader
import il.ac.technion.cs.sd.books.external.LineStorageModule
import il.ac.technion.cs.sd.books.external.LineStorageFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import io.mockk.*

class BookScoreReaderImplTest
{
    private lateinit var lineStorageFactory: LineStorageFactory

    private fun getScoresReader(fileName: String): BookScoreReader {
        val fileContents: String =
            javaClass.getResource(fileName)?.readText() ?:
            throw FileNotFoundException("Could not open file $fileName")

        val injector = Guice.createInjector(BookScoreModule(), LineStorageModule())
        lineStorageFactory = injector.getInstance<LineStorageFactory>()
        injector.getInstance<BookScoreInitializer>().setup(fileContents)
        verify (exactly = 1) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 1) {
            lineStorageFactory.open("books")
        }
        return injector.getInstance<BookScoreReader>()
    }

    @Test
    fun `gaveReview with a valid review should return true`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertTrue(reader.gaveReview("1", "Boobar"))

        verify (exactly = 2) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 2) {
            lineStorageFactory.open("books")
        }
    }

    @Test
    fun `gaveReview with an invalid reviewer should return false`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertFalse(reader.gaveReview("3", "Boobar"))
    }

    @Test
    fun `gaveReview with an invalid book should return false`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertFalse(reader.gaveReview("1", "Hoobar"))
    }

    @Test
    fun `gaveReview with an invalid book and reviewer should return false`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertFalse(reader.gaveReview("aaa", "Hoobar"))
    }

    @Test
    fun `getScore with valid reviewer and book should return the correct score (most recent)`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(7, reader.getScore("1", "Foobar"))

        verify (exactly = 2) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 2) {
            lineStorageFactory.open("books")
        }
    }

    @Test
    fun `getScore with invalid reviewer should return null`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertNull(reader.getScore("7", "Foobar"))
    }

    @Test
    fun `getScore with invalid book should return null`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertNull(reader.getScore("1", "Yuval"))
    }

    @Test
    fun `getScore with invalid book and reviewer should return null`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertNull(reader.getScore("6", "Yuval"))
    }

    @Test
    fun `getReviewedBooks with valid reviewer should return a list of all the books reviewed by the reviewer`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(listOf("Foobar" , "Koobar" , "Soobar"), reader.getReviewedBooks("2"))

        verify (exactly = 2) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 2) {
            lineStorageFactory.open("books")
        }
    }

    @Test
    fun `getReviewedBooks with invalid reviewer should return empty list`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(emptyList<String>() , reader.getReviewedBooks("3"))
    }

    @Test
    fun `getAllReviewsByReviewer with valid reviewer should return a map of all the books and their score by the reviewer`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(mapOf("Boobar" to 4 , "Foobar" to 7) , reader.getAllReviewsByReviewer("1"))

        verify (exactly = 2) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 2) {
            lineStorageFactory.open("books")
        }
    }

    @Test
    fun `getAllReviewsByReviewer with invalid reviewer should return empty map`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(emptyMap<String , Int>() , reader.getAllReviewsByReviewer("Shoko"))
    }

    @Test
    fun `getAverageScoreForReviewer with valid reviewer should return the average score of all reviews by reviewer`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(5.5 , reader.getAverageScoreForReviewer("1"))

        verify (exactly = 2) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 2) {
            lineStorageFactory.open("books")
        }
    }

    @Test
    fun `getAverageScoreForReviewer with invalid reviewer should return null`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertNull(reader.getAverageScoreForReviewer("hi"))
    }

    @Test
    fun `getReviewers with valid book should return a list of all the reviewers of the book`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(listOf("1" , "2") , reader.getReviewers("Foobar"))

        verify (exactly = 2) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 2) {
            lineStorageFactory.open("books")
        }
    }

    @Test
    fun `getReviewers with invalid book should return empty List`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(emptyList<String>() , reader.getReviewers("bye"))
    }

    @Test
    fun `getReviewsForBook with valid book should return a map of all the reviewers and their score for the book`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(mapOf("1" to 7 , "2" to 5) , reader.getReviewsForBook("Foobar"))

        verify (exactly = 2) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 2) {
            lineStorageFactory.open("books")
        }
    }

    @Test
    fun `getReviewsForBook with invalid book should return empty map`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(emptyMap<String , Int>() , reader.getReviewsForBook("g"))
    }

    @Test
    fun `getAverageScoreForBook with valid book should return the average scores given by all reviewers to the book`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertEquals(6.0 , reader.getAverageScoreForBook("Foobar"))

        verify (exactly = 2) {
            lineStorageFactory.open("reviewers")
        }
        verify (exactly = 2) {
            lineStorageFactory.open("books")
        }
    }

    @Test
    fun `getAverageScoreForBook with invalid book should return null`() {
        val reader = getScoresReader("mega_test.xml")

        Assertions.assertNull(reader.getAverageScoreForBook("lalala"))
    }
}
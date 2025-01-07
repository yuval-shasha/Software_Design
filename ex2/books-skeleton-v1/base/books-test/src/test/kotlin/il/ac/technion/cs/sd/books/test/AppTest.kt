package il.ac.technion.cs.sd.books.test

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.sd.books.app.BookScoreInitializer
import il.ac.technion.cs.sd.books.app.BookScoreModule
import il.ac.technion.cs.sd.books.app.BookScoreReader
import il.ac.technion.cs.sd.books.external.LineStorageModule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.io.FileNotFoundException
import java.time.Duration

class AppTest {
    private fun getScoresReader(fileName: String): BookScoreReader {
        val fileContents: String =
            javaClass.getResource(fileName)?.readText() ?:
            throw FileNotFoundException("Could not open file $fileName")

        val injector = Guice.createInjector(BookScoreModule(), LineStorageModule())
        injector.getInstance<BookScoreInitializer>().setup(fileContents)
        return injector.getInstance<BookScoreReader>()
    }

    @Test
    fun `first five Reader methods should finish in under 30 seconds`() {
        val reader = getScoresReader("mega_test.xml")
        assertTimeoutPreemptively(Duration.ofSeconds(30)) {
            Assertions.assertTrue(reader.gaveReview("1", "Boobar"))
            Assertions.assertEquals(7, reader.getScore("1", "Foobar"))
            Assertions.assertEquals(listOf("Foobar" , "Koobar" , "Soobar"), reader.getReviewedBooks("2"))
            Assertions.assertEquals(mapOf("Boobar" to 4 , "Foobar" to 7) , reader.getAllReviewsByReviewer("1"))
            Assertions.assertEquals(5.5 , reader.getAverageScoreForReviewer("1"))
        }
    }

    @Test
    fun `last five Reader methods should finish in under 30 seconds`() {
        val reader = getScoresReader("mega_test.xml")
        assertTimeoutPreemptively(Duration.ofSeconds(30)) {
            Assertions.assertEquals(mapOf("Boobar" to 4 , "Foobar" to 7) , reader.getAllReviewsByReviewer("1"))
            Assertions.assertEquals(5.5 , reader.getAverageScoreForReviewer("1"))
            Assertions.assertEquals(listOf("1" , "2") , reader.getReviewers("Foobar"))
            Assertions.assertEquals(mapOf("1" to 7 , "2" to 5) , reader.getReviewsForBook("Foobar"))
            Assertions.assertEquals(6.0 , reader.getAverageScoreForBook("Foobar"))
        }
    }
}

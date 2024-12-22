package il.ac.technion.cs.sd.books.test

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

import java.io.FileNotFoundException

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance


import il.ac.technion.cs.sd.books.app.BookScoreReader
import il.ac.technion.cs.sd.books.app.BookScoreInitializer
import il.ac.technion.cs.sd.books.app.BookScoreModule
import il.ac.technion.cs.sd.books.external.LineStorageModule


class ExampleTest {

    private fun getScoresReader(fileName: String): BookScoreReader {
        val fileContents: String =
            javaClass.getResource(fileName)?.readText() ?:
            throw FileNotFoundException("Could not open file $fileName")

        val injector = Guice.createInjector(BookScoreModule(), LineStorageModule())
        injector.getInstance<BookScoreInitializer>().setup(fileContents)
        return injector.getInstance<BookScoreReader>()
    }

    @Test
    fun `simple example test`() {
        val reader = getScoresReader("small.xml")
        Assertions.assertEquals(listOf("Boobar", "Foobar", "Moobar"), reader.getReviewedBooks("123"))
        Assertions.assertEquals(6.0, reader.getAverageScoreForReviewer("123"))
        Assertions.assertEquals(10.0, reader.getAverageScoreForBook("Foobar"))
    }


}
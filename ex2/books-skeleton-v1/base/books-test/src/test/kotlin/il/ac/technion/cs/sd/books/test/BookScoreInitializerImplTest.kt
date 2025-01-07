package il.ac.technion.cs.sd.books.test

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.sd.books.app.BookScoreInitializerImpl
import il.ac.technion.cs.sd.books.app.BookScoreModule
import il.ac.technion.cs.sd.books.external.LineStorageFactory
import il.ac.technion.cs.sd.books.external.LineStorageModule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

class BookScoreInitializerImplTest
{
    private val lineStorageFactory = Guice.createInjector(BookScoreModule(), LineStorageModule()).getInstance<LineStorageFactory>()

    private fun getFileContents(fileName: String) : String {
        return javaClass.getResource(fileName)?.readText() ?:
        throw FileNotFoundException("Could not open file $fileName")
    }

    @Test
    fun `each reviewer has scored only one book and each book only been scored by one reviewer` ()
    {
        val fileContent = getFileContents("multiple_reviews_per_reviewer.xml")
        val bookScoreInitializer = BookScoreInitializerImpl(lineStorageFactory)

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = bookScoreInitializer.getReviewersDB().getDatabaseAsArrayList()
        val booksDB = bookScoreInitializer.getBooksDB().getDatabaseAsArrayList()

        Assertions.assertEquals("1" , reviewersDB[0] )
        Assertions.assertEquals("Boobar 4 Foobar 10" , reviewersDB[1])
        Assertions.assertEquals("2" , reviewersDB[2] )
        Assertions.assertEquals("Koobar 7 Moobar 5 Soobar 8" , reviewersDB[3])
        Assertions.assertEquals("Boobar" , booksDB[0])
        Assertions.assertEquals("1 4" , booksDB[1])
        Assertions.assertEquals("Foobar" , booksDB[2])
        Assertions.assertEquals("1 10" , booksDB[3])
        Assertions.assertEquals("Koobar" , booksDB[4])
        Assertions.assertEquals("2 7" , booksDB[5])
        Assertions.assertEquals("Moobar" , booksDB[6])
        Assertions.assertEquals("2 5" , booksDB[7])
        Assertions.assertEquals("Soobar" , booksDB[8])
        Assertions.assertEquals("2 8" , booksDB[9])
    }

    @Test
    fun `each reviewer appears only once and reviewed some books more than once in the same scope, no book was reviewed by more than one reviewer` ()
    {
        val fileContent = getFileContents("multiple_double_reviews_per_reviewer.xml")
        val bookScoreInitializer = BookScoreInitializerImpl(lineStorageFactory)

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = bookScoreInitializer.getReviewersDB().getDatabaseAsArrayList()
        val booksDB = bookScoreInitializer.getBooksDB().getDatabaseAsArrayList()

        Assertions.assertEquals(2, reviewersDB.size / 2)
        Assertions.assertEquals(5, booksDB.size / 2)
        Assertions.assertEquals("1", reviewersDB[0])
        Assertions.assertEquals("Boobar 4 Foobar 4", reviewersDB[1])
        Assertions.assertEquals("2", reviewersDB[2])
        Assertions.assertEquals("Koobar 10 Moobar 5 Soobar 8", reviewersDB[3])
        Assertions.assertEquals("Boobar" , booksDB[0])
        Assertions.assertEquals("1 4" , booksDB[1])
        Assertions.assertEquals("Foobar" , booksDB[2])
        Assertions.assertEquals("1 4" , booksDB[3])
        Assertions.assertEquals("Koobar" , booksDB[4])
        Assertions.assertEquals("2 10" , booksDB[5])
        Assertions.assertEquals("Moobar" , booksDB[6])
        Assertions.assertEquals("2 5" , booksDB[7])
        Assertions.assertEquals("Soobar" , booksDB[8])
        Assertions.assertEquals("2 8" , booksDB[9])
    }

    @Test
    fun `some reviewers appear more than once and reviewed some books more than once, in different scopes, no book was reviewed by more than one reviewer`()
    {
        val fileContent = getFileContents("reviews_in_different_scopes.xml")
        val bookScoreInitializer = BookScoreInitializerImpl(lineStorageFactory)

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = bookScoreInitializer.getReviewersDB().getDatabaseAsArrayList()
        val booksDB = bookScoreInitializer.getBooksDB().getDatabaseAsArrayList()

        Assertions.assertEquals(2 , reviewersDB.size / 2)
        Assertions.assertEquals(5 , booksDB.size / 2)
        Assertions.assertEquals("1" , reviewersDB[0])
        Assertions.assertEquals("Boobar 4 Foobar 2", reviewersDB[1])
        Assertions.assertEquals("2" , reviewersDB[2])
        Assertions.assertEquals("Koobar 7 Moobar 5 Soobar 8" , reviewersDB[3])
        Assertions.assertEquals("Boobar" , booksDB[0])
        Assertions.assertEquals("1 4" , booksDB[1])
        Assertions.assertEquals("Foobar" , booksDB[2])
        Assertions.assertEquals("1 2" , booksDB[3])
        Assertions.assertEquals("Koobar" , booksDB[4])
        Assertions.assertEquals("2 7" , booksDB[5])
        Assertions.assertEquals("Moobar" , booksDB[6])
        Assertions.assertEquals("2 5" , booksDB[7])
        Assertions.assertEquals("Soobar" , booksDB[8])
        Assertions.assertEquals("2 8" , booksDB[9])
    }

    @Test
    fun `each reviewer appears only once and reviews the same book once`() {
        val fileContent = getFileContents("book_reviewed_by_all_reviewers.xml")
        val bookScoreInitializer = BookScoreInitializerImpl(lineStorageFactory)

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = bookScoreInitializer.getReviewersDB().getDatabaseAsArrayList()
        val booksDB = bookScoreInitializer.getBooksDB().getDatabaseAsArrayList()

        Assertions.assertEquals(3, reviewersDB.size / 2)
        Assertions.assertEquals(1 , booksDB.size / 2)
        Assertions.assertEquals("1", reviewersDB[0])
        Assertions.assertEquals("Foobar 10", reviewersDB[1])
        Assertions.assertEquals("2", reviewersDB[2])
        Assertions.assertEquals("Foobar 5", reviewersDB[3])
        Assertions.assertEquals("3", reviewersDB[4])
        Assertions.assertEquals("Foobar 2", reviewersDB[5])
        Assertions.assertEquals("Foobar", booksDB[0])
        Assertions.assertEquals("1 10 2 5 3 2", booksDB[1])
    }

    @Test
    fun `each reviewer appears once, some books reviewed more than once in the same scope and by multiple reviewers`() {
        val fileContent = getFileContents("same_book_multiple_times_in_scope.xml")
        val bookScoreInitializer = BookScoreInitializerImpl(lineStorageFactory)

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = bookScoreInitializer.getReviewersDB().getDatabaseAsArrayList()
        val booksDB = bookScoreInitializer.getBooksDB().getDatabaseAsArrayList()

        Assertions.assertEquals(2, reviewersDB.size / 2)
        Assertions.assertEquals(3 , booksDB.size / 2)
        Assertions.assertEquals("1", reviewersDB[0])
        Assertions.assertEquals("Foobar 4", reviewersDB[1])
        Assertions.assertEquals("2", reviewersDB[2])
        Assertions.assertEquals("Foobar 5 Koobar 7 Soobar 8", reviewersDB[3])
        Assertions.assertEquals("Foobar" , booksDB[0])
        Assertions.assertEquals("1 4 2 5" , booksDB[1])
        Assertions.assertEquals("Koobar" , booksDB[2])
        Assertions.assertEquals("2 7" , booksDB[3])
        Assertions.assertEquals("Soobar" , booksDB[4])
        Assertions.assertEquals("2 8" , booksDB[5])
    }

    @Test
    fun `some reviewers appear more than once and reviewed each book only once, no book was reviewed by more than one reviewer` ()
    {
        val fileContent = getFileContents("books_appear_once_reviewers_appear_more.xml")
        val bookScoreInitializer = BookScoreInitializerImpl(lineStorageFactory)

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = bookScoreInitializer.getReviewersDB().getDatabaseAsArrayList()
        val booksDB = bookScoreInitializer.getBooksDB().getDatabaseAsArrayList()

        Assertions.assertEquals("1" , reviewersDB[0])
        Assertions.assertEquals("Boobar 4 Foobar 10 Roobar 4" , reviewersDB[1])
        Assertions.assertEquals("2" , reviewersDB[2])
        Assertions.assertEquals("Loobar 7 Moobar 5" , reviewersDB[3])
        Assertions.assertEquals("Boobar" , booksDB[0])
        Assertions.assertEquals("1 4" , booksDB[1])
        Assertions.assertEquals("Foobar" , booksDB[2])
        Assertions.assertEquals("1 10" , booksDB[3])
        Assertions.assertEquals("Loobar" , booksDB[4])
        Assertions.assertEquals("2 7" , booksDB[5])
        Assertions.assertEquals("Moobar" , booksDB[6])
        Assertions.assertEquals("2 5" , booksDB[7])
        Assertions.assertEquals("Roobar" , booksDB[8])
        Assertions.assertEquals("1 4" , booksDB[9])
    }

    @Test
    fun `everything, everywhere, all at once`() {
        val fileContent = getFileContents("mega_test.xml")
        val bookScoreInitializer = BookScoreInitializerImpl(lineStorageFactory)

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = bookScoreInitializer.getReviewersDB().getDatabaseAsArrayList()
        val booksDB = bookScoreInitializer.getBooksDB().getDatabaseAsArrayList()

        Assertions.assertEquals(2, reviewersDB.size / 2)
        Assertions.assertEquals(4 , booksDB.size / 2)
        Assertions.assertEquals("1", reviewersDB[0])
        Assertions.assertEquals("Boobar 4 Foobar 7" , reviewersDB[1])
        Assertions.assertEquals("2", reviewersDB[2])
        Assertions.assertEquals("Foobar 5 Koobar 7 Soobar 8" , reviewersDB[3])
        Assertions.assertEquals("Boobar" , booksDB[0])
        Assertions.assertEquals("1 4" , booksDB[1])
        Assertions.assertEquals("Foobar" , booksDB[2])
        Assertions.assertEquals("1 7 2 5" , booksDB[3])
        Assertions.assertEquals("Koobar" , booksDB[4])
        Assertions.assertEquals("2 7" , booksDB[5])
        Assertions.assertEquals("Soobar" , booksDB[6])
        Assertions.assertEquals("2 8" , booksDB[7])
    }
}
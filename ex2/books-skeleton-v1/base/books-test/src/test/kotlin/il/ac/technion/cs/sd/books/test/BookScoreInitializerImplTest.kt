package il.ac.technion.cs.sd.books.test

import com.google.inject.Guice
import com.google.inject.Key
import com.google.inject.name.Names
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.sd.books.app.BookScoreInitializer
import il.ac.technion.cs.sd.books.app.BookScoreModule
import il.ac.technion.cs.sd.books.external.LineStorageModule
import il.ac.technion.cs.sd.books.lib.StorageLibrary
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import kotlin.collections.count

class BookScoreInitializerImplTest
{
    private fun getFileContents(fileName: String) : String {
        return javaClass.getResource(fileName)?.readText() ?:
        throw FileNotFoundException("Could not open file $fileName")
    }

    @Test
    fun `each reviewer has scored only one book and each book only been scored by one reviewer` ()
    {
        val fileContent = getFileContents("multiple_reviews_per_reviewer.xml")
        val injector = Guice.createInjector(LineStorageModule(), BookScoreModule())
        val bookScoreInitializer = injector.getInstance<BookScoreInitializer>()

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = injector.getInstance(Key.get(StorageLibrary::class.java, Names.named("ReviewersDB")))
        val booksDB = injector.getInstance(Key.get(StorageLibrary::class.java, Names.named("BooksDB")))
        val reviewerDBasArrayList = reviewersDB.getDatabaseAsArrayList()
        val booksDBasArrayList = booksDB.getDatabaseAsArrayList()


        Assertions.assertEquals(1 , reviewerDBasArrayList.count { it == "1" })
        Assertions.assertEquals(1 , reviewerDBasArrayList.count { it == "2" })
        Assertions.assertEquals(1 , booksDBasArrayList.count { it == "Foobar" })
        Assertions.assertEquals(1 , booksDBasArrayList.count { it == "Boobar" })
        Assertions.assertEquals(1 , booksDBasArrayList.count { it == "Moobar" })
        Assertions.assertEquals(1 , booksDBasArrayList.count { it == "Koobar" })
        Assertions.assertEquals(1 , booksDBasArrayList.count { it == "Soobar" })
    }

    @Test
    fun `each reviewer appears only once and reviewed some books more than once in the same scope, no book was reviewed by more than one reviewer` ()
    {
        val fileContent = getFileContents("multiple_double_reviews_per_reviewer.xml")
        val injector = Guice.createInjector(BookScoreModule(), LineStorageModule())
        val bookScoreInitializer = injector.getInstance<BookScoreInitializer>()

        bookScoreInitializer.setup(fileContent)
        val reviewersDB = injector
            .getInstance(Key.get(StorageLibrary::class.java, Names.named("ReviewersDB")))
            .getDatabaseAsArrayList()
        val booksDB = injector
            .getInstance(Key.get(StorageLibrary::class.java, Names.named("BooksDB")))
            .getDatabaseAsArrayList()

        Assertions.assertEquals(2, reviewersDB.size / 2)
        Assertions.assertEquals(5, booksDB.size / 2)
        Assertions.assertEquals("1", reviewersDB[0])
        Assertions.assertEquals("Boobar 4 Foobar 4", reviewersDB[1])
        Assertions.assertEquals("2", reviewersDB[2])
        Assertions.assertEquals("Koobar 10 Moobar 5 Soobar 8", reviewersDB[3])
    }

    // Check that data where each reviewer appears only once and reviewed some books more than once in different scopes, no book was reviewed by more than one reviewer

    // Check that data where each reviewer appears only once and reviewed each book only once, some books were reviewed by more than one reviewer

    // Check that data where each reviewer appears only once and reviewed some books more than once in the same scope, some books were reviewed by more than one reviewer

    // Check that data where each reviewer appears only once and reviewed some books more than once in different scopes, some books were reviewed by more than one reviewer

    // Check that data where some reviewers appear more than once and reviewed each book only once, no book was reviewed by more than one reviewer

    // Check that data where some reviewers appear more than once and reviewed some books more than once in the same scope, no book was reviewed by more than one reviewer

    // Check that data where some reviewers appear more than once and reviewed some books more than once in different scopes, no book was reviewed by more than one reviewer

    // Check that data where some reviewers appear more than once and reviewed each book only once, some books were reviewed by more than one reviewer

    // Check that data where some reviewers appear more than once and reviewed some books more than once in the same scope, some books were reviewed by more than one reviewer

    // Check that data where some reviewers appear more than once and reviewed some books more than once in different scopes, some books were reviewed by more than one reviewer

    // Check that the open method in LineStorageFactory is called only once per file when using the setup method
}
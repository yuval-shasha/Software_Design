package il.ac.technion.cs.sd.books.app

import com.google.inject.name.Named
import il.ac.technion.cs.sd.books.lib.*
import java.util.*

class BookScoreInitializerImpl : BookScoreInitializer
{
    @Named("ReviewersDB")
    lateinit var reviewersDB: StorageLibrary

    @Named("BooksDB")
    lateinit var booksDB: StorageLibrary

    // Merges all the sub-keys of each key.
    private fun Sequence<Reviewer>.mergeAllBooksReviewedByReviewer() : Sequence<Reviewer> =
        this
            .groupBy { it.id }
            .asSequence()
            .map { (id, booksWithSameReviewer) ->
                val concatenatedBooks = booksWithSameReviewer
                    .asSequence()
                    .flatMap { it.reviews.asSequence() }
                    .toList()

                Reviewer(id, concatenatedBooks)
            }

    // Removes all the duplicates of values for the sub-key by each key.
    // Only the last value of each sub-key is left in the sequence.
    private fun Sequence<Reviewer>.removeDuplicateBooksByReviewer() : Sequence<Reviewer> =
        this
            .map { reviewer ->
                val uniqueBooksList = reviewer.reviews
                    .asSequence()
                    .associateBy { it.name }
                    .values
                    .toList()

                Reviewer(reviewer.id, reviewer.reviews)
            }

    // Creates a list of books mapped to pairs of reviewers and scores that each reviewer gave the book
    private fun createBooksList(reviewersList: List<Reviewer>) : List<KeyListOfValuesElement>
    {
        val booksMap = HashMap<String, MutableList<KeyValueElement>>()
        reviewersList
            .forEach { reviewer ->
                reviewer.reviews
                    .forEach { review ->
                        booksMap[review.name]
                            ?.add(KeyValueElement(reviewer.id, review.score))
                    }
            }
        val booksList = ArrayList<KeyListOfValuesElement>()
        booksMap.forEach {
            booksList.add(KeyListOfValuesElement(it.key, it.value))
        }
        return booksList
    }

    override fun setup(xmlData: String)
    {
        val reviewersList = XMLParser.parseXMLFileToReviewsListByReviewer(xmlData)
        reviewersList
            .asSequence()
            .mergeAllBooksReviewedByReviewer()
            .removeDuplicateBooksByReviewer()
            .toList()

        val booksList = createBooksList(reviewersList)

        reviewersDB.createDatabase(xmlData)
        reviewersDB.initializeDatabase(reviewersList)

        booksDB.createDatabase(xmlData)
        booksDB.initializeDatabase(booksList)
    }
}
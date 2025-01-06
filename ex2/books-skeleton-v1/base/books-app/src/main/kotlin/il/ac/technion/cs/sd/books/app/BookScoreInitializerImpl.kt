package il.ac.technion.cs.sd.books.app

import com.google.inject.Inject
import com.google.inject.name.Named
import il.ac.technion.cs.sd.books.lib.*
import java.util.*

class BookScoreInitializerImpl : BookScoreInitializer
{
    @Inject
    @Named("ReviewersDB")
    private lateinit var reviewersDB: StorageLibrary

    @Inject
    @Named("BooksDB")
    private lateinit var booksDB: StorageLibrary

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

    // TODO: debug this func, need to understand why the booksList is empty
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
        var reviewersList = XMLParser.parseXMLFileToReviewsListByReviewer(xmlData)
        reviewersList = reviewersList
            .asSequence()
            .mergeAllBooksReviewedByReviewer()
            .removeDuplicateBooksByReviewer()
            .toList()

        val booksList = createBooksList(reviewersList)

        reviewersDB.createDatabase("reviewersDB")
        reviewersDB.initializeDatabase(reviewersList)

        booksDB.createDatabase("booksDB")
        booksDB.initializeDatabase(booksList)
    }
}
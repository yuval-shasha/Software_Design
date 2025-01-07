package il.ac.technion.cs.sd.books.app

import com.google.inject.Inject
import il.ac.technion.cs.sd.books.external.LineStorageFactory
import il.ac.technion.cs.sd.books.lib.*
import java.util.*

class BookScoreInitializerImpl @Inject constructor(lineStorageFactory: LineStorageFactory) : BookScoreInitializer
{
    private var reviewersDB = StorageLibrary(lineStorageFactory, "reviewers")
    private var booksDB = StorageLibrary(lineStorageFactory, "books")

    // Merges all the sub-keys of each key.
    private fun Sequence<Reviewer>.mergeAllBooksReviewedByReviewer() : Sequence<Reviewer> =
        this
            .groupBy { it.id }
            .asSequence()
            .map { (id, reviewerWithSameIdList) ->
                val concatenatedBooks = reviewerWithSameIdList
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
                val noDuplicateReviews = reviewer.reviews
                    .associateBy { it.name }
                    .map { (id, bookWithScore) ->
                        bookWithScore
                    }
                    .toList()

                Reviewer(reviewer.id, noDuplicateReviews)
            }

    // Creates a list of books mapped to pairs of reviewers and scores that each reviewer gave the book
    private fun createBooksList(reviewersList: List<Reviewer>) : List<KeyListOfValuesElement>
    {
        val booksMap = HashMap<String, MutableList<KeyValueElement>>()
        reviewersList
            .forEach { reviewer ->
                reviewer.reviews
                    .forEach { review ->
                        val list = booksMap.getOrPut(review.name) { mutableListOf() }
                        list.add(KeyValueElement(reviewer.id, review.score))
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

        reviewersDB.initializeDatabase(reviewersList)
        booksDB.initializeDatabase(booksList)
    }

    fun getReviewersDB() : StorageLibrary {
        return reviewersDB
    }

    fun getBooksDB() : StorageLibrary {
        return booksDB
    }
}
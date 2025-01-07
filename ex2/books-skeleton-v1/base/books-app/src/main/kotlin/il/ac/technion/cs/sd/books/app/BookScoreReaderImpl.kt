package il.ac.technion.cs.sd.books.app

import com.google.inject.Inject
import il.ac.technion.cs.sd.books.external.LineStorageFactory
import il.ac.technion.cs.sd.books.lib.StorageLibrary

class BookScoreReaderImpl @Inject constructor(lineStorageFactory: LineStorageFactory) : BookScoreReader
{
    private var reviewersDB = StorageLibrary(lineStorageFactory, "reviewers")
    private var booksDB = StorageLibrary(lineStorageFactory, "books")

    override fun gaveReview(reviewerId: String, bookId: String): Boolean
    {
        val reviewsByReviewer: Map<String,Int>? = reviewersDB.getDataAsMapFromMainKey(reviewerId)
        return reviewsByReviewer != null && reviewsByReviewer.containsKey(bookId)
    }

    override fun getScore(reviewerId: String, bookId: String): Int?
    {
        val reviewsByReviewer: Map<String,Int>? = reviewersDB.getDataAsMapFromMainKey(reviewerId)
        return reviewsByReviewer?.get(bookId)
    }

    override fun getReviewedBooks(reviewerId: String): List<String>
    {
        return reviewersDB.getDataAsMapFromMainKey(reviewerId)?.keys?.toList() ?: emptyList()
    }

    override fun getAllReviewsByReviewer(reviewerId: String): Map<String, Int>
    {
        return reviewersDB.getDataAsMapFromMainKey(reviewerId) ?: emptyMap()
    }

    override fun getAverageScoreForReviewer(reviewerId: String): Double?
    {
        val allReviewsByReviewer: Map<String,Int>? = reviewersDB.getDataAsMapFromMainKey(reviewerId)
        return allReviewsByReviewer
            ?.values
            ?.average()
    }

    override fun getReviewers(bookId: String): List<String>
    {
        return booksDB.getDataAsMapFromMainKey(bookId)?.keys?.toList() ?: emptyList()
    }

    override fun getReviewsForBook(bookId: String): Map<String, Int>
    {
        return booksDB.getDataAsMapFromMainKey(bookId) ?: emptyMap()
    }

    override fun getAverageScoreForBook(bookId: String): Double?
    {
        val allReviewsForBook: Map<String,Int>? = booksDB.getDataAsMapFromMainKey(bookId)
        return allReviewsForBook
            ?.values
            ?.average()
    }
}
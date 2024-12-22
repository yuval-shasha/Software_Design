package il.ac.technion.cs.sd.books.app

/**
 * This class will only be instantiated by kotlin-guice after
 * BookScoreInitializer.setup() has been called
 */
interface BookScoreReader {

    /**
     * Returns true iff a reviewer with ID reviewerId has reviewed the book
     * with ID bookId. If either the reviewer or the book does not exist,
     * returns false
     */
    fun gaveReview(reviewerId: String, bookId: String): Boolean

    /**
     * Returns the reviewer's review score for the book, if the reviewer
     * reviewed the book. If either the reviewer or the book does not exist,
     * or the reviewer did not review the book, returns null.
     */
    fun getScore(reviewerId: String, bookId: String): Int?

    /**
     * Returns a sorted (!) list of all the books the reviewer reviewed.
     * If the reviewer does not exist returns an empty list.
     */
    fun getReviewedBooks(reviewerId: String): List<String>

    /**
     * Returns the scores of all the books the reviewer reviewed, as a map from
     * the book ID to its score. If the reviewer does not exist returns an
     * empty map.
     */
    fun getAllReviewsByReviewer(reviewerId: String): Map<String, Int>

    /**
     * Returns the average score the reviewer gave, across all books reviewed
     * by him. If the reviewer does not exist, returns null.
     */
    fun getAverageScoreForReviewer(reviewerId: String): Double?

    /**
     * Returns a sorted (!) list of all the reviewers that reviewed the book.
     * If the book does not exist, returns an empty list.
     */
    fun getReviewers(bookId: String): List<String>

    /**
     * Returns the scores of all the reviewers who reviewed the book, as a map
     * from reviewer ID to their review score. If the book does not exist,
     * returns an empty map.
     */
    fun getReviewsForBook(bookId: String): Map<String, Int>

    /**
     * Returns the average review score of the book. If the book does not
     * exist, returns null.
     */
    fun getAverageScoreForBook(bookId: String): Double?

}
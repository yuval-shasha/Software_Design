package il.ac.technion.cs.sd.books.app

class BookScoreReaderImpl : BookScoreReader {
    override fun gaveReview(reviewerId: String, bookId: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getScore(reviewerId: String, bookId: String): Int? {
        TODO("Not yet implemented")
    }

    override fun getReviewedBooks(reviewerId: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun getAllReviewsByReviewer(reviewerId: String): Map<String, Int> {
        TODO("Not yet implemented")
    }

    override fun getAverageScoreForReviewer(reviewerId: String): Double? {
        TODO("Not yet implemented")
    }

    override fun getReviewers(bookId: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun getReviewsForBook(bookId: String): Map<String, Int> {
        TODO("Not yet implemented")
    }

    override fun getAverageScoreForBook(bookId: String): Double? {
        TODO("Not yet implemented")
    }
}
package il.ac.technion.cs.sd.books.lib

import il.ac.technion.cs.sd.books.external.LineStorage
import il.ac.technion.cs.sd.books.external.LineStorageFactory

class StorageLibrary(lineStorageFactory: LineStorageFactory, fileName: String)
{
    private var lineStorage : LineStorage? = null

    init
    {
        lineStorage = lineStorageFactory.open(fileName)
    }

    // TODO: Mia, go over this function. added this for better readability
   private fun binarySearchOnOddLines(id: String): String? {
       var left = 0
       var right = lineStorage?.numberOfLines()!! / 2
       while (left <= right) {
           val mid = (left + right) / 2
           val currentId = lineStorage?.read(mid * 2)
           if (currentId == id) {
               return lineStorage?.read(mid * 2 + 1)
           }
          if (currentId != null && currentId < id) {
            left = mid + 1
          } else {
            right = mid - 1
          }
       }
       return null
   }

    // TODO: Mia, go over this function
    // return the line in the LineStorage that contains the id's data
    // return null if the id is not found
    private fun searchForIdLineInLineStorage(id: String): String? {
        return binarySearchOnOddLines(id)
    }

    private fun Sequence<Reviewer>.mergeAllReviewsForReviewer() : Sequence<Reviewer> =
        this
            .groupBy { it.id }
            .asSequence()
            // concatenating the review of the same reviewer
            .map { (id, reviewersWithSameId) ->
                val concatenatedReviews = reviewersWithSameId
                    .asSequence()
                    .flatMap { it.reviews.asSequence() }
                    .toList()

                Reviewer(id, concatenatedReviews)
            }

    private fun Sequence<Reviewer>.removeDuplicateBooks() : Sequence<Reviewer> =
        this
            .map { reviewer ->
                val uniqueBooksList = reviewer.reviews
                    .asSequence()
                    .associateBy { it.name }
                    .values
                    .toList()

                Reviewer(reviewer.id, uniqueBooksList)
            }

    private fun Sequence<Reviewer>.sortEachBookListByName() : Sequence<Reviewer> =
        this
            .map { reviewer ->
                val sorted = reviewer.reviews
                    .sortedBy { it.name }
                Reviewer(reviewer.id, sorted)
            }

    // TODO: check correctness
    fun initializeDatabase(reviewerList : List<Reviewer>) {
        reviewerList.asSequence()
            .mergeAllReviewsForReviewer()
            .removeDuplicateBooks()
            // sorting the reviewer list by id
            .sortedBy {  it.id }
            .sortEachBookListByName()
            .toList()


            for (reviewer in reviewerList)
            {
                // returns null if lineStorage is null
                // if lineStorage is null, these calls will do nothing
                lineStorage?.appendLine(reviewer.id)
                lineStorage?.appendLine(reviewer.reviews.joinToString(" ") { "${it.name} ${it.score}" })
            }
    }


    // TODO: Mia, go over this function. changed the return type to Map<String, String>
    // data is stored in the following format:
    // <id1> <data1> <id2> <data2> ...
    fun getDataFromId(id: String): Map<String,String>? {
        val lineOfData = searchForIdLineInLineStorage(id) ?: return null
        val dataOfId = lineOfData
            .split(" ")
            .asSequence()
            .chunked(2)
            .associate { it[0] to it[1] }
            .toMap()
        return dataOfId
    }

    // TODO: check if really needed
    fun getDatabaseAsArrayList(): ArrayList<String> {
        val dataList = ArrayList<String>()
        val numberOfLines = lineStorage?.numberOfLines();
        for (line in 0..<numberOfLines!!) {
            val dataInLine = lineStorage?.read(line)
            if (dataInLine != null) {
                dataList.addLast(dataInLine)
            }
        }
        return dataList
    }
}
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

    // return the line in the LineStorage that starts with the provided id
    // return null if the id is not found
    private fun searchForIdLineInLineStorage(id: String): String? {
        var left = 0
        var right = lineStorage?.numberOfLines()!! - 1
        while (left <= right) {
            val mid = (left + right) / 2
            val currentLine = lineStorage?.read(mid)
            val currentId = currentLine?.split(",")!![0]
            if (currentId == id) {
                return currentLine
            }
            if (currentId < id) {
                left = mid + 1
            } else {
                right = mid - 1
            }
        }
        return null
    }

    fun initializeDatabase(reviewerList : List<Reviewer>) {
        reviewerList
            .asSequence()
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
            // removing duplicates of books with the same name
            .map { reviewer ->
                val uniqueBooksList = reviewer.reviews
                    .asSequence()
                    .associateBy { it.name }
                    .values
                    .toList()

                Reviewer(reviewer.id, uniqueBooksList)
            }
            .sortedBy {  it.id }
            // sorting each book list by name
            .map { reviewer ->
                val sorted = reviewer.reviews
                    .sortedBy { it.name }
                Reviewer(reviewer.id, sorted)
            }
            .toList()


            for (reviewer in reviewerList)
            {
                lineStorage.appendLine(reviewer.id)
                lineStorage.appendLine(reviewer.reviews.joinToString(" ") { "${it.name} ${it.score}" })
            }
    }

    fun getDataFromId(id: String): String? {
        val lineOfId = searchForIdLineInLineStorage(id) ?: return null
        val dataOfId = lineOfId.split(",")[1]
        return dataOfId
    }

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
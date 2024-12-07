package il.ac.technion.cs.sd.lib

import il.ac.technion.cs.sd.grades.external.LineStorage

/**
 * Implement your library here. Feel free to change the class name,
 * but note that if you choose to change the class name,
 * you will need to update the import statements in GradesInitializer.kt
 * and in GradesReader.kt.
 */
class StorageLibrary {
    companion object {
        // return the line in the LineStorage that starts with the item
        // return null if the item is not found
        private fun searchForItemLineInLineStorage(item: String): String? {
            var left = 0
            var right = LineStorage.numberOfLines() - 1
            while (left <= right) {
                val mid = (left + right) / 2
                val currentLine = LineStorage.read(mid)
                if (currentLine.startsWith(item)) {
                    return currentLine
                }
                val currItem = currentLine.split(",")[0]
                if (currItem < item) {
                    left = mid + 1
                } else {
                    right = mid - 1
                }
            }
            return null
        }

        fun initializeDatabase(data: String) {
            val linesList = data.lines()
                .reversed()
                .asSequence()
                .distinctBy { it.split(",")[0] }
                .sortedBy { it.split(",")[0] }
                .toList()

            for (line in linesList) {
                LineStorage.appendLine(line)
            }
        }

        fun getDataFromItem(item: String): String? {
            val lineOfItem = searchForItemLineInLineStorage(item) ?: return null
            val dataOfItem = lineOfItem.split(",")[1]
            return dataOfItem
        }
    }
}
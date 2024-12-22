package il.ac.technion.cs.sd.books.lib

class StorageLibrary
{
    class StorageLibrary private constructor(){
        companion object {
            // return the line in the LineStorage that starts with the provided id
            // return null if the id is not found
            private fun searchForIdLineInLineStorage(id: String): String? {
                var left = 0
                var right = LineStorage.numberOfLines() - 1
                while (left <= right) {
                    val mid = (left + right) / 2
                    val currentLine = LineStorage.read(mid)
                    val currentId = currentLine.split(",")[0]
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

            fun initializeDatabase(data: String) {
                val linesList = data.lines()
                    .reversed()
                    .asSequence()
                    .map { str -> str.replace("\\s".toRegex(), "") }
                    .distinctBy { it.split(",")[0] }
                    .sortedBy { it.split(",")[0] }
                    .toList()

                for (line in linesList) {
                    LineStorage.appendLine(line)
                }
            }

            fun getDataFromId(id: String): String? {
                val lineOfId = searchForIdLineInLineStorage(id) ?: return null
                val dataOfId = lineOfId.split(",")[1]
                return dataOfId
            }

            fun getDatabaseAsArrayList(): ArrayList<String> {
                val dataList = ArrayList<String>()
                val numberOfLines = LineStorage.numberOfLines();
                for (line in 0..<numberOfLines)
                {
                    val dataInLine = LineStorage.read(line)
                    dataList.addLast(dataInLine)
                }
                return dataList
            }

            fun clearLineStorage()
            {
                LineStorage.storedLines.clear()
            }
        }
    }
}
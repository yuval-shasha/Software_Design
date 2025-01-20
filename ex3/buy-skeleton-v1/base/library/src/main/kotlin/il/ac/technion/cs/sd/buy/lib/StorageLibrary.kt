package il.ac.technion.cs.sd.buy.lib

import com.google.inject.Inject
import il.ac.technion.cs.sd.buy.external.SuspendLineStorage
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import kotlinx.serialization.Serializable
import kotlinx.coroutines.*

@Serializable
open class KeyWithTwoDataLists(val key: String, val dataList1: List<String>, val dataList2: List<String>)

class StorageLibrary @Inject constructor(private val suspendLineStorageFactory: SuspendLineStorageFactory, fileName: String) {
    private var suspendLineStorage : SuspendLineStorage

    init {
        runBlocking {
            suspendLineStorage = suspendLineStorageFactory.open(fileName)
        }
    }

    // Returns the index of the line in SuspendLineStorage that contains the provided key.
    // Returns null if the provided id is not found.
    private suspend fun binarySearchOnEvenLines(key: String): Int?
    {
       var left = 0
       var right = suspendLineStorage.numberOfLines() / 2 - 1

       while (left <= right) {
           val mid = (left + right) / 2
           val currentKey = suspendLineStorage.read(mid * 2)
           if (currentKey == key) {
               return mid * 2
           }
          if (currentKey < key) {
            left = mid + 1
          } else {
            right = mid - 1
          }
       }
       return null
   }

    // Returns the first line in SuspendLineStorage that contains the provided key's data.
    // Returns null if the provided id is not found.
    private suspend fun getFirstDataFromSuspendLineStorage(key: String): String?
    {
        val indexOfKey = binarySearchOnEvenLines(key)
        if (indexOfKey != null)
        {
            return suspendLineStorage.read(indexOfKey + 1)
        }
        return null
    }

    // Returns the second line in SuspendLineStorage that contains the provided key's data.
    // Returns null if the provided id is not found.
    private suspend fun getSecondDataFromSuspendLineStorage(key: String): String?
    {
        val indexOfKey = binarySearchOnEvenLines(key)
        if (indexOfKey != null)
        {
            return suspendLineStorage.read(indexOfKey + 2)
        }
        return null
    }

    // Returns a string that contains all elements from the list separated by a space.
    private fun convertDataListToString(dataList: List<String>) : String {
        var dataString = ""
        dataList.forEach { data ->
            dataString.plus("$data ")
        }
        return dataString
    }

    // Writes the element's data to SuspendLineStorage, where the key is in the first line and each list of data is in a separate line.
    private suspend fun addElementDataToSuspendLineStorage(element: KeyWithTwoDataLists) {
        suspendLineStorage.appendLine(element.key)

        var firstRow = convertDataListToString(element.dataList1)
        suspendLineStorage.appendLine(firstRow)

        var secondRow = convertDataListToString(element.dataList2)
        suspendLineStorage.appendLine(secondRow)
    }

    // Populates SuspendLineStorage with the provided data, sorted by their keys.
    suspend fun initializeDatabase(elementsList: List<KeyWithTwoDataLists>)
    {
        val processedElementsList = elementsList.sortedBy { it.key }

        processedElementsList.forEach { element ->
            addElementDataToSuspendLineStorage(element)
        }
    }

    // Returns the layout of the database.
    // For testing the initialization of the database.
    suspend fun getDatabaseAsArrayList(): ArrayList<String>
    {
        val dataList = ArrayList<String>()
        val numberOfLines = suspendLineStorage.numberOfLines()
        for (line in 0..<numberOfLines) {
            val dataInLine = suspendLineStorage.read(line)
            dataList.addLast(dataInLine)
        }
        return dataList
    }
}
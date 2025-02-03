package il.ac.technion.cs.sd.buy.lib

import com.google.inject.Inject
import il.ac.technion.cs.sd.buy.external.SuspendLineStorage
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import kotlinx.serialization.Serializable
import kotlinx.coroutines.*

@Serializable
open class KeyWithTwoDataLists(val key: String, val mainData: String, val listData: List<String>)

class StorageLibrary @Inject constructor(private val suspendLineStorageFactory: SuspendLineStorageFactory, private val fileName: String) {
    private lateinit var suspendLineStorage : SuspendLineStorage

    // Returns the index of the line in SuspendLineStorage that contains the provided key.
    // Returns null if the provided key is not found.
    private suspend fun binarySearchOnEveryThreeLines(key: String) : Int? {
        var left = 0
        var right = suspendLineStorage.numberOfLines() / 3 - 1

        while (left <= right) {
            val mid = (left + right) / 2
            val currentKey = suspendLineStorage.read(mid * 3)
            if (currentKey == key) {
                return mid * 3
            }
            else if (currentKey < key) {
                left = mid + 1
            }
            else {
                right = mid - 1
            }
        }
        return null
    }

    // Returns a string that contains all elements from the list separated by a space.
    private fun convertDataListToString(dataList: List<String?>) : String {
        val stringBuilder = StringBuilder()
        dataList.forEach { data ->
            stringBuilder.append("$data ")
        }
        stringBuilder.setLength(stringBuilder.length - 1)
        return stringBuilder.toString()
    }

    // Writes the element's data to SuspendLineStorage, where the key is in the first line and each list of data is in a separate line.
    private suspend fun addElementDataToSuspendLineStorage(element: KeyWithTwoDataLists) {
        suspendLineStorage.appendLine(element.key)

        var firstRow = element.mainData
        suspendLineStorage.appendLine(firstRow)

        var secondRow = convertDataListToString(element.listData)
        suspendLineStorage.appendLine(secondRow)
    }

    // Returns both lines of data of the provided key from SuspendLineStorage
    // Returns null if the provided key is not found
    suspend fun getDataFromSuspendLineStorage(key: String): List<String>? = coroutineScope {
        val indexOfKey = binarySearchOnEveryThreeLines(key)

        if (indexOfKey != null) {
            val firstData = async { suspendLineStorage.read(indexOfKey + 1) }
            val secondData = async { suspendLineStorage.read(indexOfKey + 2) }
            listOf(firstData.await(), secondData.await())
        }
        else {
            null
        }
    }

    // Populates SuspendLineStorage with the provided data, sorted by their keys.
    suspend fun initializeDatabase(elementsList: List<KeyWithTwoDataLists>) {
        suspendLineStorage = suspendLineStorageFactory.open(fileName)

        val sortedElementsList = elementsList.sortedBy { it.key }

        sortedElementsList.forEach { element ->
            addElementDataToSuspendLineStorage(element)
        }
    }

    // Returns the layout of the database.
    // For testing the initialization of the database.
    suspend fun getDatabaseAsList() : List<String> {
        return (0 until suspendLineStorage.numberOfLines())
            .map { index -> suspendLineStorage.read(index) }
            .toList()
    }
}
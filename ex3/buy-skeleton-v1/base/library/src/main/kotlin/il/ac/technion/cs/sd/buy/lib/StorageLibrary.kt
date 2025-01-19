package il.ac.technion.cs.sd.buy.lib

import com.google.inject.Inject
import il.ac.technion.cs.sd.buy.external.SuspendLineStorage
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.coroutines.*

@Serializable
open class KeyThreeValuesElement(val keyType: String, val key: String, val value1: String?, val value2: String?, val value3: Int?)

class StorageLibrary @Inject constructor(private val suspendLineStorageFactory: SuspendLineStorageFactory, fileName: String) {
    private lateinit var suspendLineStorage : SuspendLineStorage

    init {
        var job = CoroutineScope(Dispatchers.Default).launch {
            suspendLineStorage = suspendLineStorageFactory.open(fileName)
        }
    }

    // Returns the index of the line in LineStorage that contains the provided key.
    // Returns null if the provided id is not found.
    private suspend fun binarySearchOnEvenLines(key: String): Int?
    {
       var left = 0
       var right = runBlocking {
           launch {
               suspendLineStorage.numberOfLines() / 2 - 1
           }.join()
       }
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

    // Returns the line in LineStorage that contains the provided key's data.
    // Returns null if the provided id is not found.
    private suspend fun getMainKeyDataFromLineStorage(key: String): String?
    {
        val indexOfKey = binarySearchOnEvenLines(key)
        if (indexOfKey != null)
        {
            return suspendLineStorage.read(indexOfKey + 1)
        }
        return null
    }

    // Sorts all the sub-keys of each key.
    private fun Sequence<KeyListOfValuesElement>.sortSubKeysByTheirKeys() : Sequence<KeyListOfValuesElement> =
        this
            .map { mainKey ->
                val sorted = mainKey.listOfSubKeys
                    .sortedBy { it.subKey }
                KeyListOfValuesElement(mainKey.mainKey, sorted)
            }

    // Merges, removes duplicates and sorts the main keys.
    private fun processMainKeysList(mainKeysList: List<KeyListOfValuesElement>) : List<KeyListOfValuesElement>
    {
        return mainKeysList
            .asSequence()
            .sortedBy { it.mainKey }
            .sortSubKeysByTheirKeys()
            .toList()
    }

    // Populates LineStorage with the provided data.
    // Data is stored in the following format:
    // <mainKey>
    // <subKey1> <value1> <subKey2> <value2> ...
    suspend fun initializeDatabase(mainKeysList: List<KeyListOfValuesElement>)
    {
        val sortedMainKeysList = processMainKeysList(mainKeysList)

        for (mainKey in sortedMainKeysList)
        {
            suspendLineStorage.appendLine(mainKey.mainKey)
            suspendLineStorage.appendLine(mainKey.listOfSubKeys.joinToString(" ") { "${it.subKey} ${it.value}" })
        }
    }

    // Returns all the couples of sub-key and value associated with the provided key as a map.
    // Data is stored in the following format:
    // <mainKey1> <listOfSubKeys1> <mainKey2> <listOfSubKeys2> ...
    suspend fun getDataAsMapFromMainKey(mainKey: String): Map<String, Int>?
    {
        val data = getMainKeyDataFromLineStorage(mainKey) ?: return null
        if (data == "") return null
        return data
            .split(" ")
            .asSequence()
            .chunked(2)
            .associate { it[0] to it[1].toInt() }
            .toMap()
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
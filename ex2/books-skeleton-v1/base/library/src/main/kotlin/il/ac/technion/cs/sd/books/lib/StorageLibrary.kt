package il.ac.technion.cs.sd.books.lib

import com.google.inject.Guice
import com.google.inject.Inject
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.sd.books.external.LineStorage
import il.ac.technion.cs.sd.books.external.LineStorageFactory
import il.ac.technion.cs.sd.books.external.LineStorageModule

open class KeyValueElement(val subKey: String, val value: Int)

open class KeyListOfValuesElement(val mainKey: String, val listOfSubKeys: List<KeyValueElement>)

class StorageLibrary @Inject constructor(private val lineStorageFactory: LineStorageFactory)
{
    private var lineStorage : LineStorage? = null

    // Returns the index of the line in LineStorage that contains the provided key.
    // Returns null if the provided id is not found.
    private fun binarySearchOnEvenLines(key: String): Int?
    {
       var left = 0
       var right = lineStorage?.numberOfLines()!! / 2 - 1
       while (left <= right) {
           val mid = (left + right) / 2
           val currentKey = lineStorage?.read(mid * 2)
           if (currentKey == key) {
               return mid * 2
           }
          if (currentKey != null && currentKey < key) {
            left = mid + 1
          } else {
            right = mid - 1
          }
       }
       return null
   }

    // Returns the line in LineStorage that contains the provided key's data.
    // Returns null if the provided id is not found.
    private fun getMainKeyDataFromLineStorage(key: String): String?
    {
        val indexOfKey = binarySearchOnEvenLines(key)
        if (indexOfKey != null)
        {
            return lineStorage?.read(indexOfKey + 1)
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

    // Creates the LineStorageInstance
    fun createDatabase(fileName: String)
    {
        lineStorage = lineStorageFactory.open(fileName)
    }

    // Populates LineStorage with the provided data.
    // Data is stored in the following format:
    // <mainKey>
    // <subKey1> <value1> <subKey2> <value2> ...
    fun initializeDatabase(mainKeysList: List<KeyListOfValuesElement>)
    {
        val sortedMainKeysList = processMainKeysList(mainKeysList)

        for (mainKey in sortedMainKeysList)
        {
            lineStorage?.appendLine(mainKey.mainKey)
            lineStorage?.appendLine(mainKey.listOfSubKeys.joinToString(" ") { "${it.subKey} ${it.value}" })
        }
    }

    // Returns all the couples of sub-key and value associated with the provided key as a map.
    // Data is stored in the following format:
    // <mainKey1> <listOfSubKeys1> <mainKey2> <listOfSubKeys2> ...
    fun getDataAsMapFromMainKey(mainKey: String): Map<String, Int>?
    {
        val data = getMainKeyDataFromLineStorage(mainKey) ?: return null
        return data
            .split(" ")
            .asSequence()
            .chunked(2)
            .associate { it[0] to it[1].toInt() }
            .toMap()
    }

    // Returns the layout of the database.
    // For testing the initialization of the database.
    fun getDatabaseAsArrayList(): ArrayList<String>
    {
        val dataList = ArrayList<String>()
        val numberOfLines = lineStorage?.numberOfLines()
        for (line in 0..<numberOfLines!!) {
            val dataInLine = lineStorage?.read(line)
            if (dataInLine != null) {
                dataList.addLast(dataInLine)
            }
        }
        return dataList
    }
}
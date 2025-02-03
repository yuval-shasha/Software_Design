package il.ac.technion.cs.sd.buy.lib

import com.google.inject.Guice
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import il.ac.technion.cs.sd.buy.external.LineStorageModule
import org.junit.jupiter.api.*
import dev.misfitlabs.kotlinguice4.getInstance
import kotlinx.coroutines.test.runTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StorageLibraryTest {
    private val mainDataList = ArrayList<KeyWithTwoDataLists>()
    private var suspendLineStorageFactory = Guice.createInjector(LineStorageModule()).getInstance<SuspendLineStorageFactory>()
    private var storageLibrary = StorageLibrary(suspendLineStorageFactory, "products")

    @BeforeAll
    fun setup() {
        val dataList1 = ArrayList<String>()
        dataList1.add("second")
        dataList1.add("1234567890")
        dataList1.add("#$!")

        mainDataList.add(KeyWithTwoDataLists("101" , "first", dataList1))

        val dataList2 = ArrayList<String>()
        dataList2.add("djvkf")
        dataList2.add("294hno2")

        mainDataList.add(KeyWithTwoDataLists("f8g" , "second", dataList2))

        val dataList3 = ArrayList<String>()
        dataList3.add("dwa0e")

        mainDataList.add(KeyWithTwoDataLists("@%" , "third", dataList3))
    }

    @Test
    fun `initializeDatabase should sort the main keys in ascending order`() = runTest {
        storageLibrary.initializeDatabase(mainDataList)
        val databaseAsList = storageLibrary.getDatabaseAsList()

        Assertions.assertEquals("101", databaseAsList[0])
        Assertions.assertEquals("first", databaseAsList[1])
        Assertions.assertEquals("second 1234567890 #$!", databaseAsList[2])

        Assertions.assertEquals("@%", databaseAsList[3])
        Assertions.assertEquals("third", databaseAsList[4])
        Assertions.assertEquals("dwa0e", databaseAsList[5])

        Assertions.assertEquals("f8g", databaseAsList[6])
        Assertions.assertEquals("second", databaseAsList[7])
        Assertions.assertEquals("djvkf 294hno2", databaseAsList[8])
    }

    @Test
    fun `getDataListsFromSuspendLineStorage should return the data lists of a valid key`() = runTest {
        storageLibrary.initializeDatabase(mainDataList)
        val dataLists = storageLibrary.getDataFromSuspendLineStorage("f8g")

        Assertions.assertEquals("second", dataLists?.get(0))
        Assertions.assertEquals("djvkf 294hno2", dataLists?.get(1))
    }

    @Test
    fun `getDataListsFromSuspendLineStorage should return null for invalid key`() = runTest {
        storageLibrary.initializeDatabase(mainDataList)
        val dataLists = storageLibrary.getDataFromSuspendLineStorage("Hello")

        Assertions.assertNull(dataLists)
    }
}

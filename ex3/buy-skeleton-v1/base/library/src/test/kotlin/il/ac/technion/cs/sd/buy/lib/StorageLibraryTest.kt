package il.ac.technion.cs.sd.buy.lib

import com.google.inject.Guice
import il.ac.technion.cs.sd.buy.external.SuspendLineStorageFactory
import il.ac.technion.cs.sd.buy.external.LineStorageModule
import org.junit.jupiter.api.*
import dev.misfitlabs.kotlinguice4.getInstance
import kotlinx.coroutines.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StorageLibraryTest {
    private val mainDataList = ArrayList<KeyWithTwoDataLists>()
    private var suspendLineStorageFactory = Guice.createInjector(LineStorageModule()).getInstance<SuspendLineStorageFactory>()
    private var storageLibrary = StorageLibrary(suspendLineStorageFactory, "products")

    @BeforeAll
    fun setup() {
        val dataList11 = ArrayList<String>()
        dataList11.add("first")
        dataList11.add("woohoo!")
        dataList11.add("123")

        val dataList12 = ArrayList<String>()
        dataList12.add("second")
        dataList12.add("1234567890")
        dataList12.add("#$!")

        mainDataList.add(KeyWithTwoDataLists("101" , dataList11, dataList12))

        val dataList21 = ArrayList<String>()
        dataList21.add("djvkf")
        dataList21.add("294hno2")

        val dataList22 = ArrayList<String>()
        dataList22.add("wepm")
        dataList22.add("5ghown")
        dataList22.add("%&$*#(")

        mainDataList.add(KeyWithTwoDataLists("f8g" , dataList21, dataList22))

        val dataList31 = ArrayList<String>()
        dataList31.add("dwa0e")

        val dataList32 = ArrayList<String>()
        dataList32.add("09bio")
        dataList32.add("3nrqa")
        dataList32.add(")()()()")

        mainDataList.add(KeyWithTwoDataLists("@%" , dataList31, dataList32))

        storageLibrary.initializeDatabase(mainDataList)
    }

    @Test
    fun `initializeDatabase should sort the main keys in ascending order`() = runBlocking {
        val databaseAsArrayList = async { storageLibrary.getDatabaseAsArrayList() }.await()

        Assertions.assertEquals("101", databaseAsArrayList[0])
        Assertions.assertEquals("first woohoo! 123", databaseAsArrayList[1])
        Assertions.assertEquals("second 1234567890 #$!", databaseAsArrayList[2])

        Assertions.assertEquals("@%", databaseAsArrayList[3])
        Assertions.assertEquals("dwa0e", databaseAsArrayList[4])
        Assertions.assertEquals("09bio 3nrqa )()()()", databaseAsArrayList[5])

        Assertions.assertEquals("f8g", databaseAsArrayList[6])
        Assertions.assertEquals("djvkf 294hno2", databaseAsArrayList[7])
        Assertions.assertEquals("wepm 5ghown %&$*#(", databaseAsArrayList[8])
    }

    @Test
    fun `getDataListsFromSuspendLineStorage should return the data lists of a valid key`() = runBlocking {
        val dataLists = storageLibrary.getDataListsFromSuspendLineStorage("f8g")

        Assertions.assertEquals("djvkf 294hno2", dataLists?.get(0))
        Assertions.assertEquals("wepm 5ghown %&$*#(", dataLists?.get(1))
    }

    @Test
    fun `getDataListsFromSuspendLineStorage should return null for invalid key`() = runBlocking {
        val dataLists = storageLibrary.getDataListsFromSuspendLineStorage("Hello")

        Assertions.assertNull(dataLists)
    }
}

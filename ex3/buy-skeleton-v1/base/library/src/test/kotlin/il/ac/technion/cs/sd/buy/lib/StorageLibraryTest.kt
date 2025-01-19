/*
package il.ac.technion.cs.sd.buy.lib

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.sd.books.external.LineStorageFactory
import il.ac.technion.cs.sd.buy.external.LineStorageModule
import org.junit.jupiter.api.*
import io.mockk.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StorageLibraryTest {
    private val mainKeysList = ArrayList<KeyListOfValuesElement>()
    private lateinit var storageLibrary: StorageLibrary
    private lateinit var lineStorageFactory: LineStorageFactory

    @BeforeAll
    fun setup(): Unit {
        lineStorageFactory = Guice.createInjector(LineStorageModule()).getInstance<LineStorageFactory>()
        storageLibrary = StorageLibrary(lineStorageFactory, "reviewers")

        val subKeysList1 = ArrayList<KeyValueElement>()
        subKeysList1.add(KeyValueElement("sub1", 31))
        mainKeysList.add(KeyListOfValuesElement("123", subKeysList1))

        val subKeysList2 = ArrayList<KeyValueElement>()
        subKeysList2.add(KeyValueElement("sub2" , 50))
        subKeysList2.add(KeyValueElement("sub3" , 25))
        mainKeysList.add(KeyListOfValuesElement("abc3" , subKeysList2))

        val subKeysList3 = ArrayList<KeyValueElement>()
        subKeysList3.add(KeyValueElement("sub1" , 15))
        subKeysList3.add(KeyValueElement("sub2" , 30))
        mainKeysList.add(KeyListOfValuesElement("101" , subKeysList3))

        storageLibrary.initializeDatabase(mainKeysList)
    }

    @Test
    fun `initializeDatabase should sort the main keys in ascending order`() {
        val databaseAsArrayList = storageLibrary.getDatabaseAsArrayList()

        Assertions.assertEquals("101", databaseAsArrayList[0])
        Assertions.assertEquals("sub1 15 sub2 30", databaseAsArrayList[1])
        Assertions.assertEquals("123", databaseAsArrayList[2])
        Assertions.assertEquals("sub1 31", databaseAsArrayList[3])
        Assertions.assertEquals("abc3", databaseAsArrayList[4])
        Assertions.assertEquals("sub2 50 sub3 25", databaseAsArrayList[5])
    }

    // test that getDataAsMapFromMainKey works as expected with valid mainKey
    @Test
    fun `getDataAsMapFromMainKey should return all the sub keys and their values by the main key` () {
        val subKeyToValueMap1 = storageLibrary.getDataAsMapFromMainKey("123")
        val subKeyToValueMap2 = storageLibrary.getDataAsMapFromMainKey("abc3")

        Assertions.assertNotNull(subKeyToValueMap1)
        Assertions.assertEquals(31 , subKeyToValueMap1?.get("sub1"))
        Assertions.assertNotNull(subKeyToValueMap2)
        Assertions.assertEquals(25 , subKeyToValueMap2?.get("sub3"))
        Assertions.assertEquals(50 , subKeyToValueMap2?.get("sub2"))
    }

    @Test
    fun `getDataAsMapFromMainKey should return null if main key does not exist`() {
        val nullData1 = storageLibrary.getDataAsMapFromMainKey("1234")
        val nullData2 = storageLibrary.getDataAsMapFromMainKey("ab")

        Assertions.assertNull(nullData1)
        Assertions.assertNull(nullData2)
    }

    @Test
    fun `verify that the open method is called only once`() {
        storageLibrary.initializeDatabase(mainKeysList)

        verify (exactly = 1) {
            lineStorageFactory.open("reviewers")
        }
    }
}
 */
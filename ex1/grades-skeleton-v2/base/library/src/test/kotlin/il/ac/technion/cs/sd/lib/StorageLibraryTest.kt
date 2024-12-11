package il.ac.technion.cs.sd.lib

import il.ac.technion.cs.sd.grades.external.LineStorage
import org.junit.jupiter.api.*
import java.io.Console

/** Use this class to test your library implementation */
class StorageLibraryTest {
    @Test
    fun `initializeDatabase should sort the input in ascending order according to id`()
    {
        val csvData = """2,100
            4,90
            3,50"""
        StorageLibrary.initializeDatabase(csvData)

        val databaseAsArrayList = StorageLibrary.getDatabaseAsArrayList()

        Assertions.assertEquals("2,100", databaseAsArrayList[0])
        Assertions.assertEquals("3,50", databaseAsArrayList[1])
        Assertions.assertEquals("4,90", databaseAsArrayList[2])
    }

    @Test
    fun `initializeDatabase should keep only the last data that was associated with each id`()
    {
        val csvData = """2,100
            4,90
            2,50"""
        StorageLibrary.initializeDatabase(csvData)

        val databaseAsArrayList = StorageLibrary.getDatabaseAsArrayList()

        Assertions.assertEquals("2,50", databaseAsArrayList[0])
        Assertions.assertEquals("4,90", databaseAsArrayList[1])
    }

    @Test
    fun `getDataFromId should return null if the provided id is not in the database`()
    {
        val csvData = """2,100
            4,90
            3,50"""
        StorageLibrary.initializeDatabase(csvData)

        Assertions.assertNull(StorageLibrary.getDataFromId("1"))
    }

    @Test
    fun `getDataFromId should return the data that was associated with the provided id`()
    {
        val csvData = """2,100
            4,90
            3,50"""
        StorageLibrary.initializeDatabase(csvData)

        Assertions.assertEquals("90", StorageLibrary.getDataFromId("4"))
    }

    @Test
    fun `getDataFromId should return the last data that was associated with the provided id`()
    {
        val csvData = """2,100
            4,90
            2,50"""
        StorageLibrary.initializeDatabase(csvData)

        Assertions.assertEquals("50", StorageLibrary.getDataFromId("2"))
    }

    @AfterEach
    fun tearDown()
    {
        StorageLibrary.clearLineStorage()
    }
}
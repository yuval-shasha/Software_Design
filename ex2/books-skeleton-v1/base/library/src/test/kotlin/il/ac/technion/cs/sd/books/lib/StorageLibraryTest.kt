package il.ac.technion.cs.sd.books.lib

import org.junit.jupiter.api.Test

class StorageLibraryTest
{
    // test that initializeDatabase works as expected - all keys should be sorted
    @Test
    fun `initializeDatabase should sort the main keys in ascending order`()
    {
        val reviewersList =
        StorageLibrary.initializeDatabase(csvData)

        val databaseAsArrayList = StorageLibrary.getDatabaseAsArrayList()

        Assertions.assertEquals("2,100", databaseAsArrayList[0])
        Assertions.assertEquals("3,50", databaseAsArrayList[1])
        Assertions.assertEquals("4,90", databaseAsArrayList[2])
    }

    // test that getDataAsMapFromMainKey works as expected with valid mainKey

    // test that getDataAsMapFromMainKey works as expected with invalid mainKey (should return null)

    // Check that the open method in LineStorageFactory is called only once per file when testing each method
}
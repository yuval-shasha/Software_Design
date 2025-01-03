package il.ac.technion.cs.sd.books.external

class LineStorageFactoryMock : LineStorageFactory
{
    override fun open(fileName: String): LineStorage
    {
        return LineStorageMock()
    }
}
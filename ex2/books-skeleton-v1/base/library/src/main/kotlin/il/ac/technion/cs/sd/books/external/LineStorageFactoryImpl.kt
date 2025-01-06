package il.ac.technion.cs.sd.books.external

class LineStorageFactoryImpl : LineStorageFactory
{
    override fun open(fileName: String): LineStorage
    {
        return LineStorageImpl()
    }
}
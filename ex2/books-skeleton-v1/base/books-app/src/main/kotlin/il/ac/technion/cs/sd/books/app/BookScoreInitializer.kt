package il.ac.technion.cs.sd.books.app

interface BookScoreInitializer {
    /** Saves the XML data persistently, so that it could be queried using BookScoreReader */
    fun setup(xmlData: String)
}
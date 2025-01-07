package il.ac.technion.cs.sd.books.external

import com.google.inject.Singleton

@Singleton
class LineStorageFactoryImpl : LineStorageFactory {
    var reviewers = LineStorageImpl()
    var books = LineStorageImpl()

    override fun open(fileName: String): LineStorage {
        if (fileName == "reviewers") {
            return reviewers
        }
        else {
            return books
        }
    }
}
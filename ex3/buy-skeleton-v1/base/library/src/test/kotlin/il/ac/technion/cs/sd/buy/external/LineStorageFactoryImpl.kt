package il.ac.technion.cs.sd.buy.external

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
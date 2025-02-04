package il.ac.technion.cs.sd.buy.external

import kotlinx.coroutines.delay
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.system.measureTimeMillis

class SuspendLineStorageImpl : SuspendLineStorage {
    private val concurrentStoredLinesList = CopyOnWriteArrayList<String>()

    override suspend fun appendLine(line: String) {
        concurrentStoredLinesList.addLast(line)
    }

    override suspend fun read(lineNumber: Int): String {
        var lineAtLineNumber = ""
        val time = measureTimeMillis {
            lineAtLineNumber = concurrentStoredLinesList[lineNumber]
        }
        delay (lineAtLineNumber.length - time)
        return lineAtLineNumber
    }

    override suspend fun numberOfLines(): Int {
        var numberOfLines = 0
        val time = measureTimeMillis {
            numberOfLines = concurrentStoredLinesList.size
        }
        delay (100 - time)
        return numberOfLines
    }
}
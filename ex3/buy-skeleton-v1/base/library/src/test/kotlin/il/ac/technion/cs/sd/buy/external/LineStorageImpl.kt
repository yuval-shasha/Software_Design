package il.ac.technion.cs.sd.buy.external

import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

class SuspendLineStorageImpl : SuspendLineStorage
{
    private var storedLines = ArrayList<String>()

    override suspend fun appendLine(line: String): Unit
    {
        storedLines.addLast(line)
    }

    override suspend fun read(lineNumber: Int): String
    {
        var lineAtLineNumber = ""
        val time = measureTimeMillis {
            lineAtLineNumber = storedLines[lineNumber]
        }
        delay (lineAtLineNumber.length - time)
        return lineAtLineNumber
    }

    override suspend fun numberOfLines(): Int
    {
        var numberOfLines = 0
        val time = measureTimeMillis {
            numberOfLines = storedLines.size
        }
        delay (100 - time)
        return numberOfLines
    }
}
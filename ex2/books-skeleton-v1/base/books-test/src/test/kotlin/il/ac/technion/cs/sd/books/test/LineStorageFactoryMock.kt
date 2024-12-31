package il.ac.technion.cs.sd.books.test

import il.ac.technion.cs.sd.books.external.LineStorageFactory
import il.ac.technion.cs.sd.books.external.LineStorage
import java.lang.Thread.sleep
import kotlin.system.measureTimeMillis

class LineStorageFactoryMock : LineStorageFactory
{
    override fun open(fileName: String): LineStorage
    {
        return LineStorageMock()
    }

    private class LineStorageMock : LineStorage
    {
        var storedLines = ArrayList<String>()

        override fun appendLine(line: String): Unit
        {
            storedLines.addLast(line)
        }

        override fun read(lineNumber: Int): String
        {
            var lineAtLineNumber = ""
            val time = measureTimeMillis {
                lineAtLineNumber = storedLines[lineNumber]
            }
            sleep (lineAtLineNumber.length - time)
            return lineAtLineNumber
        }

        override fun numberOfLines(): Int
        {
            var numberOfLines = 0
            val time = measureTimeMillis {
                numberOfLines = storedLines.size
            }
            sleep (100 - time)
            return numberOfLines
        }
    }
}
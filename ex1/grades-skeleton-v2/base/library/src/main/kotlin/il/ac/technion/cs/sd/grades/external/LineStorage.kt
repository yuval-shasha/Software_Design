package il.ac.technion.cs.sd.grades.external

import java.lang.Thread.sleep
import kotlin.system.measureTimeMillis

/**
 * This package and class override the external library
 * which was automatically imported to the project (you can view it under
 * the 'External Libraries' directory). This is NOT good practice but
 * was required for this assignment. In the following assignments
 * we will be using a different mechanism to achieve this behavior.
 *
 * You should implement this class for testing your library implementation.
 * Make sure it enforces the timing restrictions specified in the assignment PDF.
 * Note that your implementation will be overridden by staff code.
 * IMPORTANT: 1) DO NOT in any way alter the API of this class.
 *            2) DO NOT create any files under the package defined at the top of this file.
 */
class LineStorage {
    companion object {
        var storedLines = ArrayList<String>()

        /** Appends a line to the END of the file */
        fun appendLine(line: String) {
            storedLines.addLast(line)
        }

        /** Returns the line at index lineNumber (0-indexed) */
        fun read(lineNumber: Int): String {
            var lineAtLineNumber = ""
            val time = measureTimeMillis {
                lineAtLineNumber = storedLines[lineNumber]
            }
            sleep (lineAtLineNumber.length - time)
            return lineAtLineNumber
        }

        /** Returns the total number of lines in the file */
        fun numberOfLines(): Int {
            var numberOfLines = 0
            val time = measureTimeMillis {
                numberOfLines = storedLines.size
            }
            sleep (100 - time)
            return numberOfLines
        }
    }
}
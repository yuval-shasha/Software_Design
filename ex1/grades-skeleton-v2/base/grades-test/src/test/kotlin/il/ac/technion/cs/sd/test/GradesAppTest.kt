package il.ac.technion.cs.sd.test

/** JUnit5 imports. Add more if needed */
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

/**
 * Example imports for more declarative testing.
 * Recommended, but not mandatory.
 * Use the web to figure out what to import for your specific tests.
 */
import com.natpryce.hamkrest.assertion.assertThat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

import java.io.FileNotFoundException

import il.ac.technion.cs.sd.app.GradesReader
import il.ac.technion.cs.sd.app.GradesInitializer
import il.ac.technion.cs.sd.grades.external.LineStorage
import il.ac.technion.cs.sd.lib.StorageLibrary
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertTimeout
import java.time.Duration

class GradesAppTest {
    private fun getGradesReader(fileName: String): GradesReader {
        val fileContents: String =
            javaClass.getResource(fileName)?.readText() ?:
            throw FileNotFoundException("Could not open file $fileName")

        val gradesInitializer = GradesInitializer()
        gradesInitializer.setup(fileContents)

        return GradesReader()
    }

    @Test
    fun `noDuplicatesShort file should return the only grade associated with student id`() {
        assertTimeout(Duration.ofSeconds(10)) {
            val gradesReader = getGradesReader("noDuplicatesShort")
            Assertions.assertEquals(59, gradesReader.getGrade("83969992"))
        }
    }

    @Test
    fun `noDuplicatesShort file should return null for an unknown student id`() {
        assertTimeout(Duration.ofSeconds(10)) {
            val gradesReader = getGradesReader("noDuplicatesShort")
            Assertions.assertEquals(null, gradesReader.getGrade("12345"))
        }
    }

    /*
    // TODO: create a file with 1 million distinct ids and complete parameters in the test
    @Test
    fun `noDuplicatesLong file should return the only grade associated with student id`() {
        assertTimeout(Duration.ofSeconds(10)) {
            val gradesReader = getGradesReader("noDuplicatesLong")
            Assertions.assertEquals(null, gradesReader.getGrade("null"))
        }
    }
     */

    @Test
    fun `withDuplicatesShort file should return the last grade associated with student id`() {
        assertTimeout(Duration.ofSeconds(10)) {
            val gradesReader = getGradesReader("withDuplicatesShort")
            Assertions.assertEquals(386, gradesReader.getGrade("264878408"))
        }
    }

    @Test
    fun `withDuplicatesLong file should return the last grade associated with student id where student is last`() {
        assertTimeout(Duration.ofSeconds(10)) {
            val gradesReader = getGradesReader("withDuplicatesLong")
            Assertions.assertEquals(1, gradesReader.getGrade("933670118"))
        }
    }

    @Test
    fun `withDuplicatesLong file should return the last grade associated with student id where student is in the middle`() {
        assertTimeout(Duration.ofSeconds(10)) {
            val gradesReader = getGradesReader("withDuplicatesLong")
            Assertions.assertEquals(348, gradesReader.getGrade("50204972"))
        }
    }

    @AfterEach
    fun tearDown()
    {
        StorageLibrary.clearLineStorage()
    }
}
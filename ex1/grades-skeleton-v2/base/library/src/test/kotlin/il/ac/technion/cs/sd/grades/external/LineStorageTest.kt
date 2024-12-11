package il.ac.technion.cs.sd.grades.external

/** JUnit5 imports. Add more if needed */
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertTimeout
import java.time.Duration


class LineStorageTest {
    @Test
    fun `appendLine should return instantly`()
    {
        assertTimeout(Duration.ofMillis(1)) { LineStorage.appendLine("test") }
    }

    @Test
    fun `reading a line of 5 characters should take 5 milliseconds`()
    {
        LineStorage.appendLine("short")

        assertTimeout(Duration.ofMillis(5)) { LineStorage.read(0) }
    }

    @Test
    fun `reading a line of 13 characters should take 13 milliseconds`()
    {
        LineStorage.appendLine("13 characters")

        assertTimeout(Duration.ofMillis(13)) { LineStorage.read(0) }
    }

    @Test
    fun `numberOfLines should take 100 milliseconds for one line`()
    {
        LineStorage.appendLine("test")

        assertTimeout(Duration.ofMillis(100)) { LineStorage.numberOfLines() }
    }

    @Test
    fun `numberOfLines should take 100 milliseconds for 5 lines`()
    {
        for (i in 1..5)
        {
            LineStorage.appendLine("test $i")
        }

        assertTimeout(Duration.ofMillis(100)) { LineStorage.numberOfLines() }
    }

    @AfterEach
    fun tearDown()
    {
        LineStorage.storedLines.clear()
    }
}
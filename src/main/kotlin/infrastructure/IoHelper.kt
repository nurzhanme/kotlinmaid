package infrastructure

import java.io.File

/**
 *
 * @author Nurzhan Aitbayev github.com/nurzhanme
 */
class IoHelper {
    fun readFile(path: String): String {
        return File(path).readText()
    }

    fun createFile(path: String, lines: List<String>) {
        File(path).bufferedWriter().use { writer ->
            lines.forEach { line ->
                writer.write(line)
                writer.newLine()
            }
        }
    }
}
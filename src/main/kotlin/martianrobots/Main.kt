package martianrobots

import martianrobots.engine.Engine
import martianrobots.exception.InputFileNotFoundException
import martianrobots.exception.MartianRobotsException
import martianrobots.output.OutputFormatter
import martianrobots.parser.InputParser
import java.io.File
import java.io.InputStream
import java.io.PrintStream
import kotlin.system.exitProcess

/** Reads the input from the file named by the first argument, or from stdin when there is none. */
fun main(args: Array<String>) {
    val exitCode = run(args.toList(), System.`in`, System.out, System.err)
    if (exitCode != 0) exitProcess(exitCode)
}

internal fun run(
    args: List<String>,
    stdin: InputStream,
    out: PrintStream,
    err: PrintStream,
): Int =
    runCatching {
        val input = args.firstOrNull()?.let(::openFile) ?: stdin
        val lines = input.bufferedReader().readLines()
        Engine.run(InputParser.parse(lines)).map(OutputFormatter::format).forEach(out::println)
        0
    }.getOrElse { e ->
        when (e) {
            is MartianRobotsException -> {
                err.println("Error: ${e.message}")
                1
            }
            else -> throw e
        }
    }

private fun openFile(path: String): InputStream {
    val file = File(path)
    if (!file.isFile) throw InputFileNotFoundException(file.absolutePath)
    return file.inputStream()
}

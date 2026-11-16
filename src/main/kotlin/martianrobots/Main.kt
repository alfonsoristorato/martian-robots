package martianrobots

import martianrobots.engine.Engine
import martianrobots.exception.MartianRobotsException
import martianrobots.output.OutputFormatter
import martianrobots.parser.InputParser
import java.io.InputStream
import java.io.PrintStream
import kotlin.system.exitProcess

fun main() {
    val exitCode = run(System.`in`, System.out, System.err)
    if (exitCode != 0) exitProcess(exitCode)
}

internal fun run(
    input: InputStream,
    out: PrintStream,
    err: PrintStream,
): Int =
    runCatching {
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

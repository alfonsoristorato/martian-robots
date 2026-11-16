package martianrobots.parser

import martianrobots.command.Command
import martianrobots.exception.InvalidInputException
import martianrobots.model.Grid
import martianrobots.model.Mission
import martianrobots.model.Orientation
import martianrobots.model.Position
import martianrobots.model.Robot
import martianrobots.model.RobotInstructions

/**
 * Parses the raw input lines. Blank lines are skipped where a grid or robot position is expected, so
 * leading, trailing and in-between blank lines are fine. The line right after a robot position is always
 * its instructions, so a blank one means the robot has no moves.
 * Any invalid line throws an [InvalidInputException] naming the line number.
 */
object InputParser {
    const val MAX_COORDINATE = 50
    const val MAX_INSTRUCTION_LENGTH = 99

    private val whitespace = Regex("\\s+")

    private data class Line(
        val number: Int,
        val text: String,
    )

    fun parse(lines: List<String>): Mission {
        val remaining = lines.mapIndexed { index, text -> Line(index + 1, text.trim()) }.iterator()

        fun nextNonBlank(): Line? = remaining.asSequence().firstOrNull { it.text.isNotEmpty() }

        val gridLine =
            nextNonBlank()
                ?: throw InvalidInputException(
                    null,
                    "Input is empty, expected the grid's upper-right coordinates on the first line",
                )
        val grid = parseGrid(gridLine)
        val robots =
            generateSequence(::nextNonBlank)
                .map { positionLine ->
                    if (!remaining.hasNext()) positionLine.fail("robot position has no instruction line after it")
                    RobotInstructions(parseRobot(positionLine, grid), parseCommands(remaining.next()))
                }.toList()
        return Mission(grid, robots)
    }

    private fun parseGrid(line: Line): Grid {
        val tokens = line.text.split(whitespace)
        if (tokens.size != 2) line.fail("expected grid size as 'x y', got '${line.text}'")
        val (maxX, maxY) = tokens.map { parseCoordinate(it, line) }
        return Grid(maxX, maxY)
    }

    private fun parseRobot(
        line: Line,
        grid: Grid,
    ): Robot {
        val tokens = line.text.split(whitespace)
        if (tokens.size != 3) line.fail("expected robot position as 'x y orientation', got '${line.text}'")
        val position = Position(parseCoordinate(tokens[0], line), parseCoordinate(tokens[1], line))
        if (position !in grid) {
            line.fail("robot starts at ${position.x} ${position.y}, outside the grid 0 0 to ${grid.maxX} ${grid.maxY}")
        }
        val orientation =
            Orientation.fromCode(tokens[2])
                ?: line.fail("unknown orientation '${tokens[2]}', expected one of ${Orientation.entries.map { it.code }}")
        return Robot(position, orientation)
    }

    private fun parseCommands(line: Line): List<Command> {
        if (line.text.length > MAX_INSTRUCTION_LENGTH) {
            line.fail("instructions are ${line.text.length} characters, the maximum is $MAX_INSTRUCTION_LENGTH")
        }
        return line.text.mapIndexed { index, code ->
            Command.fromCode(code)
                ?: line.fail("unknown instruction '$code' at position ${index + 1}, expected one of ${Command.all.map { it.code }}")
        }
    }

    private fun parseCoordinate(
        token: String,
        line: Line,
    ): Int =
        token.toIntOrNull()?.takeIf { it in 0..MAX_COORDINATE }
            ?: line.fail("'$token' is not a coordinate between 0 and $MAX_COORDINATE")

    private fun Line.fail(reason: String): Nothing = throw InvalidInputException(number, reason)
}

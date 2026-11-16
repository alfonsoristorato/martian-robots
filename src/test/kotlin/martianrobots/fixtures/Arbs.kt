package martianrobots.fixtures

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import martianrobots.command.Command
import martianrobots.model.Grid
import martianrobots.model.Orientation
import martianrobots.model.Position
import martianrobots.model.Robot
import martianrobots.parser.InputParser

/** Generators for property tests, all within the limits the parser accepts. */
object Arbs {
    val grid: Arb<Grid> =
        Arb.bind(Arb.int(0..InputParser.MAX_COORDINATE), Arb.int(0..InputParser.MAX_COORDINATE)) { maxX, maxY ->
            Grid(maxX, maxY)
        }

    val commands: Arb<List<Command>> = Arb.list(Arb.element(Command.all), 0..InputParser.MAX_INSTRUCTION_LENGTH)

    fun robotOn(grid: Grid): Arb<Robot> =
        Arb.bind(Arb.int(0..grid.maxX), Arb.int(0..grid.maxY), Arb.enum<Orientation>()) { x, y, orientation ->
            Robot(Position(x, y), orientation)
        }

    /** A random grid with a robot starting somewhere on it. */
    val gridWithRobot: Arb<Pair<Grid, Robot>> = grid.flatMap { grid -> robotOn(grid).map { grid to it } }
}

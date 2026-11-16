package martianrobots.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import martianrobots.command.Forward
import martianrobots.command.Left
import martianrobots.command.Right
import martianrobots.exception.InvalidInputException
import martianrobots.fixtures.SampleInput
import martianrobots.model.Grid
import martianrobots.model.Orientation
import martianrobots.model.Position
import martianrobots.model.Robot

private data class InvalidCase(
    val name: String,
    val lines: List<String>,
    val error: String,
)

private fun robotLines(vararg lines: String) = listOf("5 3") + lines

private fun shouldReject(case: InvalidCase) {
    val e = shouldThrow<InvalidInputException> { InputParser.parse(case.lines) }
    e.message shouldContain case.error
}

class InputParserSpec :
    FreeSpec({
        "valid input" - {
            "parses the sample input" {
                InputParser.parse(SampleInput.lines) shouldBe SampleInput.mission
            }

            "parses a grid with no robots" {
                val parsed = InputParser.parse(listOf("5 3"))

                parsed.grid shouldBe Grid(5, 3)
                parsed.robots.shouldBeEmpty()
            }

            "ignores blank lines before the grid, between robots and at the end" {
                val spaced =
                    """

                    5 3

                    1 1 E
                    RFRFRFRF

                    3 2 N
                    FRRFLLFFRRFLL

                    0 3 W
                    LLFFFLFLFL

                    """.trimIndent().lines()

                InputParser.parse(spaced) shouldBe SampleInput.mission
            }

            "treats a blank instruction line as a robot with no moves" {
                val parsed = InputParser.parse(robotLines("1 1 N", "  ", "2 2 E", "F"))

                parsed.robots.map { it.commands } shouldBe listOf(emptyList(), listOf(Forward))
            }

            "accepts any whitespace around and between tokens" {
                val robot = InputParser.parse(listOf("  5 \t 3 ", " 1   1\tE ", "  LRF  ")).robots.single()

                robot.robot shouldBe Robot(Position(1, 1), Orientation.EAST)
                robot.commands shouldBe listOf(Left, Right, Forward)
            }

            withTests(
                nameFn = { "accepts the grid ${it.maxX} ${it.maxY}" },
                Grid(0, 0),
                Grid(50, 50),
            ) { grid ->
                InputParser.parse(listOf("${grid.maxX} ${grid.maxY}")).grid shouldBe grid
            }

            withTests(
                nameFn = { "accepts a robot starting on the corner ${it.x} ${it.y}" },
                Position(0, 0),
                Position(5, 3),
            ) { position ->
                val robot = InputParser.parse(robotLines("${position.x} ${position.y} N", "F")).robots.single()

                robot.robot.position shouldBe position
            }

            withTests(
                nameFn = { "accepts orientation ${it.code}" },
                Orientation.entries,
            ) { orientation ->
                val robot = InputParser.parse(robotLines("0 0 ${orientation.code}", "F")).robots.single()

                robot.robot.orientation shouldBe orientation
            }

            "accepts instructions at the maximum length" {
                val longest = "F".repeat(InputParser.MAX_INSTRUCTION_LENGTH)

                InputParser
                    .parse(robotLines("0 0 N", longest))
                    .robots
                    .single()
                    .commands shouldHaveSize
                    InputParser.MAX_INSTRUCTION_LENGTH
            }
        }

        "invalid input" - {
            withTests(
                nameFn = { "rejects empty input: ${it.name}" },
                InvalidCase("no lines", emptyList(), "Input is empty"),
                InvalidCase("only blank lines", listOf("", "  "), "Input is empty"),
            ) { shouldReject(it) }

            withTests(
                nameFn = { "rejects an invalid grid line: ${it.name}" },
                InvalidCase("one coordinate", listOf("5"), "Line 1: expected grid size"),
                InvalidCase("three coordinates", listOf("5 3 1"), "Line 1: expected grid size"),
                InvalidCase("not a number", listOf("5 x"), "Line 1: 'x' is not a coordinate"),
                InvalidCase("negative", listOf("-1 3"), "Line 1: '-1' is not a coordinate"),
                InvalidCase("above the maximum", listOf("51 3"), "Line 1: '51' is not a coordinate"),
            ) { shouldReject(it) }

            withTests(
                nameFn = { "rejects an invalid robot position: ${it.name}" },
                InvalidCase("missing orientation", robotLines("1 1", "F"), "Line 2: expected robot position"),
                InvalidCase("extra token", robotLines("1 1 N N", "F"), "Line 2: expected robot position"),
                InvalidCase("not a number", robotLines("a 1 N", "F"), "Line 2: 'a' is not a coordinate"),
                InvalidCase("negative", robotLines("1 -1 N", "F"), "Line 2: '-1' is not a coordinate"),
                InvalidCase("outside the grid", robotLines("6 1 N", "F"), "Line 2: robot starts at 6 1, outside the grid"),
                InvalidCase("unknown orientation", robotLines("1 1 X", "F"), "Line 2: unknown orientation 'X'"),
                InvalidCase("full orientation name", robotLines("1 1 NORTH", "F"), "Line 2: unknown orientation 'NORTH'"),
                InvalidCase("lowercase orientation", robotLines("1 1 n", "F"), "Line 2: unknown orientation 'n'"),
            ) { shouldReject(it) }

            withTests(
                nameFn = { "rejects invalid instructions: ${it.name}" },
                InvalidCase("unknown command", robotLines("1 1 N", "FRX"), "Line 3: unknown instruction 'X' at position 3"),
                InvalidCase("lowercase command", robotLines("1 1 N", "f"), "Line 3: unknown instruction 'f' at position 1"),
                InvalidCase("space between commands", robotLines("1 1 N", "F R"), "Line 3: unknown instruction ' '"),
                InvalidCase(
                    "over the maximum length",
                    robotLines("1 1 N", "F".repeat(InputParser.MAX_INSTRUCTION_LENGTH + 1)),
                    "Line 3: instructions are 100 characters, the maximum is 99",
                ),
            ) { shouldReject(it) }

            "rejects a robot position with no instruction line after it" {
                shouldReject(InvalidCase("", robotLines("1 1 N", "F", "2 2 E"), "Line 4: robot position has no instruction line after it"))
            }

            "reports the original line number when blank lines are skipped" {
                shouldReject(InvalidCase("", listOf("5 3", "", "", "1 1 X", "F"), "Line 4: unknown orientation 'X'"))
            }
        }
    })

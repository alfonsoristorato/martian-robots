package martianrobots.command

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import martianrobots.fixtures.Arbs
import martianrobots.model.Grid
import martianrobots.model.Orientation.EAST
import martianrobots.model.Orientation.NORTH
import martianrobots.model.Orientation.SOUTH
import martianrobots.model.Orientation.WEST
import martianrobots.model.Position
import martianrobots.model.Robot

private val grid = Grid(5, 3)

private data class FallCase(
    val edge: String,
    val robot: Robot,
)

class CommandSpec :
    FreeSpec({
        "registry" - {
            "lists every command implementation" {
                Command::class.sealedSubclasses.map { it.objectInstance } shouldContainExactlyInAnyOrder Command.all
            }

            "every command has a unique code" {
                Command.all.map { it.code }.shouldBeUnique()
            }

            withTests(
                nameFn = { "fromCode finds $it by '${it.code}'" },
                Command.all,
            ) { command ->
                Command.fromCode(command.code) shouldBe command
            }

            "fromCode returns null for an unknown code" {
                Command.fromCode('X') shouldBe null
            }
        }

        "properties" - {
            "turning never moves the robot or changes the grid" {
                checkAll(Arbs.gridWithRobot) { (grid, robot) ->
                    listOf(Left, Right).forEach { turn ->
                        val result = turn.execute(robot, grid)

                        result.robot.position shouldBe robot.position
                        result.grid shouldBe grid
                    }
                }
            }
        }

        "Left" - {
            "turns left on the same point and leaves the grid alone" {
                Left.execute(Robot(Position(2, 2), NORTH), grid) shouldBe CommandResult(Robot(Position(2, 2), WEST), grid)
            }
        }

        "Right" - {
            "turns right on the same point and leaves the grid alone" {
                Right.execute(Robot(Position(2, 2), NORTH), grid) shouldBe CommandResult(Robot(Position(2, 2), EAST), grid)
            }
        }

        "Forward" - {
            "moves one point in the direction faced" {
                Forward.execute(Robot(Position(2, 2), NORTH), grid) shouldBe CommandResult(Robot(Position(2, 3), NORTH), grid)
            }

            "moves onto an edge point without being lost" {
                Forward.execute(Robot(Position(4, 3), EAST), grid) shouldBe CommandResult(Robot(Position(5, 3), EAST), grid)
            }

            withTests(
                nameFn = { "falling off the ${it.edge} edge loses the robot and leaves a scent where it was" },
                FallCase("north", Robot(Position(2, 3), NORTH)),
                FallCase("east", Robot(Position(5, 1), EAST)),
                FallCase("south", Robot(Position(2, 0), SOUTH)),
                FallCase("west", Robot(Position(0, 1), WEST)),
            ) { (_, robot) ->
                Forward.execute(robot, grid) shouldBe CommandResult(robot.copy(lost = true), grid.withScent(robot.position))
            }

            "a move off the grid from a scented point is ignored" {
                val scented = grid.withScent(Position(2, 3))
                val robot = Robot(Position(2, 3), NORTH)

                Forward.execute(robot, scented) shouldBe CommandResult(robot, scented)
            }

            "a scent only blocks falling, a move onto the grid from that point still happens" {
                val scented = grid.withScent(Position(2, 3))

                Forward.execute(Robot(Position(2, 3), SOUTH), scented) shouldBe
                    CommandResult(Robot(Position(2, 2), SOUTH), scented)
            }

            "a scent blocks falling off any edge from that point" {
                val scented = grid.withScent(Position(5, 3))
                val robot = Robot(Position(5, 3), EAST)

                Forward.execute(robot, scented) shouldBe CommandResult(robot, scented)
            }
        }
    })

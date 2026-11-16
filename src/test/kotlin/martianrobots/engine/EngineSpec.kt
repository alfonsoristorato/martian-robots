package martianrobots.engine

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import martianrobots.fixtures.SampleInput
import martianrobots.fixtures.commandsOf
import martianrobots.model.Grid
import martianrobots.model.Mission
import martianrobots.model.Orientation
import martianrobots.model.Orientation.EAST
import martianrobots.model.Orientation.NORTH
import martianrobots.model.Position
import martianrobots.model.Robot
import martianrobots.model.RobotInstructions

private val grid = Grid(5, 3)

private fun robot(
    x: Int,
    y: Int,
    orientation: Orientation,
    codes: String,
) = RobotInstructions(Robot(Position(x, y), orientation), commandsOf(codes))

class EngineSpec :
    FreeSpec({
        "runs the sample mission" {
            Engine.run(SampleInput.mission) shouldBe SampleInput.finalRobots
        }

        "returns nothing for a mission with no robots" {
            Engine.run(Mission(grid, emptyList())).shouldBeEmpty()
        }

        "a robot with no commands ends where it started" {
            Engine.run(Mission(grid, listOf(robot(2, 2, EAST, "")))) shouldBe listOf(Robot(Position(2, 2), EAST))
        }

        "a lost robot ignores the rest of its commands" {
            Engine.run(Mission(grid, listOf(robot(0, 3, NORTH, "FRF")))) shouldBe
                listOf(Robot(Position(0, 3), NORTH, lost = true))
        }

        "scents" - {
            "a scent left by a lost robot saves a later robot at the same point" {
                val mission = Mission(grid, listOf(robot(0, 3, NORTH, "F"), robot(0, 3, NORTH, "FR")))

                Engine.run(mission) shouldBe
                    listOf(
                        Robot(Position(0, 3), NORTH, lost = true),
                        Robot(Position(0, 3), EAST),
                    )
            }

            "a scent does not save a robot falling from a different point" {
                val mission = Mission(grid, listOf(robot(0, 3, NORTH, "F"), robot(1, 3, NORTH, "F")))

                Engine.run(mission) shouldBe
                    listOf(
                        Robot(Position(0, 3), NORTH, lost = true),
                        Robot(Position(1, 3), NORTH, lost = true),
                    )
            }

            "on a one-point grid a single scent blocks falling off every edge" {
                val mission = Mission(Grid(0, 0), listOf(robot(0, 0, NORTH, "F"), robot(0, 0, EAST, "FRFRFRF")))

                Engine.run(mission) shouldBe
                    listOf(
                        Robot(Position(0, 0), NORTH, lost = true),
                        Robot(Position(0, 0), NORTH),
                    )
            }

            "scents build up, so a scent still saves a robot several robots later" {
                val mission = Mission(grid, listOf(robot(1, 3, NORTH, "F"), robot(0, 3, NORTH, "F"), robot(1, 3, NORTH, "F")))

                Engine.run(mission) shouldBe
                    listOf(
                        Robot(Position(1, 3), NORTH, lost = true),
                        Robot(Position(0, 3), NORTH, lost = true),
                        Robot(Position(1, 3), NORTH),
                    )
            }
        }
    })

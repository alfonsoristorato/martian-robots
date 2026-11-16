package martianrobots.fixtures

import martianrobots.model.Grid
import martianrobots.model.Mission
import martianrobots.model.Orientation.EAST
import martianrobots.model.Orientation.NORTH
import martianrobots.model.Orientation.SOUTH
import martianrobots.model.Orientation.WEST
import martianrobots.model.Position
import martianrobots.model.Robot
import martianrobots.model.RobotInstructions

object SampleInput {
    val lines =
        """
        5 3
        1 1 E
        RFRFRFRF
        3 2 N
        FRRFLLFFRRFLL
        0 3 W
        LLFFFLFLFL
        """.trimIndent().lines()

    val mission =
        Mission(
            grid = Grid(5, 3),
            robots =
                listOf(
                    RobotInstructions(Robot(Position(1, 1), EAST), commandsOf("RFRFRFRF")),
                    RobotInstructions(Robot(Position(3, 2), NORTH), commandsOf("FRRFLLFFRRFLL")),
                    RobotInstructions(Robot(Position(0, 3), WEST), commandsOf("LLFFFLFLFL")),
                ),
        )

    val finalRobots =
        listOf(
            Robot(Position(1, 1), EAST),
            Robot(Position(3, 3), NORTH, lost = true),
            Robot(Position(2, 3), SOUTH),
        )
}

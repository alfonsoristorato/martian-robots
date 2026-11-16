package martianrobots.command

import martianrobots.model.Grid
import martianrobots.model.Robot

/** Turns the robot 90 degrees right, staying on the same grid point. */
data object Right : Command {
    override val code = 'R'

    override fun execute(
        robot: Robot,
        grid: Grid,
    ) = CommandResult(robot.copy(orientation = robot.orientation.right()), grid)
}

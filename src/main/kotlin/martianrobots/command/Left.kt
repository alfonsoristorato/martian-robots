package martianrobots.command

import martianrobots.model.Grid
import martianrobots.model.Robot

/** Turns the robot 90 degrees left, staying on the same grid point. */
data object Left : Command {
    override val code = 'L'

    override fun execute(
        robot: Robot,
        grid: Grid,
    ) = CommandResult(robot.copy(orientation = robot.orientation.left()), grid)
}

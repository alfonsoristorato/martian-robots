package martianrobots.command

import martianrobots.model.Grid
import martianrobots.model.Robot

/**
 * Moves the robot one grid point in the direction it faces. Moving off the grid loses the robot and leaves a
 * scent on its last position, unless a scent is already there, in which case the move is ignored.
 */
data object Forward : Command {
    override val code = 'F'

    override fun execute(
        robot: Robot,
        grid: Grid,
    ): CommandResult {
        val next = robot.position.step(robot.orientation)
        return when {
            next in grid -> CommandResult(robot.copy(position = next), grid)
            robot.position in grid.scents -> CommandResult(robot, grid)
            else -> CommandResult(robot.copy(lost = true), grid.withScent(robot.position))
        }
    }
}

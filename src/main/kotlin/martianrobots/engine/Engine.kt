package martianrobots.engine

import martianrobots.command.CommandResult
import martianrobots.model.Mission
import martianrobots.model.Robot
import martianrobots.model.RobotInstructions

/** Runs a mission's robots in order and returns each robot's final state, in input order. */
object Engine {
    fun run(mission: Mission): List<Robot> {
        // the grid carries over between robots, so scents left by lost robots protect the ones after them
        var grid = mission.grid
        return mission.robots.map { instructions ->
            val result = runRobot(instructions, CommandResult(instructions.robot, grid))
            grid = result.grid
            result.robot
        }
    }

    private fun runRobot(
        instructions: RobotInstructions,
        start: CommandResult,
    ): CommandResult =
        instructions.commands.fold(start) { state, command ->
            // a lost robot is gone, so it ignores the rest of its commands
            if (state.robot.lost) state else command.execute(state.robot, state.grid)
        }
}

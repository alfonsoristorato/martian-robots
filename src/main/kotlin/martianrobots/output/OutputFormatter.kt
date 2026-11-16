package martianrobots.output

import martianrobots.model.Robot

/** Formats a robot's final state as `x y orientation`, followed by `LOST` when it fell off the grid. */
object OutputFormatter {
    fun format(robot: Robot): String =
        buildString {
            append("${robot.position.x} ${robot.position.y} ${robot.orientation.code}")
            if (robot.lost) append(" LOST")
        }
}

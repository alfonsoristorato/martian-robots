package martianrobots.model

import martianrobots.command.Command

data class RobotInstructions(
    val robot: Robot,
    val commands: List<Command>,
)

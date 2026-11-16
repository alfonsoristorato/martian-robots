package martianrobots.model

data class RobotInstructions(
    val robot: Robot,
    val commands: List<Command>,
)

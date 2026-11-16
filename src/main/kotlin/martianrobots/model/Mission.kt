package martianrobots.model

/** A grid and the robots to run on it, in order. */
data class Mission(
    val grid: Grid,
    val robots: List<RobotInstructions>,
)

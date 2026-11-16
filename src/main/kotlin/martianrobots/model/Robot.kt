package martianrobots.model

data class Robot(
    val position: Position,
    val orientation: Orientation,
    val lost: Boolean = false,
)

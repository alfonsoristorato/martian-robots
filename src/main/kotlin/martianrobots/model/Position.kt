package martianrobots.model

data class Position(
    val x: Int,
    val y: Int,
) {
    fun step(orientation: Orientation) = Position(x + orientation.stepX, y + orientation.stepY)
}

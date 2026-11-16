package martianrobots.model

data class Grid(
    val maxX: Int,
    val maxY: Int,
    val scents: Set<Position> = emptySet(),
) {
    operator fun contains(position: Position) = position.x in 0..maxX && position.y in 0..maxY

    fun withScent(position: Position) = copy(scents = scents + position)
}

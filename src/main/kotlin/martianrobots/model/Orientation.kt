package martianrobots.model

enum class Orientation(
    val code: Char,
    val stepX: Int,
    val stepY: Int,
) {
    NORTH('N', 0, 1),
    EAST('E', 1, 0),
    SOUTH('S', 0, -1),
    WEST('W', -1, 0),
    ;

    fun left() =
        when (this) {
            NORTH -> WEST
            WEST -> SOUTH
            SOUTH -> EAST
            EAST -> NORTH
        }

    fun right() =
        when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }

    companion object {
        fun fromCode(code: String): Orientation? = entries.firstOrNull { it.code.toString() == code }
    }
}

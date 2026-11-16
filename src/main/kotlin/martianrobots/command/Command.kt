package martianrobots.command

import martianrobots.model.Grid
import martianrobots.model.Robot

/** The robot and grid after a command ran. The grid changes only when a lost robot leaves a scent. */
data class CommandResult(
    val robot: Robot,
    val grid: Grid,
)

/** A single robot instruction. A new command type is a new implementation, registered in [all]. */
sealed interface Command {
    val code: Char

    fun execute(
        robot: Robot,
        grid: Grid,
    ): CommandResult

    companion object {
        // NOTE: With reflect we could have done `Command::class.sealedSubclasses.mapNotNull { it.objectInstance }` - but it seemed overkill for a 3-entry list.
        val all: List<Command> = listOf(Left, Right, Forward)

        fun fromCode(code: Char): Command? = all.firstOrNull { it.code == code }
    }
}

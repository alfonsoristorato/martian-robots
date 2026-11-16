package martianrobots.model

enum class Command(
    val code: Char,
) {
    LEFT('L'),
    RIGHT('R'),
    FORWARD('F'),
    ;

    companion object {
        fun fromCode(code: Char): Command? = entries.firstOrNull { it.code == code }
    }
}

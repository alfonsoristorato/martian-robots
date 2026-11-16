package martianrobots.exception

/** The input does not follow the expected format. [lineNumber] is 1-based, null when no line applies. */
class InvalidInputException(
    val lineNumber: Int?,
    reason: String,
) : MartianRobotsException(if (lineNumber == null) reason else "Line $lineNumber: $reason")

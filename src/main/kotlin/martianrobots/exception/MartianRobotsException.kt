package martianrobots.exception

/** Base for every error the app raises on purpose. */
sealed class MartianRobotsException(
    message: String,
) : RuntimeException(message)

package martianrobots.exception

/** The input file passed as an argument does not exist or is not a file. */
class InputFileNotFoundException(
    path: String,
) : MartianRobotsException("no input file at '$path'")

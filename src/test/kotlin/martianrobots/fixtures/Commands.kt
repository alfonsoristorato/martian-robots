package martianrobots.fixtures

import martianrobots.command.Command

fun commandsOf(codes: String): List<Command> = codes.map { requireNotNull(Command.fromCode(it)) }

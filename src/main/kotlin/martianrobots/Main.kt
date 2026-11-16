package martianrobots

fun main() {
    val input = generateSequence(::readlnOrNull).toList()
    // shortcut: echoes input until the simulation exists, then print its output instead
    input.forEach(::println)
}

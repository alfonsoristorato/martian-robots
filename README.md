# martian-robots

A Kotlin command-line solution to the Martian Robots problem. The full problem statement is in [PROBLEM_STATEMENT.md](PROBLEM_STATEMENT.md).

## Requirements

- JDK 25 installed locally (Gradle picks it up as the toolchain).

## Build

```
./gradlew build
```

This compiles, formats and lints with ktlint, runs the tests and checks coverage with Kover.

## Run

The program reads the input from stdin:

```
./gradlew run -q < input/sample-input.txt
```

[input/sample-input.txt](input/sample-input.txt) holds the sample input from the problem statement.

## Tests

```
./gradlew test
```

## Assumptions and decisions

### Input

- **Blank lines**: skipped wherever a grid or robot position is expected, so blank lines before the grid,
  between robots and at the end of the input are fine.
- **Empty instructions**: the line right after a robot position is always its instructions, even when blank.
  A blank instruction line means the robot has no moves and reports its starting position.
- **Missing instructions**: a robot position on the last line, with no instruction line after it, is an error.
- **Whitespace**: any amount of spaces or tabs is accepted around and between tokens. Spaces inside an
  instruction string are rejected.
- **Case**: orientations (`N`, `E`, `S`, `W`) and commands (`L`, `R`, `F`) are case-sensitive, so `n` or `f`
  are rejected.
- **Limits**: grid coordinates must be between 0 and 50. Instruction strings can be at most 99 characters.
- **Starting position**: a robot must start inside the grid.
- **Errors**: any invalid line stops the program with a message naming the line number and the reason.

### Design

- **Mission**: the parser turns the input into a `Mission` (the grid plus each robot with its commands).
- **New commands**: commands are an enum (`Command`), so a new command type is a new entry plus its behaviour.

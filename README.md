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

[input/sample-input.txt](input/sample-input.txt) holds the sample input from the problem statement, and prints:

```
1 1 E
3 3 N LOST
2 3 S
```

Any other file in the same format can be passed the same way. Invalid input prints an error naming the line
and the reason to stderr, for example `Error: Line 2: unknown orientation 'X', expected one of [N, E, S, W]`,
and exits with code 1 (Gradle then also reports the run task as failed).

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
- **Errors**: any invalid line stops the program with a message naming the line number and the reason. The
  whole input is validated before any robot runs, so invalid input never produces partial output.

### Scent

- A scent belongs to a grid point, not to a direction: once a robot is lost from a point, any later move off
  the grid from that point is ignored, whichever edge it would cross. Moves from that point that stay on the
  grid are unaffected.

### Design

- **Mission**: the parser turns the input into a `Mission` (the grid plus each robot with its commands).
- **Orientation**: each direction carries its own one-point step (`stepX`, `stepY`), so moving forward is a
  plain addition with no branching. Turning left and right uses an exhaustive `when` which means that adding a new direction
  does not compile until its turns are defined.
- **Commands**: `Command` is a sealed interface with one object per command (`Left`, `Right`, `Forward`), each
  owning its behaviour. A command takes the robot and the grid and returns both, since `Forward` can leave a
  scent. A new command type is one new object plus an entry in `Command.all`, which the parser uses to
  recognise its code. A test fails if an implementation is missing from that list.
- **Engine**: runs the robots in input order, carrying the grid (and so its scents) from one robot to the next.
  A lost robot ignores the rest of its commands.

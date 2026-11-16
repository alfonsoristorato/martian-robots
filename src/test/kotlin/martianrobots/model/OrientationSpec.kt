package martianrobots.model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import martianrobots.model.Orientation.EAST
import martianrobots.model.Orientation.NORTH
import martianrobots.model.Orientation.SOUTH
import martianrobots.model.Orientation.WEST

class OrientationSpec :
    FreeSpec({
        "properties" - {
            "four turns in the same direction face the starting orientation again" {
                checkAll(Exhaustive.enum<Orientation>()) { orientation ->
                    orientation
                        .right()
                        .right()
                        .right()
                        .right() shouldBe orientation
                    orientation
                        .left()
                        .left()
                        .left()
                        .left() shouldBe orientation
                }
            }

            "a left turn undoes a right turn" {
                checkAll(Exhaustive.enum<Orientation>()) { orientation ->
                    orientation.right().left() shouldBe orientation
                    orientation.left().right() shouldBe orientation
                }
            }
        }

        withTests(
            nameFn = { (from, to) -> "turning right from $from faces $to" },
            NORTH to EAST,
            EAST to SOUTH,
            SOUTH to WEST,
            WEST to NORTH,
        ) { (from, to) ->
            from.right() shouldBe to
        }

        withTests(
            nameFn = { (from, to) -> "turning left from $from faces $to" },
            NORTH to WEST,
            WEST to SOUTH,
            SOUTH to EAST,
            EAST to NORTH,
        ) { (from, to) ->
            from.left() shouldBe to
        }

        withTests(
            nameFn = { (orientation, expected) -> "stepping $orientation from 1 1 reaches ${expected.x} ${expected.y}" },
            NORTH to Position(1, 2),
            EAST to Position(2, 1),
            SOUTH to Position(1, 0),
            WEST to Position(0, 1),
        ) { (orientation, expected) ->
            Position(1, 1).step(orientation) shouldBe expected
        }
    })

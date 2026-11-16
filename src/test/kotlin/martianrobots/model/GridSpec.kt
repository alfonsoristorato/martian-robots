package martianrobots.model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

class GridSpec :
    FreeSpec({
        val grid = Grid(5, 3)

        withTests(
            nameFn = { "contains ${it.x} ${it.y}, on or inside the edges" },
            Position(0, 0),
            Position(5, 3),
            Position(2, 1),
        ) { position ->
            grid.contains(position).shouldBeTrue()
        }

        withTests(
            nameFn = { "does not contain ${it.x} ${it.y}, past an edge" },
            Position(-1, 0),
            Position(0, -1),
            Position(6, 3),
            Position(5, 4),
        ) { position ->
            grid.contains(position).shouldBeFalse()
        }

        "withScent adds the scent without changing the original grid" {
            val scented = grid.withScent(Position(3, 3))

            scented.scents shouldContainExactly setOf(Position(3, 3))
            grid.scents.shouldBeEmpty()
        }
    })

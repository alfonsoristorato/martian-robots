package martianrobots.output

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import martianrobots.fixtures.SampleInput
import martianrobots.model.Orientation
import martianrobots.model.Position
import martianrobots.model.Robot

class OutputFormatterSpec :
    FreeSpec({
        withTests(
            nameFn = { "formats a robot facing ${it.name} with code ${it.code}" },
            Orientation.entries,
        ) { orientation ->
            OutputFormatter.format(Robot(Position(1, 2), orientation)) shouldBe "1 2 ${orientation.code}"
        }

        "appends LOST for a lost robot" {
            OutputFormatter.format(Robot(Position(3, 3), Orientation.NORTH, lost = true)) shouldBe "3 3 N LOST"
        }

        "formats the sample's final robots as the sample output" {
            SampleInput.finalRobots.map(OutputFormatter::format) shouldBe SampleInput.outputLines
        }
    })

package martianrobots

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import martianrobots.fixtures.SampleInput
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.PrintStream

private data class AppResult(
    val exitCode: Int,
    val out: String,
    val err: String,
)

private fun runApp(input: InputStream): AppResult {
    val out = ByteArrayOutputStream()
    val err = ByteArrayOutputStream()
    val exitCode = run(input, PrintStream(out, true), PrintStream(err, true))
    return AppResult(exitCode, out.toString().trim(), err.toString().trim())
}

private fun runApp(input: String) = runApp(input.byteInputStream())

class MainSpec :
    FreeSpec({
        "runs the shipped sample input file end to end" {
            val result = runApp(File("input/sample-input.txt").inputStream())

            result shouldBe AppResult(0, SampleInput.outputLines.joinToString("\n"), "")
        }

        "prints one line per robot in input order" {
            val result =
                runApp(
                    """
                    5 3
                    0 3 N
                    F
                    2 2 S

                    0 3 N
                    FR
                    """.trimIndent(),
                )

            result.out shouldBe
                """
                0 3 N LOST
                2 2 S
                0 3 E
                """.trimIndent()
            result.exitCode shouldBe 0
        }

        "prints nothing for a grid with no robots" {
            val result = runApp("5 3")

            result.out.shouldBeEmpty()
            result.exitCode shouldBe 0
        }

        "invalid input" - {
            "reports the error on stderr and exits with 1" {
                val input =
                    """
                    5 3
                    1 1 X
                    F
                    """.trimIndent()

                runApp(input) shouldBe
                    AppResult(1, "", "Error: Line 2: unknown orientation 'X', expected one of [N, E, S, W]")
            }

            "prints no robots at all when a later line is invalid" {
                val result =
                    runApp(
                        """
                        5 3
                        1 1 N
                        F
                        2 2 E
                        FX
                        """.trimIndent(),
                    )

                result.out.shouldBeEmpty()
                result.exitCode shouldBe 1
            }

            "reports empty input" {
                runApp("").exitCode shouldBe 1
            }
        }
    })

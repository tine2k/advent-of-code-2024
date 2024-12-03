import java.util.regex.MatchResult
import java.util.regex.Pattern

sealed interface Instruction
class Multiplication(val a: Int, val b: Int) : Instruction
data object Dont : Instruction
data object Do : Instruction

data class State(val value: Int, val enabled: Boolean)

fun main() {
    val testInput = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"

    fun solve1(lines: List<String>): Long {
        val pattern = Pattern.compile("mul\\((\\d*),(\\d*)\\)")
        return lines.sumOf { line ->
            pattern.matcher(line).results()
                .map { it.group(1).toInt() to it.group(2).toInt() }
                .toList()
                .sumOf { (a, b) -> a * b }
        }.toLong()
    }

    val testInput2 = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"

    fun toInstruction(it: MatchResult) = when (it.group()) {
        "don\'t()" -> Dont
        "do()" -> Do
        else -> Multiplication(it.group(1).toInt(), it.group(2).toInt())
    }

    fun solve2(lines: List<String>): Long {
        val pattern = Pattern.compile("mul\\((\\d*),(\\d*)\\)|(do\\(\\))|(don't\\(\\))")
        return lines.sumOf { line ->
            pattern.matcher(line).results().map { toInstruction(it) }.toList()
                .runningFold(State(0, true)) { state, instruction ->
                    when (instruction) {
                        is Multiplication -> {
                            if (state.enabled) {
                                State(state.value + (instruction.a * instruction.b), true)
                            } else {
                                state
                            }
                        }

                        is Dont -> State(state.value, false)
                        is Do -> State(state.value, true)
                    }
                }.last().value
        }.toLong()
    }

    header(1)
    test(::solve1, testInput, 161)
    solve(::solve1)

    header(2)
    test(::solve2, testInput2, 48)
    solve(::solve2)
}

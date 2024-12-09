import Operator.*
import kotlin.math.pow

enum class Operator { PLUS, MULTIPLY, COMBINE }

fun main() {
    data class Equation(val result: Long, val numbers: List<Long>)

    val testInput = "190: 10 19\n" +
            "3267: 81 40 27\n" +
            "83: 17 5\n" +
            "156: 15 6\n" +
            "7290: 6 8 6 15\n" +
            "161011: 16 10 13\n" +
            "192: 17 8 14\n" +
            "21037: 9 7 18 13\n" +
            "292: 11 6 16 20"

    fun getEquation(input: String): Equation {
        val split = input.split(":")
        return Equation(split[0].toLong(), split[1].trim().split(" ").map { it.toLong() })
    }

    fun pow(a: Long, b: Long): Long = a.toDouble().pow(b.toDouble()).toLong()

    fun getOperators(index: Int, count: Int, allowedOperators: List<Operator>): List<Operator> =
        (0..<count)
            .map { index / pow(allowedOperators.count().toLong(), it.toLong()) % allowedOperators.count() }
            .map { Operator.entries[it.toInt()] }

    fun calc(operator: Operator, sum: Long, next: Long): Long =
        when (operator) {
            PLUS -> sum + next
            MULTIPLY -> sum * next
            COMBINE -> "$sum$next".toLong()
        }

    fun isValidCombination(equation: Equation, index: Int, allowedOperators: List<Operator>): Boolean {
        val operators = getOperators(index, equation.numbers.size - 1, allowedOperators)
        return equation.numbers.reduceIndexed { i, sum, next -> calc(operators[i - 1], sum, next) } == equation.result
    }

    fun isValid(equation: Equation, allowedOperators: List<Operator>): Boolean =
        (0..allowedOperators.size.toDouble().pow(equation.numbers.size.toDouble()).toInt())
            .any { isValidCombination(equation, it, allowedOperators) }

    fun solve1(lines: List<String>): Long =
        lines.map { getEquation(it) }
            .filter { isValid(it, listOf(PLUS, MULTIPLY)) }
            .sumOf { it.result }

    fun solve2(lines: List<String>): Long =
        lines.map { getEquation(it) }
            .filter { isValid(it, listOf(PLUS, MULTIPLY, COMBINE)) }
            .sumOf { it.result }

    header(1)
    test(::solve1, testInput, 3749)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 11387)
    solve(::solve2)
}

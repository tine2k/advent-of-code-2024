import com.google.ortools.Loader
import com.google.ortools.modelbuilder.LinearExpr
import com.google.ortools.sat.CpModel
import com.google.ortools.sat.CpSolver
import com.google.ortools.sat.IntVar
import com.sun.org.apache.xpath.internal.operations.Variable
import org.apache.commons.lang3.math.NumberUtils.toDouble
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.DecompositionSolver
import org.apache.commons.math3.linear.LUDecomposition
import org.checkerframework.checker.units.qual.m
import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Point(val x: Long, val y: Long)
    data class Machine(val a: Point, val b: Point, val prize: Point, val multiplier: Long)
    data class Solution(val a: Long, val b: Long, val multiplier: Long)

    val testInput = "Button A: X+94, Y+34\n" +
            "Button B: X+22, Y+67\n" +
            "Prize: X=8400, Y=5400\n" +
            "\n" +
            "Button A: X+26, Y+66\n" +
            "Button B: X+67, Y+21\n" +
            "Prize: X=12748, Y=12176\n" +
            "\n" +
            "Button A: X+17, Y+86\n" +
            "Button B: X+84, Y+37\n" +
            "Prize: X=7870, Y=6450\n" +
            "\n" +
            "Button A: X+69, Y+23\n" +
            "Button B: X+27, Y+71\n" +
            "Prize: X=18641, Y=10279"

    fun parsePoint(line: String, separator: Char): Point {
        val tokens = line.split(":")[1].split(",")
        return Point(tokens[0].split(separator)[1].toLong(), tokens[1].split(separator)[1].toLong())
    }

    fun parseButton(line: String): Point = parsePoint(line, '+')

    fun parsePrize(line: String): Point = parsePoint(line, '=')

    fun parseMachines(lines: List<String>) = lines.chunked(4)
        .map { Machine(parseButton(it[0]), parseButton(it[1]), parsePrize(it[2]), 1) }

    fun calcBPushes(m: Machine, aPushes: Long): Long {
        val remainingX = m.prize.x - (m.a.x * aPushes)
        val remainingY = m.prize.y - (m.a.y * aPushes)
        return if (remainingX % m.b.x == 0L && remainingY % m.b.y == 0L && remainingX / m.b.x == remainingY / m.b.y)
            remainingX / m.b.x
        else {
            -1
        }
    }

    fun getRange(m: Machine): LongRange {
        val from = 0
        val to = max(m.prize.x / min(m.a.x, m.b.x), m.prize.y / min(m.a.y, m.b.y))
        return from..to
    }

    fun calcMachine(m: Machine): Long =
        getRange(m).map { Solution(it, calcBPushes(m, it), m.multiplier) }
            .firstOrNull { it.b >= 0 }?.let { (it.a * 3 + it.b) * it.multiplier } ?: 0


    fun solve1(lines: List<String>) =
        parseMachines(lines).sumOf { calcMachine(it) }

    fun solve2(lines: List<String>): Long =
        parseMachines(lines).sumOf {
            val newPrize = Point(it.prize.x + 10000000000000, it.prize.y + 10000000000000)

            val bigPartsX = newPrize.x / (it.a.x * it.b.x)
            val prizeBigPart = if (it.a.x * 3 < it.b.x) {
                bigPartsX * it.a.x * 3
            } else {
                bigPartsX * it.b.x
            }
            val mod = it.a.x * it.b.x

            val calcCosts = calcMachine(Machine(it.a, it.b, Point(newPrize.x % mod, newPrize.y - (bigPartsX * it.a.y * it.b.y )), 1))
            if (calcCosts > 0) {
                calcCosts + prizeBigPart
            } else {
                calcCosts
            }
        }

    header(1)
    test(::solve1, testInput, 480)
    solve(::solve1)

    // tried 484710551248, too low
    // tried 118500000094584
    // tried 99672903872580
    header(2)
    solve(::solve2)
}

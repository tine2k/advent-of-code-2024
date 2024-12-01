import kotlin.math.absoluteValue

fun main() {
    val testInput = "3   4\n" +
            "4   3\n" +
            "2   5\n" +
            "1   3\n" +
            "3   9\n" +
            "3   3"

    fun getInput(lines: List<String>): Pair<List<Int>, List<Int>> {
        val input = lines.map { it.split("   ").map { s -> s.toInt() } }
        val firstList = input.map { it[0] }.sorted()
        val secondList = input.map { it[1] }.sorted()
        return firstList to secondList
    }

    fun solve1(lines: List<String>): Long {
        val (firstList, secondList) = getInput(lines)
        return firstList.mapIndexed { index, value -> (secondList[index] - value).absoluteValue }.sum().toLong()
    }

    fun solve2(lines: List<String>): Long {
        val (firstList, secondList) = getInput(lines)
        val countList = secondList.groupBy { it }.mapValues { it.value.size  }
        return firstList.sumOf { value -> value * (countList[value] ?: 0) }.toLong()
    }
    
    header(1)
    test(::solve1, testInput, 11)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 31)
    solve(::solve2)
}

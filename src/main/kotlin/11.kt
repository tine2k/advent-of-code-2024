fun main() {
    val testInput1 = "0 1 10 99 999"
    val testInput2 = "125 17"

    fun mapStone(stone: Long): List<Long> {
        val stoneString = stone.toString()
        return when {
            stone == 0L -> listOf(1)
            stoneString.length % 2L == 0L -> stoneString.chunked(stoneString.length / 2).map { it.toLong() }
            else -> listOf(stone * 2024L)
        }
    }

    fun calcValue(stone: Long, times: Int, timesTotal: Int, cache: MutableMap<Pair<Long, Int>, Long>): Long {
        val x = cache[stone to times]
        if (x != null) {
            return x
        }

        val returnValue = if (times == timesTotal) {
            1
        } else {
            mapStone(stone).sumOf { calcValue(it, times + 1, timesTotal, cache) }
        }

        cache[stone to times] = returnValue
        return returnValue
    }

    fun solve(lines: List<String>, timesTotal: Int): Long {
        val stones = lines[0].split(" ").map { it.toLong() }
        return stones.sumOf { calcValue(it, 0, timesTotal, mutableMapOf()) }
    }

    fun solve1Times1(lines: List<String>): Long = solve(lines, 1)

    fun solve1Times6(lines: List<String>): Long = solve(lines, 6)

    fun solve1Times25(lines: List<String>): Long = solve(lines, 25)

    fun solve2(lines: List<String>): Long = solve(lines, 75)

    header(1)
    test(::solve1Times1, testInput1, 7)
    test(::solve1Times6, testInput2, 22)
    test(::solve1Times25, testInput2, 55312)
    solve(::solve1Times25)

    header(2)
    solve(::solve2)
}

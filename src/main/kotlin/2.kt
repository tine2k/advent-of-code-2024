fun main() {
    val testInput = "7 6 4 2 1\n" +
            "1 2 7 8 9\n" +
            "9 7 6 2 1\n" +
            "1 3 2 4 5\n" +
            "8 6 4 4 1\n" +
            "1 3 6 7 9"

    fun createAcceleration(report: List<Int>, increase: Boolean): List<Boolean> =
        report.zipWithNext { a, b -> (b - a) * (if (increase) 1 else -1)  in 1..3 }

    fun isAllOneDirection(report: List<Int>, increase: Boolean): Boolean =
        createAcceleration(report, increase).count { it } == report.size - 1

    fun isSafeNoException(report: List<Int>): Boolean = isAllOneDirection(report, false) || isAllOneDirection(report, true)

    fun isAllOneDirectionWithException(report: List<Int>, increase: Boolean): Boolean {
        val acceleration = createAcceleration(report, increase)
        val accelerationWithoutErrorLeft = report.filterIndexed { index, _ -> index != acceleration.indexOf(false) }
        val accelerationWithoutErrorRight = report.filterIndexed { index, _ -> index != acceleration.indexOf(false) + 1 }
        return isAllOneDirection(accelerationWithoutErrorLeft, increase) || isAllOneDirection(accelerationWithoutErrorRight, increase)
    }

    fun isSafeOneException(report: List<Int>): Boolean =
        isAllOneDirectionWithException(report, true) || isAllOneDirectionWithException(report, false)

    fun createReport(line: String) = line.split(" ").map { it.toInt() }

    fun solve1(lines: List<String>): Long =
        lines.count { line -> isSafeNoException(createReport(line)) }.toLong()

    fun solve2(lines: List<String>): Long =
        lines.count { line -> isSafeNoException(createReport(line)) || isSafeOneException(createReport(line)) }.toLong()

    header(1)
    test(::solve1, testInput, 2)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 4)
    solve(::solve2)
}

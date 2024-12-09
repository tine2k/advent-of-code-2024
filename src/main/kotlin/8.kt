import com.google.common.collect.Sets

fun main() {
    val testInput = "............\n" +
            "........0...\n" +
            ".....0......\n" +
            ".......0....\n" +
            "....0.......\n" +
            "......A.....\n" +
            "............\n" +
            "............\n" +
            "........A...\n" +
            ".........A..\n" +
            "............\n" +
            "............"

    data class Antenna(val point: Point, val frequency: Char)

    fun getAntennas(lines: List<String>): List<Antenna> =
        lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, char ->
                Antenna(Point(x, y), char).takeIf { char != '.' }
            }
        }

    fun isValidPoint(point: Point, lines: List<String>): Boolean =
        point.x >= 0 && point.y >= 0 && point.y < lines.size && point.x < lines[0].length

    fun calcPoint(a: Antenna, b: Antenna): Point =
        Point(a.point.x + (a.point.x - b.point.x), a.point.y + (a.point.y - b.point.y))

    fun findAntinodeNeighbours(a: Antenna, b: Antenna, lines: List<String>): List<Point> =
        listOf(calcPoint(a, b), calcPoint(b, a)).filter { isValidPoint(it, lines) }

    fun calcPointsInLine(a: Antenna, b: Antenna, lines: List<String>): List<Point> {
        val x = (a.point.x - b.point.x)
        val y = (a.point.y - b.point.y)
        return generateSequence(a.point) { Point(it.x + x, it.y + y).takeIf { isValidPoint(it, lines) } }.toList()
    }

    fun findAntinodesInLine(a: Antenna, b: Antenna, lines: List<String>): List<Point> =
        calcPointsInLine(a, b, lines) + calcPointsInLine(b, a, lines)

    fun findCombinations(antennas: List<Antenna>): List<Pair<Antenna, Antenna>> =
        Sets.combinations(antennas.toSet(), 2)
            .map { it.toList() }
            .map { it[0] to it[1] }

    fun solve(lines: List<String>, fn: (a: Antenna, b: Antenna, lines: List<String>) -> List<Point>): Long =
        getAntennas(lines).groupBy { it.frequency }
            .flatMap { group -> findCombinations(group.value).flatMap { fn(it.first, it.second, lines) } }
            .distinct()
            .size
            .toLong()

    fun solve1(lines: List<String>): Long = solve(lines, ::findAntinodeNeighbours)

    fun solve2(lines: List<String>): Long = solve(lines, ::findAntinodesInLine)

    header(1)
    test(::solve1, testInput, 14)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 34)
    solve(::solve2)
}

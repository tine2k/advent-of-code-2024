fun main() {
    val testInput = "" +
            "MMMSXXMASM\n" +
            "MSAMXMSMSA\n" +
            "AMXSXMAAMM\n" +
            "MSAMASMSMX\n" +
            "XMASAMXAMM\n" +
            "XXAMMXXAMA\n" +
            "SMSMSASXSS\n" +
            "SAXAMASAAA\n" +
            "MAMMMXMMMM\n" +
            "MXMXAXMASX"

    data class CandidatePoint(val p: Point, val expectation: Char)
    data class Candidate(val points: List<CandidatePoint>)

    fun move(p: Point, direction: Int): Point =
        when (direction) {
            0 -> Point(p.x + 1, p.y - 1)
            1 -> Point(p.x + 1, p.y + 0)
            2 -> Point(p.x + 1, p.y + 1)
            3 -> Point(p.x + 0, p.y - 1)
            4 -> Point(p.x + 0, p.y + 1)
            5 -> Point(p.x - 1, p.y - 1)
            6 -> Point(p.x - 1, p.y + 0)
            7 -> Point(p.x - 1, p.y + 1)
            else -> error("wrong direction")
        }

    fun buildXmasCandidate(p: Point, direction: Int): Candidate {
        val x = CandidatePoint(p, 'X')
        val m = CandidatePoint(move(p, direction), 'M')
        val a = CandidatePoint(move(m.p, direction), 'A')
        val s = CandidatePoint(move(a.p, direction), 'S')
        return Candidate(listOf(x, m, a, s))
    }

    fun getFieldValue(lines: List<String>, point: Point): Char? =
        if (point.x < 0 || point.y < 0 || point.x >= lines[0].length || point.y >= lines.size) {
            null
        } else {
            lines[point.y][point.x]
        }

    fun isValid(lines: List<String>, candidate: Candidate): Boolean =
        candidate.points.all { getFieldValue(lines, it.p) == it.expectation }

    fun buildMasCrossCandidate(p: Point, chars: String): Candidate {
        val a = CandidatePoint(p, 'A')
        val m1 = CandidatePoint(move(p, 5), chars[0])
        val m2 = CandidatePoint(move(p, 7), chars[1])
        val s1 = CandidatePoint(move(p, 2), chars[2])
        val s2 = CandidatePoint(move(p, 0), chars[3])
        return Candidate(listOf(a, m1, m2, s1, s2))
    }

    fun solve(lines: List<String>, builder: (Int, Int) -> List<Candidate>) =
        lines.flatMapIndexed { y, line ->
            line.mapIndexed { x, _ -> builder(x, y).filter { isValid(lines, it) }.size }
        }.sum().toLong()

    fun solve1(lines: List<String>): Long = solve(lines) { x, y ->
        (0..7).map { buildXmasCandidate(Point(x, y), it) }
    }

    fun solve2(lines: List<String>): Long = solve(lines) { x, y ->
        listOf("MMSS", "MSSM", "SSMM", "SMMS").map { buildMasCrossCandidate(Point(x, y), it) }
    }

    header(1)
    test(::solve1, testInput, 18)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 9)
    solve(::solve2)
}

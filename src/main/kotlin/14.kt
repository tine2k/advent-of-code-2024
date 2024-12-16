fun main() {
    val testInput = "p=0,4 v=3,-3\n" +
            "p=6,3 v=-1,-3\n" +
            "p=10,3 v=-1,2\n" +
            "p=2,0 v=2,-1\n" +
            "p=0,0 v=1,3\n" +
            "p=3,0 v=-2,-2\n" +
            "p=7,6 v=-1,-3\n" +
            "p=3,0 v=-1,-2\n" +
            "p=9,3 v=2,3\n" +
            "p=7,3 v=-1,2\n" +
            "p=2,4 v=2,-3\n" +
            "p=9,5 v=-3,-3"

    data class Robot(val location: Point, val velocity: Point)

    fun getRobots(lines: List<String>) =
        lines.map {
            val p = it.split(" ")
            val loc = p[0].split("=")[1].split(",")
            val vel = p[1].split("=")[1].split(",")
            Robot(Point(loc[0].toInt(), loc[1].toInt()), Point(vel[0].toInt(), vel[1].toInt()))
        }

    fun move(robot: Robot, width: Int, height: Int): Robot {
        val newX = (robot.location.x + robot.velocity.x + width) % width
        val newY = (robot.location.y + robot.velocity.y + height) % height
        return Robot(Point(newX, newY), robot.velocity)
    }

    fun solve1(lines: List<String>, width: Int, height: Int, times: Int): Long {
        val movedRobots = (0..<times).fold(getRobots(lines)) { r, _ -> r.map { move(it, width, height) } }
        val q1 = movedRobots.count { it.location.x < width / 2 && it.location.y < height / 2 }.toLong()
        val q2 = movedRobots.count { it.location.x > width / 2 && it.location.y > height / 2 }.toLong()
        val q3 = movedRobots.count { it.location.x < width / 2 && it.location.y > height / 2 }.toLong()
        val q4 = movedRobots.count { it.location.x > width / 2 && it.location.y < height / 2 }.toLong()
        return q1 * q2 * q3 * q4
    }

    fun printRobots(robots: List<Robot>, width: Int, height: Int) {
        (0..<height).forEach { y ->
            (0..<width).forEach { x ->
                val count = robots.count { it.location.x == x && it.location.y == y }
                if (count > 0) {
                    print(count)
                } else {
                    print(".")
                }
            }
            println()
        }
        println()
    }

    fun findRobotsInARowMaxLength(robots: List<Robot>): Int =
        robots
            .sortedBy { it.location.x }
            .zipWithNext { a, b -> a.location.x + 1 == b.location.x }
            .fold(0 to 0) { (max, current), isConsecutive ->
                if (isConsecutive) {
                    val newCurrent = current + 1
                    max.coerceAtLeast(newCurrent) to newCurrent
                } else {
                    max to 0
                }
            }.first

    fun solve2(lines: List<String>, width: Int, height: Int): Long {
        (0..<1_000_000).fold(getRobots(lines)) { robots, index ->
            val counts = robots.groupBy { robot -> robot.location.y }
                .mapValues { robotsInRow -> findRobotsInARowMaxLength(robotsInRow.value) }

            if (counts.values.any { it > 10 }) {
                printRobots(robots, width, height)
                return index.toLong()
            }
            robots.map { move(it, width, height) }
        }
        return -1
    }

    fun solve1Small(lines: List<String>): Long = solve1(lines, 11, 7, 100)

    fun solve1Large(lines: List<String>): Long = solve1(lines, 101, 103, 100)

    fun solve2(lines: List<String>): Long = solve2(lines, 101, 103)

    header(1)
    test(::solve1Small, testInput, 12)
    solve(::solve1Large)

    header(2)
    test(::solve2, testInput, 6398)
    solve(::solve2)
}

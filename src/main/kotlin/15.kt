enum class FieldValue {
    FREE, BOX, WALL, ROBOT
}

fun main() {
    val testInput1 = "########\n" +
            "#..O.O.#\n" +
            "##@.O..#\n" +
            "#...O..#\n" +
            "#.#.O..#\n" +
            "#...O..#\n" +
            "#......#\n" +
            "########\n" +
            "\n" +
            "<^^>>>vv<v>>v<<"

    val testInput2 = "##########\n" +
            "#..O..O.O#\n" +
            "#......O.#\n" +
            "#.OO..O.O#\n" +
            "#..O@..O.#\n" +
            "#O#..O...#\n" +
            "#O..O..O.#\n" +
            "#.OO.O.OO#\n" +
            "#....O...#\n" +
            "##########\n" +
            "\n" +
            "<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^\n" +
            "vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v\n" +
            "><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<\n" +
            "<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^\n" +
            "^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><\n" +
            "^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^\n" +
            ">^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^\n" +
            "<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>\n" +
            "^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>\n" +
            "v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^\n"

    data class Data(val field: List<String>, val directions: List<Direction>)

    fun readData(lines: List<String>): Data {
        val split = lines.indexOf("")
        val field = lines.take(split)
        val directions = lines.drop(split + 1).joinToString("") { it.trim() }
            .map {
                when (it) {
                    '<' -> Direction.WEST
                    '>' -> Direction.EAST
                    '^' -> Direction.NORTH
                    'v' -> Direction.SOUTH
                    else -> error("Invalid character: $it")
                }
            }
        return Data(field, directions)
    }

    fun countBoxes(field: List<String>): Long =
        field.flatMapIndexed { indexTop, line ->
            line.mapIndexed { indexLeft, char ->
                if (char == 'O') {
                    indexTop * 100 + indexLeft
                } else {
                    0
                }
            }
        }.sum().toLong()

    fun findRobot(field: List<String>): Point {
        field.mapIndexed { indexTop, line ->
            line.mapIndexed { indexLeft, char ->
                if (char == '@') {
                    return Point(indexLeft, indexTop)
                }
            }
        }
        error("No robot found")
    }

    fun getFieldValue(field: List<String>, point: Point): FieldValue =
        when (field[point.y][point.x]) {
            '#' -> FieldValue.WALL
            '.' -> FieldValue.FREE
            'O' -> FieldValue.BOX
            else -> error("Invalid field value: ${field[point.x][point.y]}")
        }

    fun getCharOfFieldValue(value: FieldValue) =
        when (value) {
            FieldValue.WALL -> '#'
            FieldValue.FREE -> '.'
            FieldValue.BOX -> 'O'
            FieldValue.ROBOT -> '@'
        }

    fun replace(field: List<String>, point: Point, value: FieldValue): List<String> =
        field.mapIndexed { indexTop, line ->
            if (indexTop == point.y) {
                line.substring(0, point.x) + getCharOfFieldValue(value) + line.substring(point.x + 1)
            } else {
                line
            }
        }

    fun getBoxesTillFree(field: List<String>, robot: Point, direction: Point): Int {
        var curPos: Point = robot
        var boxCount = 0
        while (true) {
            curPos = curPos.plus(direction)
            when (getFieldValue(field, curPos)) {
                FieldValue.FREE -> {
                    return boxCount
                }

                FieldValue.BOX -> {
                    boxCount++
                }

                FieldValue.WALL -> {
                    return -1
                }

                else -> error("invalid state")
            }
        }
    }

    fun printField(field: List<String>) {
        println()
        field.forEach { line ->
            println(line)
        }
        println()
    }

    fun moveRobot(field: List<String>, robot: Point, direction: Direction): List<String> {
//        printField(field)

        val directionPoint = when (direction) {
            Direction.NORTH -> Point(0, -1)
            Direction.EAST -> Point(+1, 0)
            Direction.WEST -> Point(-1, 0)
            Direction.SOUTH -> Point(0, 1)
        }

        val boxesTillFree = getBoxesTillFree(field, robot, directionPoint)
        return if (boxesTillFree == -1) {
            field
        } else {
            val robotPosFree = replace(field, robot, FieldValue.FREE)
            val newRobotPos = replace(robotPosFree, robot.plus(directionPoint), FieldValue.ROBOT)
            if (boxesTillFree == 0) {
                newRobotPos
            } else {
                val newBoxPos = (0..<boxesTillFree).fold(robot.plus(directionPoint)) { p, _ -> p.plus(directionPoint) }
                replace(newRobotPos, newBoxPos, FieldValue.BOX)
            }
        }
    }


    fun solve1(lines: List<String>): Long {
        val data = readData(lines)
        val endField = data.directions.fold(data.field) { field, direction ->
            moveRobot(field, findRobot(field), direction)
        }
        return countBoxes(endField)
    }

    fun solve2(lines: List<String>): Long {
        return -1
    }

    header(1)
    test(::solve1, testInput1, 2028)
    test(::solve1, testInput2, 10092)
    solve(::solve1) // 1526018
//
//    header(2)
//    test(::solve2, testInput2, 1337)
//    solve(::solve2)
}

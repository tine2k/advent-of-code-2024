fun main() {

    data class Guard(val position: Point, val direction: Direction)
    data class State(val guard: Guard, val visited: List<Guard>, val isLoop: Boolean? = null)

    val testInput = "....#.....\n" +
            ".........#\n" +
            "..........\n" +
            "..#.......\n" +
            ".......#..\n" +
            "..........\n" +
            ".#..^.....\n" +
            "........#.\n" +
            "#.........\n" +
            "......#..."

    fun findStart(lines: List<String>): Point {
        val y = lines.indexOfFirst { it.contains('^') }
        return Point(lines[y].indexOfFirst { it == '^' }, y)
    }

    fun getValue(lines: List<String>, point: Point): Char = lines[point.y][point.x]

    fun getNewPosition(direction: Direction, position: Point): Point =
        when (direction) {
            Direction.NORTH -> Point(position.x, position.y - 1)
            Direction.EAST -> Point(position.x + 1, position.y)
            Direction.SOUTH -> Point(position.x, position.y + 1)
            Direction.WEST -> Point(position.x - 1, position.y)
        }

    fun isOutside(lines: List<String>, position: Point): Boolean =
        position.x < 0 || position.y < 0 || position.y >= lines.size || position.x >= lines[0].length

    fun move(lines: List<String>, guard: Guard, turn: Boolean = false): Guard {
        val newPosStraightAhead = getNewPosition(guard.direction, guard.position)
        return if (turn || !isOutside(lines, newPosStraightAhead) && getValue(lines, newPosStraightAhead) == '#') {
            val newDirection = when (guard.direction) {
                Direction.NORTH -> Direction.EAST
                Direction.EAST -> Direction.SOUTH
                Direction.SOUTH -> Direction.WEST
                Direction.WEST -> Direction.NORTH
            }
            Guard(getNewPosition(newDirection, guard.position), newDirection)
        } else {
            Guard(newPosStraightAhead, guard.direction)
        }
    }

    fun moveTillEnd(lines: List<String>, state: State): Sequence<State> =
        generateSequence(state) { (guard, visited, isLoop) ->
            val newGuard = move(lines, guard)
            if (isOutside(lines, newGuard.position) || isLoop == true) {
                null
            } else {
                State(newGuard, (visited + newGuard), visited.contains(newGuard))
            }
        }

    fun solve1(lines: List<String>): Long {
        val guard = Guard(findStart(lines), Direction.NORTH)
        val initialState = State(guard, listOf(guard))
        return moveTillEnd(lines, initialState).last().visited.map { it.position }.distinct().size.toLong()
    }

    fun isLoop(lines: List<String>, state: State): Boolean = moveTillEnd(lines, state).last().isLoop == true

    fun isLoopAfterTurn(lines: List<String>, guard: Guard, visited: List<Guard>): Boolean {
        val turnedGuard = move(lines, guard, true)
        val isLoopAfterTurn = isLoop(lines, State(turnedGuard, visited + turnedGuard))
                && !isOutside(lines, turnedGuard.position)
        return isLoopAfterTurn
    }

    fun solve2(lines: List<String>): Long {
        val guard = Guard(findStart(lines), Direction.NORTH)
        val initialState = State(guard, listOf(guard))
        return generateSequence(initialState) { (guard, visited) ->
            val newGuard = move(lines, guard)
            if (isOutside(lines, newGuard.position)) {
                null
            } else {
                State(newGuard, (visited + newGuard), isLoopAfterTurn(lines, newGuard, (visited + newGuard)))
            }
        }.filter { it.isLoop == true }.distinctBy { it.guard.position }.toList().size.toLong()
    }

    header(1)
    test(::solve1, testInput, 41)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 6)
    solve(::solve2)
}

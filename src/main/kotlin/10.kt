import org.jgrapht.Graph
import org.jgrapht.alg.shortestpath.AllDirectedPaths
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import kotlin.math.abs


fun main() {
    val testInput1 = "0123\n" +
            "1234\n" +
            "8765\n" +
            "9876"

    val testInput2 = "...0...\n" +
            "...1...\n" +
            "...2...\n" +
            "6543456\n" +
            "7.....7\n" +
            "8.....8\n" +
            "9.....9"

    val testInput3 = "..90..9\n" +
            "...1.98\n" +
            "...2..7\n" +
            "6543456\n" +
            "765.987\n" +
            "876....\n" +
            "987...."

    val testInput4 = "10..9..\n" +
            "2...8..\n" +
            "3...7..\n" +
            "4567654\n" +
            "...8..3\n" +
            "...9..2\n" +
            ".....01"

    val testInput5 = "89010123\n" +
            "78121874\n" +
            "87430965\n" +
            "96549874\n" +
            "45678903\n" +
            "32019012\n" +
            "01329801\n" +
            "10456732"

    val testInput6 = ".....0.\n" +
            "..4321.\n" +
            "..5..2.\n" +
            "..6543.\n" +
            "..7..4.\n" +
            "..8765.\n" +
            "..9...."

    val testInput7 = "..90..9\n" +
            "...1.98\n" +
            "...2..7\n" +
            "6543456\n" +
            "765.987\n" +
            "876....\n" +
            "987...."

    val testInput8 = "012345\n" +
            "123456\n" +
            "234567\n" +
            "345678\n" +
            "4.6789\n" +
            "56789."

    data class Peak(val point: Point, val height: Int)

    fun isNextTo(p1: Point, p2: Point): Boolean =
        (abs(p1.x - p2.x) == 1 && p1.y == p2.y) ||
                (p1.x == p2.x && abs(p1.y - p2.y) == 1)

    fun createGraph(lines: List<String>): Graph<Peak, DefaultEdge> {
        val graph = DefaultDirectedGraph<Peak, DefaultEdge>(DefaultEdge::class.java)
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                if (char != '.') {
                    graph.addVertex(Peak(Point(x, y), char.toString().toInt()))
                }
            }
        }
        graph.vertexSet().forEach { peak ->
            graph.vertexSet()
                .filter { peak.height == it.height - 1 && isNextTo(peak.point, it.point) }
                .forEach { graph.addEdge(peak, it) }
        }
        return graph
    }

    fun solve(graph: Graph<Peak, DefaultEdge>, evaluateFn: (Peak, Peak) -> Int): Long {
        val heads = graph.vertexSet().filter { it.height == 0 }
        val ends = graph.vertexSet().filter { it.height == 9 }
        return heads.sumOf { head -> ends.sumOf { end -> evaluateFn(head, end) } }.toLong()
    }

    fun solve1(lines: List<String>): Long {
        val graph = createGraph(lines)
        val dijkstra = DijkstraShortestPath(graph)
        return solve(graph) { head, end -> dijkstra.getPath(head, end)?.let { 1 } ?: 0 }
    }

    fun solve2(lines: List<String>): Long {
        val graph = createGraph(lines)
        val allDirectedPaths = AllDirectedPaths(graph)
        return solve(graph) { head, end -> allDirectedPaths.getAllPaths(head, end, true, null).size }
    }

    header(1)
    test(::solve1, testInput1, 1)
    test(::solve1, testInput2, 2)
    test(::solve1, testInput3, 4)
    test(::solve1, testInput4, 3)
    test(::solve1, testInput5, 36)
    solve(::solve1)

    header(2)
    test(::solve2, testInput6, 3)
    test(::solve2, testInput7, 13)
    test(::solve2, testInput8, 227)
    test(::solve2, testInput5, 81)
    solve(::solve2)
}

fun main() {
    val testInput = "47|53\n" +
            "97|13\n" +
            "97|61\n" +
            "97|47\n" +
            "75|29\n" +
            "61|13\n" +
            "75|53\n" +
            "29|13\n" +
            "97|29\n" +
            "53|29\n" +
            "61|53\n" +
            "97|53\n" +
            "61|29\n" +
            "47|13\n" +
            "75|47\n" +
            "97|75\n" +
            "47|61\n" +
            "75|61\n" +
            "47|29\n" +
            "75|13\n" +
            "53|13\n" +
            "\n" +
            "75,47,61,53,29\n" +
            "97,61,53,29,13\n" +
            "75,29,13\n" +
            "75,97,47,61,53\n" +
            "61,13,29\n" +
            "97,13,75,29,47"

    fun isRuleBroken(rule: Pair<Int, Int>, prints: List<Int>): Boolean {
        val indexOf1 = prints.indexOfFirst { it == rule.first }
        val indexOf2 = prints.indexOfFirst { it == rule.second }
        return indexOf1 != -1 && indexOf2 != -1 && indexOf1 > indexOf2
    }

    fun isValid(rules: List<Pair<Int, Int>>, prints: List<Int>): Boolean = !rules.any { isRuleBroken(it, prints) }

    fun getData(lines: List<String>): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
        val split = lines.indexOfFirst { it.isEmpty() }
        val rules = lines.subList(0, split).map { it.split("|") }.map { it[0].toInt() to it[1].toInt() }
        val prints = lines.subList(split + 1, lines.size).map { it.split(",").map { v -> v.toInt() } }
        return Pair(rules, prints)
    }

    fun solve1(lines: List<String>): Long {
        val (rules, prints) = getData(lines)
        return prints.filter { isValid(rules, it) }
            .sumOf { it[it.size / 2] }
            .toLong()
    }

    fun compare(rules: List<Pair<Int, Int>>, a: Int, b: Int): Int =
        if (isValid(rules, listOf(a, b))) {
            1
        } else {
            -1
        }

    fun reorder(rules: List<Pair<Int, Int>>, prints: List<Int>): List<Int> =
        prints.sortedWith { a, b -> compare(rules, a, b) }

    fun solve2(lines: List<String>): Long {
        val (rules, prints) = getData(lines)
        return prints.filter { !isValid(rules, it) }
            .map { reorder(rules, it) }
            .sumOf { it[it.size / 2] }
            .toLong()
    }

    header(1)
    test(::solve1, testInput, 143)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 123)
    solve(::solve2)
}

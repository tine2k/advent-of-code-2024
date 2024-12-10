fun main() {
    val testInput1 = "12345"
    val testInput2 = "2333133121414131402"

    data class File(val id: Int, val length: Int, val freeSpace: Int, val moved: Boolean = false)

    data class Value(val file: File, val whitespaceCount: Int?)

    fun getFiles(lines: List<String>): List<File> =
        lines[0]
            .map { it.toString().toInt() }
            .chunked(2)
            .mapIndexed { index, chars -> File(index, chars[0], chars.getOrNull(1) ?: 0) }

    fun getWhitespaceTillPosition(pos: Int, files: List<File>): Value {
        var whitespace = 0
        var curPos = 0
        var counter = 0
        while (curPos <= pos) {
            val file = files[counter++]
            if (curPos + file.length > pos) {
                return Value(file, null)
            } else {
                curPos += file.length
            }

            if (curPos + file.freeSpace > pos) {
                return Value(file, whitespace + (pos - curPos))
            } else {
                curPos += file.freeSpace
                whitespace += file.freeSpace
            }
        }
        error("too far")
    }

    fun getValue(pos: Int, files: List<File>): Int {
        files.reversed().fold(0) { index, file ->
            if (index + file.length <= pos) {
                index + file.length
            } else {
                return file.id
            }
        }
        error("too far")
    }

    fun getValueAtPosition(index: Int, files: List<File>): Int {
        val value = getWhitespaceTillPosition(index, files)
        return if (value.whitespaceCount != null) {
            getValue(value.whitespaceCount, files)
        } else {
            value.file.id
        }
    }

    fun solve1(lines: List<String>): Long {
        val files = getFiles(lines)
        val totalData = files.sumOf { it.length }
        return (0..<totalData).sumOf { index -> (index * getValueAtPosition(index, files)).toLong() }
    }

    fun getValueOfFile(position: Int, file: File): Pair<Int, Long> {
        val totalOfFile = (position..<(position+ file.length)).sumOf { it * file.id.toLong() }
        val nextPosition = position + file.length + file.freeSpace
        return nextPosition to totalOfFile
    }

    fun solve2(lines: List<String>): Long {
        val files = getFiles(lines)
        val result = files.toMutableList()
        files.map { it.id }.reversed().forEach { file2MoveId ->
            val file2Move = result.first { it.id == file2MoveId && !it.moved }
            val file2MoveIndex = result.indexOf(file2Move)
            val indexOfGap = result.indexOfFirst { file -> file.freeSpace >= file2Move.length }
            if (indexOfGap != -1 && file2MoveIndex > indexOfGap) {
                val fileAtIndex = result[indexOfGap]
                result[indexOfGap] = File(fileAtIndex.id, fileAtIndex.length, 0)
                result.add(
                    indexOfGap + 1,
                    File(file2Move.id, file2Move.length, fileAtIndex.freeSpace - file2Move.length, true)
                )

                result[result.lastIndexOf(file2Move)] = File(file2Move.id, 0, file2Move.freeSpace + file2Move.length)
            }
        }

        return result.fold(0 to 0L) { acc, file ->
            val (nextPosition, totalOfFile) = getValueOfFile(acc.first, file)
            nextPosition to acc.second + totalOfFile
        }.second
    }

    header(1)
    test(::solve1, testInput1, 60)
    test(::solve1, testInput2, 1928)
    solve(::solve1)

    header(2)
    test(::solve2, testInput2, 2858)
    solve(::solve2)
}

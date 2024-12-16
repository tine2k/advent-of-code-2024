import org.apache.commons.io.IOUtils
import java.io.File
import java.io.InputStream
import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.measureTimedValue

val day = Thread.currentThread().stackTrace[2].fileName!!.split(".")[0]

fun header(part: Int) {
    println("--- Day $day Part $part ---");
}

fun test(solveFn: (List<String>) -> Long, testInput: String, testResult: Int) {
    return test(solveFn, testInput, testResult.toLong())
}

fun test(solveFn: (List<String>) -> Long, testInput: String, testResult: Long) {
    return testString({ solveFn.invoke(it).toString() }, testInput, testResult.toString())
}

fun testString(solveFn: (List<String>) -> String, testInput: String, testResult: String) {
    val result = solveFn(IOUtils.readLines(StringReader(testInput)))
    if (result != testResult) {
        println("Test ❌: Result should be $testResult but was $result")
    } else {
        println("Test ✅")
    }
}

fun solve(solveFn: (List<String>) -> Long, input: List<String> = getInputFile()) {
    solveString({ solveFn.invoke(it).toString() }, input)
}

fun solveString(solveFn: (List<String>) -> String, input: List<String> = getInputFile()) {
    val (solution, timeTaken) = measureTimedValue {
        solveFn(input)
    }
    println()
    println("➡️ $solution ⬅️")
    println()
    println("⏳ $timeTaken ⏳")
    println()
}

fun getInputFile(): List<String> =
    IOUtils.readLines(readInputFile(day) ?: fetchInputFile(day), "UTF-8")

fun fetchInputFile(day: String): InputStream {
    val sessionCookie = File("aoc-session-cookie.txt").readText().trim()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://adventofcode.com/2024/day/$day/input"))
        .header("Cookie", "session=$sessionCookie")
        .build()
    val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream())
    check(response.statusCode() == 200) { "Invalid status code: ${response.statusCode()}" }
    val outputpath = "src/main/resources/$day.txt"
    Files.copy(response.body(), Path.of(outputpath))
    println("🔢 Input data file fetched to $outputpath 🔢")
    return File(outputpath).inputStream()
}

fun readInputFile(day: String): InputStream? = IOUtils::class.java.getResourceAsStream("/$day.txt")

fun <T> printProgress(i: Int, lines: List<T>) {
    printProgress(i, lines.size)
}

fun printProgress(i: Int, total: Int) {
    print("calculating ${i + 1} of ${total}...\r")
}

fun printProgress(i: Int) {
    print("calculating ${i + 1}...\r")
}

enum class Direction {
    NORTH, SOUTH, WEST, EAST
}

data class Point(val x: Int, val y: Int) {
    operator fun plus(p2: Point): Point = Point(this.x + p2.x, this.y + p2.y)
}

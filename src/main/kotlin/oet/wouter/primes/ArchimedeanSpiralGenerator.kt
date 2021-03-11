package oet.wouter.primes

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.stream.IntStream
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.streams.toList

private val DECIMAL_FORMAT = DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

private const val DIMENSION = 2000
private const val X_OFFSET = DIMENSION / 2
private const val Y_OFFSET = DIMENSION / 2

class ArchimedeanSpiralGenerator(
	private val numberOfPrimes: Int = 20,
	private val dotSize: String = "2.0",
	private val radiusBase: Double = 0.01
) {

	data class Point(val x: Int, val y: Int)
	data class RangePoint(val prime: Int, val rounds: Int, val rangeStart: Int, val rangeEnd: Int)

	fun generate(): String {
		val ranges = IntStream.iterate(1) { it + 1 }
			.map { i -> i * i }
			.takeWhile { it < 1299709 }
			.toList() + listOf(Integer.MAX_VALUE)

		val primes = Primes.get().subList(0, numberOfPrimes)

		val points = zipLists2(primes, ranges)
			.map { toDegrees(it) }
			.map { toPoint(it) }
			.joinToString("\n") { toSvgPoint(it, toHue(it)) }
		return listOf(
			header(),
			grid(),
			points,
			footer()
		).joinToString("\n")
	}

	private fun grid(): String {
		return """
			<line x1="0" y1="$Y_OFFSET" x2="$DIMENSION" y2="$Y_OFFSET" style="stroke:rgb(255,0,0);stroke-width:2" />
			<line x1="$X_OFFSET" y1="$0" x2="$X_OFFSET" y2="$DIMENSION" style="stroke:rgb(255,0,0);stroke-width:2" />

			""".trimIndent()
	}

	private fun zipLists2(primes: List<Int>, ranges: List<Int>): List<RangePoint> {
		tailrec fun match(
			primes: List<Int>,
			ranges: List<Int>,
			rounds: Int,
			rangeStart: Int,
			results: List<RangePoint>
		): List<RangePoint> {
			if (primes.isEmpty()) return results

			val prime = primes.first()
			val rangeEnd = ranges.first()
			return if (prime <= rangeEnd) {
				match(
					primes.subList(1, primes.size - 1),
					ranges,
					rounds,
					rangeStart,
					results + listOf(RangePoint(prime, rounds, rangeStart, rangeEnd))
				)
			} else {
				match(primes, ranges.subList(1, ranges.size - 1), rounds + 1, rangeEnd + 1, results)
			}
		}

		return match(primes, ranges, -1, 0, listOf())
	}

	private fun header() =
		"""<svg version="1.1" baseProfile="full" width="$DIMENSION" height="$DIMENSION" xmlns="http://www.w3.org/2000/svg">
			<rect width="$DIMENSION" height="$DIMENSION" style="fill:hsl(0,100%,0%);" />"""

	private fun footer() = "</svg>\n"

	private fun toDegrees(rangePoint: RangePoint): Double {

		val rangeStart = rangePoint.rangeStart
		val rangeEnd = rangePoint.rangeEnd

		val degreesPerStep: Double = 360.0 / (rangeEnd - rangeStart)
		val stepOffset = rangePoint.prime - rangeStart

		return stepOffset * degreesPerStep + 360 * rangePoint.rounds
	}

	private fun toHue(point: Point): Double =
		(sqrt(point.x * point.x + point.y * point.y * 1.0) / (DIMENSION / 2) * 360 + 100) % 360

	private fun toPoint(degrees: Double): Point {
		val a = 0
		val c = 1.0
		val r: Double = a + radiusBase * degrees.pow(1 / c)
		val x: Int = (r * cos(degrees * (Math.PI / 180))).roundToInt()
		val y: Int = (r * sin(degrees * (Math.PI / 180))).roundToInt()

		return Point(x, y)
	}

	private fun toSvgPoint(p: Point, hue: Double): String {
		val xx = DECIMAL_FORMAT.format(p.x + X_OFFSET)
		val yy = DECIMAL_FORMAT.format(p.y + Y_OFFSET)
		val huehue = DECIMAL_FORMAT.format(hue)

		return """<circle cx="$xx" cy="$yy" r="$dotSize" style="fill:hsl($huehue, 75%, 50%)" />"""
	}
}

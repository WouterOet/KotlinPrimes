package oet.wouter.primes

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private val DECIMAL_FORMAT = DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

private const val DIMENSION = 2000
private const val X_OFFSET = DIMENSION / 2
private const val Y_OFFSET = DIMENSION / 2

class SpiralGenerator(
	private val numberOfPrimes: Int = 19999,
	private val dotSize: String = "2.0",
	private val radiusBase: Double = 2.0
) {

	fun generate(): String {
		val primes = Primes.get()
		var previousSquared = 1
		var current = 2
		var currentSquared = 4

		val points = mutableListOf(header())
		for (i in 0..numberOfPrimes) {
			val prime = primes[i]
			if (prime > currentSquared) {
				previousSquared = currentSquared
				current++
				currentSquared = current * current
			}

			points.add(toSvgPoint(previousSquared, current, currentSquared, prime))
		}
		points.add(footer())
		return points.joinToString("\n")
	}

	private fun toSvgPoint(previousSquared: Int, current: Int, currentSquared: Int, prime: Int): String {
		val range = currentSquared - previousSquared
		val value = prime - previousSquared
		val angle = (0.0 + value) / range
		val radius = radiusBase * (current - 1) + angle * radiusBase
		val x = radius * cos(angle * 2 * Math.PI)
		val y = radius * sin(angle * 2 * Math.PI) * -1

		val xx = DECIMAL_FORMAT.format(x + X_OFFSET)
		val yy = DECIMAL_FORMAT.format(y + Y_OFFSET)

//        int hue = (int) (2.0 / 3 * Math.sqrt(prime) + 100);
		val hue = ((sqrt(x * x + y * y) / (DIMENSION / 2) * 360).toInt() + 100) % 360
		return """<circle cx="$xx" cy="$yy" r="$dotSize" style="fill:hsl($hue, 75%, 50%)" />"""
	}

	private fun header() =
		"""<svg version="1.1" baseProfile="full" width="$DIMENSION" height="$DIMENSION" xmlns="http://www.w3.org/2000/svg">
			<rect width="$DIMENSION" height="$DIMENSION" style="fill:hsl(0,100%,0%);" />"""

	private fun footer() = "</svg>\n"
}

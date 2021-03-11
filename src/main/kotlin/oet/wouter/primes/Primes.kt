package oet.wouter.primes

import java.nio.file.Files
import java.nio.file.Paths

object Primes {
    fun get(): List<Int> {
		val primes = String(Files.readAllBytes(Paths.get("src/main/resources/primes.txt")))
		return primes.split(",").toTypedArray().map { it.toInt() }
    }
}

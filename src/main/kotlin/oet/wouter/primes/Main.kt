package oet.wouter.primes

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.IntStream

object Main {

	@JvmStatic
	fun main(args: Array<String>) {
		val spiral = ArchimedeanSpiralGenerator().generate()
//		val spiral = SpiralGenerator().generate()

		Files.write(Paths.get("./primes_arch2.0.svg"), listOf(spiral))
	}


}

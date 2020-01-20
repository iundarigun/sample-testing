package br.com.devcave.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleTestingApplication

fun main(args: Array<String>) {
	runApplication<SampleTestingApplication>(*args)
}

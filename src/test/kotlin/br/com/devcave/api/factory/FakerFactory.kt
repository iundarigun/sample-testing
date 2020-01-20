package br.com.devcave.api.factory

import com.github.javafaker.Faker
import org.slf4j.LoggerFactory
import java.util.Random

object FakerFactory {
    val faker = Faker(
        Random(
            System.currentTimeMillis().also {
                LoggerFactory.getLogger(javaClass).info("faker seed $it")
            }
        )
    )
}
package br.com.devcave.api.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@Tag("unit")
class DockerContainerTest {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        @Container
        val mock: GenericContainer<Nothing> = GenericContainer<Nothing>("iundarigun/mock-ws").also {
            it.withExposedPorts(1899)
            it.waitingFor(Wait.forHttp("/").forStatusCode(200))
        }
    }

    @Test
    fun `validate container UP`() {
        log.info("validating container UP")
        val restTemplate = RestTemplate()
        val forEntity = restTemplate.getForEntity(
            "http://localhost:${mock.firstMappedPort}/health",
            Any::class.java
        )
        Assertions.assertNotNull(forEntity)
        Assertions.assertEquals(200, forEntity.statusCodeValue)
    }
}
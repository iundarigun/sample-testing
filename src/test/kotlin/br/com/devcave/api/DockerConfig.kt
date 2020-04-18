package br.com.devcave.api

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.MountableFile

@Configuration
@Profile("docker")
class DockerConfig {
    companion object {
        val mock: GenericContainer<Nothing> = GenericContainer<Nothing>("postgres").also {
            it.withExposedPorts(5432)
            it.portBindings.add("55432:5432")
            it.addEnv("POSTGRES_USER", "test")
            it.addEnv("POSTGRES_PASSWORD", "test")
//            it.withClasspathResourceMapping(
//                "database/schema.sql", "/docker-entrypoint-initdb.d/10-schema.sql",
//                BindMode.READ_ONLY
//            )
//            it.withClasspathResourceMapping(
//                "database/data.sql", "/docker-entrypoint-initdb.d/20-data.sql",
//                BindMode.READ_ONLY
//            )
            it.withCopyFileToContainer(
                MountableFile.forClasspathResource(
                    "database/schema.sql"
                ),
                "/docker-entrypoint-initdb.d/10-schema.sql"
            )
            it.withCopyFileToContainer(
                MountableFile.forClasspathResource(
                    "database/data.sql"
                ),
                "/docker-entrypoint-initdb.d/20-data.sql"
            )
            it.waitingFor(Wait.forListeningPort())
            it.start()
        }
    }
}
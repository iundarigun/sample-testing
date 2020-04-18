package br.com.devcave.api.factory

import br.com.devcave.api.domain.entity.Sector
import com.github.javafaker.Faker

object SectorFactory {
    private val faker = Faker.instance()
    private val sector = listOf("IT", "INFRA", "SEC", "SUP", "ACC")

    fun builderSector(): Sector {
        val index = faker.number().numberBetween(0, sector.size - 1)
        return Sector(
            index + 1L,
            sector[index],
            sector[index]
        )
    }

    fun getAllSector(): List<Sector> {
        return (sector.indices).map {
            Sector(it + 1L, sector[it], sector[it])
        }
    }
}
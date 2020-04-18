package br.com.devcave.api.repository

import br.com.devcave.api.domain.entity.Sector
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SectorRepository : JpaRepository<Sector, Long> {
    fun findByCode(code: String): Optional<Sector>
}
package br.com.devcave.api.domain

import java.time.LocalDate
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class EmployeeRequest(
    @field:NotBlank
    var name: String = "",
    @field:NotBlank
    var document: String = "",
    var collageCompletedYear: Int? = 0,
    @field:NotBlank
    var sector: String = "",
    @field:NotNull
    var bornAt: LocalDate = LocalDate.now()
)
package br.com.devcave.api.domain

import java.time.LocalDate

data class EmployeeResponse(
    val id: Long,
    val name: String,
    val document: String,
    val collageCompletedYear: Int?,
    val bornAt: LocalDate = LocalDate.now()
)
package br.com.devcave.api.repository

import br.com.devcave.api.domain.entity.Employee
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository : JpaRepository<Employee, Long> {
    fun existsByDocument(document: String): Boolean
    fun existsByDocumentAndIdNot(document: String, id: Long): Boolean
}
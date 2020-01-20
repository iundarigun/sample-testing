package br.com.devcave.api.service

import br.com.devcave.api.domain.entity.Employee
import br.com.devcave.api.domain.EmployeeRequest
import br.com.devcave.api.domain.EmployeeResponse
import br.com.devcave.api.exception.EmployeeAlreadyExistsException
import br.com.devcave.api.exception.EmployeeNotFoundException
import br.com.devcave.api.repository.EmployeeRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository
) {

    fun deleteEmployee(id: Long) {
        if (!employeeRepository.existsById(id)) {
            throw EmployeeNotFoundException("employee id $id not found")
        }
        employeeRepository.deleteById(id)
    }

    fun findAll(page: Int, size: Int): List<EmployeeResponse> {
        val pageRequest = PageRequest.of(page - 1, size, Sort.by("name"))

        return employeeRepository
            .findAll(pageRequest)
            .content.map {
            EmployeeResponse(
                it.id,
                it.name,
                it.document,
                it.collageCompletedYear,
                it.bornAt
            )
        }
    }

    fun createEmployee(request: EmployeeRequest): Long {
        if (employeeRepository.existsByDocument(request.document)) {
            throw EmployeeAlreadyExistsException("Employee document ${request.document} already exists")
        }
        val employee = Employee(
            name = request.name,
            document = request.document,
            collageCompletedYear = request.collageCompletedYear,
            bornAt = request.bornAt
        )
        return employeeRepository.save(employee).id
    }

    fun updateEmployee(id: Long, request: EmployeeRequest) {
        if (!employeeRepository.existsById(id)) {
            throw EmployeeNotFoundException("employee id $id not found")
        }

        if (employeeRepository.existsByDocumentAndIdNot(request.document, id)) {
            throw EmployeeAlreadyExistsException("Employee document ${request.document} already exists")
        }
        employeeRepository.findById(id).ifPresent {
            val employee = it.copy(
                name = request.name,
                document = request.document,
                collageCompletedYear = request.collageCompletedYear,
                bornAt = request.bornAt
            )
            employeeRepository.save(employee)
        }
    }

    fun findById(id: Long): EmployeeResponse {
        return employeeRepository.findById(id).orElseThrow {
            EmployeeNotFoundException("employee id $id not found")
        }
        .let {
                EmployeeResponse(
                    it.id,
                    it.name,
                    it.document,
                    it.collageCompletedYear,
                    it.bornAt
                )
            }
    }
}
package br.com.devcave.api.service

import br.com.devcave.api.client.FraudServiceClient
import br.com.devcave.api.domain.entity.Employee
import br.com.devcave.api.domain.EmployeeRequest
import br.com.devcave.api.domain.EmployeeResponse
import br.com.devcave.api.domain.entity.Sector
import br.com.devcave.api.exception.EmployeeAlreadyExistsException
import br.com.devcave.api.exception.EntityNotFoundException
import br.com.devcave.api.exception.EmployeeWithFraudSuspectException
import br.com.devcave.api.repository.EmployeeRepository
import br.com.devcave.api.repository.SectorRepository
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val sectorRepository: SectorRepository,
    private val fraudServiceClient: FraudServiceClient
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun deleteEmployee(id: Long) {
        if (!employeeRepository.existsById(id)) {
            throw EntityNotFoundException("employee id $id not found")
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
        validateFraud(request.document)

        val employee = Employee(
            name = request.name,
            document = request.document,
            collageCompletedYear = request.collageCompletedYear,
            bornAt = request.bornAt,
            sector = getSectorByCode(request.sector)
        )
        return employeeRepository.save(employee).id
    }

    fun updateEmployee(id: Long, request: EmployeeRequest) {
        if (!employeeRepository.existsById(id)) {
            throw EntityNotFoundException("employee id $id not found")
        }

        if (employeeRepository.existsByDocumentAndIdNot(request.document, id)) {
            throw EmployeeAlreadyExistsException("Employee document ${request.document} already exists")
        }
        validateFraud(request.document)

        employeeRepository.findById(id).ifPresent {
            val employee = it.copy(
                name = request.name,
                document = request.document,
                collageCompletedYear = request.collageCompletedYear,
                bornAt = request.bornAt,
                sector = getSectorByCode(request.sector)
            )
            employeeRepository.save(employee)
        }
    }

    fun findById(id: Long): EmployeeResponse {
        return employeeRepository.findById(id).orElseThrow {
            EntityNotFoundException("employee id $id not found")
        }.let {
            EmployeeResponse(
                it.id,
                it.name,
                it.document,
                it.collageCompletedYear,
                it.bornAt
            )
        }
    }

    private fun getSectorByCode(code: String): Sector {
        return sectorRepository.findByCode(code).orElseThrow {
            EntityNotFoundException("sector with code $code not found")
        }
    }

    private fun validateFraud(document: String) {
        try {
            val fraudResponse = fraudServiceClient.validateFraud(document)
            if (fraudResponse.fraud) {
                throw EmployeeWithFraudSuspectException(
                    "Document $document is fraud suspect. " +
                            "Reason: ${fraudResponse.reason}"
                )
            }
        } catch (e: FeignException) {
            log.warn("Fraud service is unavailable")
        }
    }
}
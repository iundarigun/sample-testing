package br.com.devcave.api.service

import br.com.devcave.api.domain.entity.Employee
import br.com.devcave.api.exception.EmployeeAlreadyExistsException
import br.com.devcave.api.exception.EmployeeNotFoundException
import br.com.devcave.api.factory.EmployeeFactory
import br.com.devcave.api.factory.FakerFactory
import br.com.devcave.api.repository.EmployeeRepository
import com.github.javafaker.Faker
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.Optional

@Tag("unit")
class EmployeeServiceTest {

    private val faker = FakerFactory.faker

    private val employeeRepository = mockk<EmployeeRepository>()

    private val employeeService = EmployeeService(employeeRepository)

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `delete employee with success`() {
        every {
            employeeRepository.existsById(any())
        } returns true
        every {
            employeeRepository.deleteById(any())
        } just runs

        employeeService.deleteEmployee(faker.number().numberBetween(1L, 10_000L))

        verify(exactly = 1) {
            employeeRepository.existsById(any())
            employeeRepository.deleteById(any())
        }
    }

    @Test
    fun `trying delete nonexistent employee`() {
        every {
            employeeRepository.existsById(any())
        } returns false
        every {
            employeeRepository.deleteById(any())
        } just runs

        var exception: Exception? = null
        try {
            employeeService.deleteEmployee(faker.number().numberBetween(1L, 10_000L))
        } catch (e: Exception) {
            exception = e
        }

        Assertions.assertNotNull(exception)
        Assertions.assertTrue(exception is EmployeeNotFoundException)

        verify(exactly = 1) {
            employeeRepository.existsById(any())
        }
        verify(exactly = 0) {
            employeeRepository.deleteById(any())
        }
    }

    @Test
    fun `save new employee with success`() {
        val request = EmployeeFactory.builderRequest()
        val employeeToReturn = mockk<Employee>()
        val idToReturn = faker.number().numberBetween(1L, 10_000L)

        every {
            employeeRepository.existsByDocument(any())
        } returns false
        every {
            employeeRepository.save(any<Employee>())
        } returns employeeToReturn
        every {
            employeeToReturn.id
        } returns idToReturn

        val result = employeeService.createEmployee(request)

        Assertions.assertEquals(idToReturn, result)

        val employeeToSave = mutableListOf<Employee>()
        verify(exactly = 1) {
            employeeRepository.existsByDocument(any())
            employeeRepository.save(capture(employeeToSave))
        }

        Assertions.assertFalse(employeeToSave.isEmpty())
        Assertions.assertEquals(1, employeeToSave.size)
        employeeToSave[0].let {
            assertAll(
                { Assertions.assertEquals(request.name, it.name) },
                { Assertions.assertEquals(request.document, it.document) },
                { Assertions.assertEquals(request.collageCompletedYear, it.collageCompletedYear) },
                { Assertions.assertEquals(request.bornAt, it.bornAt) }
            )
        }
    }

    @Test
    fun `trying save new employee with existent document`() {
        val request = EmployeeFactory.builderRequest()

        every {
            employeeRepository.existsByDocument(any())
        } returns true

        var exception: Exception? = null
        try {
            employeeService.createEmployee(request)
        } catch (e: Exception) {
            exception = e
        }

        Assertions.assertNotNull(exception)
        Assertions.assertTrue(exception is EmployeeAlreadyExistsException)

        verify(exactly = 1) {
            employeeRepository.existsByDocument(any())
        }
        verify(exactly = 0) {
            employeeRepository.save(any<Employee>())
        }
    }

    @Test
    fun `update employee with success`() {
        val userId = faker.number().numberBetween(1L, 10_000L)
        val request = EmployeeFactory.builderRequest()
        val employee = EmployeeFactory.builderEmployee(userId)

        every {
            employeeRepository.existsById(userId)
        } returns true
        every {
            employeeRepository.existsByDocumentAndIdNot(request.document, userId)
        } returns false
        every {
            employeeRepository.findById(userId)
        } returns Optional.of(employee)
        every {
            employeeRepository.save(any<Employee>())
        } returns mockk()

        employeeService.updateEmployee(userId, request)

        val employeeToSave = mutableListOf<Employee>()
        verify(exactly = 1) {
            employeeRepository.existsById(any())
            employeeRepository.existsByDocumentAndIdNot(any(), any())
            employeeRepository.findById(any())
            employeeRepository.save(capture(employeeToSave))
        }

        Assertions.assertFalse(employeeToSave.isEmpty())
        Assertions.assertEquals(1, employeeToSave.size)
        employeeToSave[0].let {
            assertAll(
                { Assertions.assertEquals(request.name, it.name) },
                { Assertions.assertEquals(request.document, it.document) },
                { Assertions.assertEquals(request.collageCompletedYear, it.collageCompletedYear) },
                { Assertions.assertEquals(request.bornAt, it.bornAt) }
            )
        }
    }

    @Test
    fun `trying update nonexistent employee`() {
        val userId = faker.number().numberBetween(1L, 10_000L)
        val request = EmployeeFactory.builderRequest()

        every {
            employeeRepository.existsById(userId)
        } returns false

        var exception: Exception? = null
        try {
            employeeService.updateEmployee(userId, request)
        } catch (e: Exception) {
            exception = e
        }

        Assertions.assertNotNull(exception)
        Assertions.assertTrue(exception is EmployeeNotFoundException)

        verify(exactly = 1) {
            employeeRepository.existsById(any())
        }
        verify(exactly = 0) {
            employeeRepository.existsByDocumentAndIdNot(any(), any())
            employeeRepository.findById(any())
            employeeRepository.save(any<Employee>())
        }
    }

    @Test
    fun `trying update employee with existent document`() {
        val userId = faker.number().numberBetween(1L, 10_000L)
        val request = EmployeeFactory.builderRequest()

        every {
            employeeRepository.existsById(userId)
        } returns true
        every {
            employeeRepository.existsByDocumentAndIdNot(request.document, userId)
        } returns true

        var exception: Exception? = null
        try {
            employeeService.updateEmployee(userId, request)
        } catch (e: Exception) {
            exception = e
        }

        Assertions.assertNotNull(exception)
        Assertions.assertTrue(exception is EmployeeAlreadyExistsException)

        verify(exactly = 1) {
            employeeRepository.existsById(any())
            employeeRepository.existsByDocumentAndIdNot(any(), any())
        }
        verify(exactly = 0) {
            employeeRepository.findById(any())
            employeeRepository.save(any<Employee>())
        }
    }
}
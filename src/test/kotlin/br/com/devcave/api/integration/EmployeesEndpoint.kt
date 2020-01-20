package br.com.devcave.api.integration

import br.com.devcave.api.domain.EmployeeResponse
import br.com.devcave.api.domain.entity.Employee
import br.com.devcave.api.factory.EmployeeFactory
import br.com.devcave.api.factory.FakerFactory
import br.com.devcave.api.repository.EmployeeRepository
import com.github.javafaker.Faker
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeesEndpoint {

    @LocalServerPort
    private val port: Int? = null

    private val faker = FakerFactory.faker

	@Autowired
	private lateinit var employeeRepository: EmployeeRepository

    @BeforeEach
    fun before() {
        RestAssured.port = port ?: 8080
    }

    @AfterEach
    fun after() {
        employeeRepository.deleteAll()
    }

    @Test
    fun `findAll with success`() {
		createEmployees(10)

		RestAssured
			.given()
			.param("page", "1")
			.param("size", "20")
			.`when`()
			.get("/employees")
			.then()
			.body("$", Matchers.hasSize<Int>(10))
			.statusCode(HttpStatus.OK.value())
    }

    @Test
    fun `findAll wrong size`() {
        RestAssured
            .given()
            .param("page", "0")
            .param("size", "20")
            .`when`()
            .get("/employees")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `findById with success`() {
        val employee = createEmployees()[0]

        val result = RestAssured
            .given()
            .pathParam("id", employee.id)
            .`when`()
            .get("/employees/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getObject("", EmployeeResponse::class.java)

        Assertions.assertNotNull(result)
        assertAll(
            { Assertions.assertEquals(employee.id, result.id) },
            { Assertions.assertEquals(employee.name, result.name) },
            { Assertions.assertEquals(employee.document, result.document) },
            { Assertions.assertEquals(employee.collageCompletedYear, result.collageCompletedYear) },
            { Assertions.assertEquals(employee.bornAt, result.bornAt) }
        )
    }

    @Test
    fun `findById nonexistent employee`() {
        RestAssured
            .given()
            .pathParam("id", faker.number().randomNumber())
            .`when`()
            .get("/employees/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `Delete by id with success`() {
        val employee = createEmployees()[0]

        RestAssured
            .given()
            .pathParam("id", employee.id)
            .`when`()
            .delete("/employees/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
    }

    @Test
    fun `Delete by id nonexistent employee`() {
        RestAssured
            .given()
            .pathParam("id", faker.number().randomNumber())
            .`when`()
            .delete("/employees/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `create employee with success`() {
        val request = EmployeeFactory.builderRequest()

        val total = employeeRepository.count()

        val location = RestAssured
            .given()
            .body(request)
            .contentType(ContentType.JSON)
            .`when`()
            .post("/employees")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header("location")

        Assertions.assertEquals(total + 1, employeeRepository.count())

        val employee = employeeRepository.findById(location.split("/").last().toLong()).get()

        assertAll(
            { Assertions.assertEquals(request.name, employee.name) },
            { Assertions.assertEquals(request.document, employee.document) },
            { Assertions.assertEquals(request.collageCompletedYear, employee.collageCompletedYear) },
            { Assertions.assertEquals(request.bornAt, employee.bornAt) }
        )
    }

    @Test
    fun `Trying create employee with empty name`() {
        val request = EmployeeFactory.builderRequest().copy(name = "")

        RestAssured
            .given()
            .body(request)
            .contentType(ContentType.JSON)
            .`when`()
            .post("/employees")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    private fun createEmployees(total: Int = 1): List<Employee> {
        return (1..total).map { _ ->
			employeeRepository.save(EmployeeFactory.builderEmployee(0))
        }
    }
}

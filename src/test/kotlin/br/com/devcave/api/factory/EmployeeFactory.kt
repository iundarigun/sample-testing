package br.com.devcave.api.factory

import br.com.devcave.api.domain.EmployeeRequest
import br.com.devcave.api.domain.entity.Employee
import com.github.javafaker.Faker
import java.time.ZoneId

object EmployeeFactory {
    private val faker = Faker.instance()

    fun builderRequest(): EmployeeRequest {
        return EmployeeRequest(
            name = faker.name().fullName(),
            document = faker.idNumber().valid(),
            collageCompletedYear = faker.number().numberBetween(1990, 2020),
            bornAt = faker.date().birthday(20, 60)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(),
            sector = SectorFactory.builderSector().code
        )
    }

    fun builderEmployee(id: Long): Employee {
        return Employee(
            id = id,
            name = faker.name().fullName(),
            document = faker.idNumber().valid(),
            collageCompletedYear = faker.number().numberBetween(1990, 2020),
            bornAt = faker.date().birthday(20, 60)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(),
            sector = SectorFactory.builderSector()
        )
    }
}
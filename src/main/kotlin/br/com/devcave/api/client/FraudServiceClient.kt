package br.com.devcave.api.client

import br.com.devcave.api.domain.FraudResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(value = "api-configuration-client", url = "\${fraud-service.url}")
interface FraudServiceClient {
    @GetMapping("{document}")
    fun validateFraud(@PathVariable document: String): FraudResponse
}
package com.exacta.desafio.port.adapters.rest.handle

import com.exacta.desafio.application.CustomerApplicationService
import com.exacta.desafio.application.NewCustomerCommand
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.created

@Component
class CustomerHandler(
        private val customerApplicationService: CustomerApplicationService
) {

    suspend fun newCustomer(req: ServerRequest): ServerResponse {

        val command = req.awaitBody<NewCustomerCommand>()

        return try {
            val customerId = customerApplicationService.newCustomer(command)
            created(req.uriBuilder().path("/{customerId}")
                    .build(customerId.toString()))
                    .header("resource-id", customerId.toString())
                    .buildAndAwait()
        } catch (ex: Exception) {
            when (ex) {
                else -> throw ex
            }
        }

    }

}
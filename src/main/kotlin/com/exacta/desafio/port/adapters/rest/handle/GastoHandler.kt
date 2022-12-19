package com.exacta.desafio.port.adapters.rest.handle

import com.exacta.desafio.application.GastoApplicationService
import com.exacta.desafio.application.config.ApiError
import com.exacta.desafio.application.config.Constraint
import com.exacta.desafio.application.config.Error
import com.exacta.desafio.domain.DomainConstraints.CustomerNotFound
import com.exacta.desafio.domain.DomainExceptions.CustomerNotFoundException
import com.exacta.desafio.domain.DomainExceptions.GastoNotFoundException
import com.exacta.desafio.domain.customer.CustomerId
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.status
import java.util.*
import javax.money.MonetaryAmount

data class NewGastoCommand(
        @JsonProperty("description")
        val description: String,
        val amount: MonetaryAmount,
        @JsonProperty("tags")
        val tags: List<String>,
        val customerId: CustomerId? = null
)

@Component
class GastoHandler(
        private val gastoApplicationService: GastoApplicationService
) {

    suspend fun newGasto(req: ServerRequest): ServerResponse {

        val customerId = req.pathVariable("customerId").let {
            UUID.fromString(it)
        } ?: throw CustomerNotFoundException("CustomerId inválido", req.pathVariable("customerId"), CustomerNotFound())

        val command = req.awaitBody<NewGastoCommand>()

        return try {
            val gastoId = gastoApplicationService.registerGasto(command.copy(customerId = customerId))
            created(req.uriBuilder().path("/{gastoId}")
                    .build(gastoId.toString()))
                    .header("resource-id", gastoId.toString())
                    .buildAndAwait()
        } catch (ex: Exception) {
            when (ex) {
                is CustomerNotFoundException -> status(HttpStatus.NOT_FOUND).bodyValueAndAwait(
                        ApiError(listOf(Error(message = ex.msg, value = ex.value, constraint = Constraint(ex.constraint.name)))))

                else -> throw ex
            }
        }

    }

    suspend fun getGastos(req: ServerRequest): ServerResponse {
        return try {

            val customerId = req.pathVariable("customerId").let { UUID.fromString(it) }
                    ?: throw CustomerNotFoundException("CustomerId inválido", req.pathVariable("customerId"), CustomerNotFound())

            val pageNumber = req.queryParam("pageNumber")
            val pageSize = req.queryParam("pageSize")

            ServerResponse.ok().bodyValueAndAwait(
                    gastoApplicationService.getGastos(
                            customerId = customerId,
                            pageNumber = if (pageNumber.isPresent) pageNumber.get().toInt() else 0,
                            pageSize = if (pageSize.isPresent) pageSize.get().toInt() else 50,
                    )
            )
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> status(HttpStatus.BAD_REQUEST).bodyValueAndAwait(
                        ApiError(listOf(Error(
                                message = ex.message ?: ex.localizedMessage,
                                value = req.pathVariable("customerId"),
                                constraint = Constraint(name = "customerId")))))

                is CustomerNotFoundException -> status(HttpStatus.NOT_FOUND).bodyValueAndAwait(
                        ApiError(listOf(Error(message = ex.msg, value = ex.value, constraint = Constraint(ex.constraint.name)))))

                else -> throw ex
            }
        }
    }

    suspend fun getGastoById(req: ServerRequest): ServerResponse {

        val gastoId = UUID.fromString(req.pathVariable("gastoId"))

        return try {
            ServerResponse.ok().bodyValueAndAwait(gastoApplicationService.getGastoById(gastoId))
        } catch (ex: Exception) {
            when (ex) {
                is GastoNotFoundException -> status(HttpStatus.NOT_FOUND).bodyValueAndAwait(
                        ApiError(listOf(Error(message = ex.msg, constraint = Constraint(ex.constraint.name)))))

                else -> throw ex
            }
        }

    }

}
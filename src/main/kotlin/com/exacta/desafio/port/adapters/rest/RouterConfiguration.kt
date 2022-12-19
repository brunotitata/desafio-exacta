package com.exacta.desafio.port.adapters.rest

import com.exacta.desafio.application.NewCustomerCommand
import com.exacta.desafio.application.config.ApiError
import com.exacta.desafio.domain.customer.Customer
import com.exacta.desafio.domain.gasto.Gasto
import com.exacta.desafio.port.adapters.rest.handle.CustomerHandler
import com.exacta.desafio.port.adapters.rest.handle.GastoHandler
import com.exacta.desafio.port.adapters.rest.handle.NewGastoCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.Explode
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.enums.ParameterStyle
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.FlowPreview
import org.javamoney.moneta.CurrencyUnitBuilder
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.function.server.coRouter
import java.util.*
import javax.money.Monetary

@Configuration
class RouterConfiguration {

    @FlowPreview
    @RouterOperation(
            path = "/v1/customers",
            consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE],
            method = [RequestMethod.POST],
            beanClass = CustomerHandler::class,
            beanMethod = "newCustomer",
            operation = Operation(
                    operationId = "newCustomer",
                    responses = [
                        ApiResponse(
                                responseCode = "201",
                                description = "Usuário criado com sucesso")
                    ],
                    requestBody = RequestBody(
                            required = true,
                            description = "Exemplo de body para criação de usuario -> {\n" +
                                    "    \"name\": \"Bruno Costa\"\n" +
                                    "}",
                            content = [
                                Content(
                                        schema = Schema(
                                                implementation = NewCustomerCommand::class
                                        )
                                )
                            ]
                    )
            )
    )
    @Bean
    fun customerRoutes(customerHandler: CustomerHandler) = coRouter {
        POST("/v1/customers", customerHandler::newCustomer)
    }

    @FlowPreview
    @RouterOperations(
            RouterOperation(
                    path = "/v1/customers/{customerId}/gastos",
                    produces = [MediaType.APPLICATION_JSON_VALUE],
                    method = [RequestMethod.GET],
                    beanClass = GastoHandler::class,
                    beanMethod = "getGastos",
                    operation = Operation(
                            operationId = "getGastos",
                            responses = [
                                ApiResponse(
                                        responseCode = "200",
                                        description = "Gasto cadastrado com sucesso"
                                ),
                                ApiResponse(
                                        responseCode = "404",
                                        description = "Usuario não encontrado",
                                        content = [Content(schema = Schema(implementation = ApiError::class))])
                            ],
                            parameters = [
                                Parameter(
                                        `in` = ParameterIn.PATH,
                                        name = "customerId",
                                        style = ParameterStyle.SIMPLE,
                                        explode = Explode.FALSE,
                                        required = true
                                ),
                                Parameter(
                                        `in` = ParameterIn.QUERY,
                                        name = "pageNumber",
                                        style = ParameterStyle.SIMPLE,
                                        explode = Explode.FALSE,
                                        required = true
                                ),
                                Parameter(
                                        `in` = ParameterIn.QUERY,
                                        name = "pageSize",
                                        style = ParameterStyle.SIMPLE,
                                        explode = Explode.FALSE,
                                        required = true
                                )
                            ]
                    )
            ),
            RouterOperation(
                    path = "/v1/customers/{customerId}/gastos/{gastoId}",
                    produces = [MediaType.APPLICATION_JSON_VALUE],
                    method = [RequestMethod.GET],
                    beanClass = GastoHandler::class,
                    beanMethod = "getGastoById",
                    operation = Operation(
                            operationId = "getGastoById",
                            responses = [
                                ApiResponse(
                                        responseCode = "200",
                                        description = "Gasto retornado com sucesso"
                                ),
                                ApiResponse(
                                        responseCode = "404",
                                        description = "Usuario não encontrado",
                                        content = [Content(schema = Schema(implementation = ApiError::class))])
                            ],
                            parameters = [
                                Parameter(
                                        `in` = ParameterIn.PATH,
                                        name = "customerId",
                                        style = ParameterStyle.SIMPLE,
                                        explode = Explode.FALSE,
                                        required = true
                                ),
                                Parameter(
                                        `in` = ParameterIn.PATH,
                                        name = "gastoId",
                                        style = ParameterStyle.SIMPLE,
                                        explode = Explode.FALSE,
                                        required = true
                                )
                            ]
                    )
            ),
            RouterOperation(
                    path = "/v1/customers/{customerId}/gastos",
                    consumes = [MediaType.APPLICATION_JSON_VALUE],
                    produces = [MediaType.APPLICATION_JSON_VALUE],
                    method = [RequestMethod.POST],
                    beanClass = GastoHandler::class,
                    beanMethod = "newGasto",
                    operation = Operation(
                            operationId = "newGasto",
                            requestBody = RequestBody(
                                    required = true,
                                    description = "Exemplo de body para criação de gasto -> {\n" +
                                            "    \"amount\": {\n" +
                                            "        \"amount\": 10.00,\n" +
                                            "        \"currency\": \"BRL\"\n" +
                                            "    },\n" +
                                            "    \"description\": \"Maquina de Lavar\",\n" +
                                            "    \"tags\": [\n" +
                                            "        \"Eletrodomestico\",\n" +
                                            "        \"Casa\"\n" +
                                            "    ]\n" +
                                            "}",
                                    content = [
                                        Content(
                                                schema = Schema(
                                                        implementation = NewGastoCommand::class
                                                )
                                        )]
                            ),
                            responses = [
                                ApiResponse(
                                        responseCode = "201",
                                        description = "Gasto cadastrado com sucesso"
                                ),
                                ApiResponse(
                                        responseCode = "404",
                                        description = "Usuario não encontrado",
                                        content = [
                                            Content(
                                                    schema = Schema(
                                                            implementation = ApiError::class))])
                            ],
                            parameters = [
                                Parameter(
                                        `in` = ParameterIn.PATH,
                                        name = "customerId",
                                        style = ParameterStyle.SIMPLE,
                                        explode = Explode.FALSE,
                                        required = true
                                )
                            ]
                    )
            )
    )
    @Bean
    fun gastoRoutes(gastoHandler: GastoHandler) = coRouter {
        POST("/v1/customers/{customerId}/gastos", gastoHandler::newGasto)
        GET("/v1/customers/{customerId}/gastos", gastoHandler::getGastos)
        GET("/v1/customers/{customerId}/gastos/{gastoId}", gastoHandler::getGastoById)
    }
}
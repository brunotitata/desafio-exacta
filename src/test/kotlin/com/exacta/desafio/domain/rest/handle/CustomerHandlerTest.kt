package com.exacta.desafio.domain.rest.handle

import com.exacta.desafio.application.CustomerApplicationService
import com.exacta.desafio.application.NewCustomerCommand
import com.exacta.desafio.application.config.RestConfiguration
import com.exacta.desafio.port.adapters.rest.RouterConfiguration
import com.exacta.desafio.port.adapters.rest.handle.CustomerHandler
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.*
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.HandlerStrategies
import java.util.*

class CustomerHandlerTest : AnnotationSpec() {

    private val customerId: UUID = UUID.fromString("87a1b85b-5135-4d27-8015-9950142138d2")

    private val customerApplicationService = mockk<CustomerApplicationService>()
    private val handler = CustomerHandler(customerApplicationService)
    private val mapper = RestConfiguration().objectMapper()

    private val webClient = WebTestClient
            .bindToRouterFunction(RouterConfiguration().customerRoutes(handler))
            .handlerStrategies(HandlerStrategies
                    .builder()
                    .codecs { configurer ->
                        configurer.registerDefaults(false)
                        configurer.customCodecs().register(Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON))
                        configurer.customCodecs().register(Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON))
                    }
                    .build())
            .build()

    @BeforeEach
    fun setUp() = clearAllMocks()

    @Test
    fun newCustomer() {

        val newCustomerCommand = NewCustomerCommand("Bruno Costa")

        coEvery { customerApplicationService.newCustomer(newCustomerCommand) } returns customerId

        webClient
                .post()
                .uri("/v1/customers/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(newCustomerCommand))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/v1/customers/$customerId")
                .expectHeader().valueEquals("resource-id", customerId.toString())

        coVerify { customerApplicationService.newCustomer(newCustomerCommand) }
        confirmVerified(customerApplicationService)

    }

}
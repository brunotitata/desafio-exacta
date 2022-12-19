package com.exacta.desafio.domain.rest.handle

import com.exacta.desafio.application.GastoApplicationService
import com.exacta.desafio.application.GastosData
import com.exacta.desafio.application.config.RestConfiguration
import com.exacta.desafio.domain.DomainConstraints.CustomerNotFound
import com.exacta.desafio.domain.DomainExceptions.CustomerNotFoundException
import com.exacta.desafio.port.adapters.rest.RouterConfiguration
import com.exacta.desafio.port.adapters.rest.handle.GastoHandler
import com.exacta.desafio.port.adapters.rest.handle.NewGastoCommand
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.*
import org.javamoney.moneta.CurrencyUnitBuilder
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.HandlerStrategies
import java.time.OffsetDateTime
import java.util.*
import javax.money.Monetary

class GastoHandlerTest : AnnotationSpec() {

    private val customerId: UUID = UUID.fromString("87a1b85b-5135-4d27-8015-9950142138d2")
    private val gastoId: UUID = UUID.fromString("3c32300c-725a-49b7-ac9a-2e95091a810d")

    private val gastoApplicationService = mockk<GastoApplicationService>()
    private val handler = GastoHandler(gastoApplicationService)
    private val mapper = RestConfiguration().objectMapper()

    private val webClient = WebTestClient
            .bindToRouterFunction(RouterConfiguration().gastoRoutes(handler))
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
    fun newGasto() {

        val newGastoCommand = NewGastoCommand(
                description = "Bar dos amigos",
                amount = toMoney(125.00),
                tags = listOf("Varejo"),
                customerId = customerId
        )

        coEvery { gastoApplicationService.registerGasto(newGastoCommand) } returns gastoId

        webClient
                .post()
                .uri("/v1/customers/{customerId}/gastos", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(newGastoCommand))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/v1/customers/$customerId/gastos/$gastoId")
                .expectHeader().valueEquals("resource-id", gastoId.toString())

        coVerify { gastoApplicationService.registerGasto(newGastoCommand) }
        confirmVerified(gastoApplicationService)

    }

    @Test
    fun newGastoGivenCustomerNotFoundThrowException() {

        val newGastoCommand = NewGastoCommand(
                description = "Bar dos amigos",
                amount = toMoney(125.00),
                tags = listOf("Varejo"),
                customerId = customerId
        )

        coEvery { gastoApplicationService.registerGasto(newGastoCommand) } throws CustomerNotFoundException(
                msg = "Customer não encontrado",
                value = customerId,
                constraint = CustomerNotFound()
        )

        webClient
                .post()
                .uri("/v1/customers/{customerId}/gastos", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(newGastoCommand))
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Customer não encontrado")
                .jsonPath("$.errors[0].constraint.name").isEqualTo("CustomerNotFound")
                .jsonPath("$.errors[0].constraint.params").isArray

        coVerify { gastoApplicationService.registerGasto(newGastoCommand) }
        confirmVerified(gastoApplicationService)

    }

    @Test
    fun getGasto() {

        val pageResult = PageImpl(listOf(GastosData(
                gastoId = UUID.fromString("99edd1a2-defa-4d59-a092-07faca38c0cc"),
                amount = toMoney(15.00),
                description = "Bar do centro",
                dateTime = OffsetDateTime.now(),
                tags = "[ Outros ]"
        )))

        coEvery {
            gastoApplicationService.getGastos(
                    pageNumber = 0,
                    pageSize = 50,
                    customerId = customerId)
        } returns pageResult

        webClient
                .get()
                .uri("/v1/customers/{customerId}/gastos", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.content[0].gastoId").isEqualTo("99edd1a2-defa-4d59-a092-07faca38c0cc")
                .jsonPath("$.content[0].amount.amount").isEqualTo(15.00)
                .jsonPath("$.content[0].amount.currency").isEqualTo("BRL")
                .jsonPath("$.content[0].description").isEqualTo("Bar do centro")
                .jsonPath("$.content[0].tags").isEqualTo("[ Outros ]")

        coVerify { gastoApplicationService.getGastos(0, 50, customerId) }
        confirmVerified(gastoApplicationService)

    }

    private fun toMoney(value: Double) =
            Monetary.getDefaultAmountFactory()
                    .setNumber(value)
                    .setCurrency(CurrencyUnitBuilder.of("BRL", "real").setDefaultFractionDigits(2).build())
                    .create()

}
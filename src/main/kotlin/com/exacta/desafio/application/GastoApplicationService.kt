package com.exacta.desafio.application

import com.exacta.desafio.domain.DomainConstraints.CustomerNotFound
import com.exacta.desafio.domain.DomainConstraints.GastoNotFound
import com.exacta.desafio.domain.DomainExceptions.CustomerNotFoundException
import com.exacta.desafio.domain.DomainExceptions.GastoNotFoundException
import com.exacta.desafio.domain.customer.CustomerId
import com.exacta.desafio.domain.customer.CustomerRepository
import com.exacta.desafio.domain.gasto.Gasto
import com.exacta.desafio.domain.gasto.GastoId
import com.exacta.desafio.domain.gasto.GastoRepository
import com.exacta.desafio.port.adapters.rest.handle.NewGastoCommand
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.money.MonetaryAmount

data class GastosData(
        val gastoId: GastoId,
        val amount: MonetaryAmount,
        val description: String,
        val dateTime: OffsetDateTime,
        val tags: String
)

@Service
class GastoApplicationService(
        private val customerRepository: CustomerRepository,
        private val gastoRepository: GastoRepository
) {

    suspend fun registerGasto(command: NewGastoCommand): GastoId {

        customerRepository.run {
            command.customerId?.let {
                this.findByCustomerId(it)?.let { customer ->
                    val gasto = Gasto(
                            description = command.description,
                            amount = command.amount,
                            tags = command.tags.toString(),
                            customer = customer)

                    customer.newGasto(gasto)

                    this.save(customer)
                    return gasto.gastoId

                }
            } ?: throw CustomerNotFoundException(
                    msg = "Customer não encontrado",
                    value = command.customerId,
                    constraint = CustomerNotFound())
        }

    }

    fun getGastos(pageNumber: Int, pageSize: Int, customerId: CustomerId): Page<GastosData> {
        return customerRepository.findByCustomerId(customerId)?.let {

            val paging: Pageable = PageRequest.of(pageNumber, pageSize)
            val start = Math.min(paging.offset, it.gastos.size.toLong())
            val end = Math.min(start + paging.pageSize, it.gastos.size.toLong())

            val listPageable = it.gastos.map { toGastoData(it) }

            PageImpl(listPageable.subList(start.toInt(), end.toInt()), paging, listPageable.size.toLong())

        } ?: throw CustomerNotFoundException("Customer não encontrado", customerId, CustomerNotFound())
    }

    private fun toGastoData(gasto: Gasto) = GastosData(
            gastoId = gasto.gastoId,
            amount = gasto.amount,
            description = gasto.description,
            dateTime = gasto.dateTime,
            tags = gasto.tags
    )

    suspend fun getGastoById(gastoId: GastoId): GastosData =
            gastoRepository.findByGastoId(gastoId)?.let {
                toGastoData(it)
            } ?: throw GastoNotFoundException("Gasto informado não encontrado.", GastoNotFound())

}
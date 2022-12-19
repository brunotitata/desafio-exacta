package com.exacta.desafio.port.adapters.repository

import com.exacta.desafio.domain.gasto.Gasto
import com.exacta.desafio.domain.gasto.GastoId
import com.exacta.desafio.domain.gasto.GastoRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

interface GastoRepositorySpringData : PagingAndSortingRepository<Gasto, GastoId> {
    fun findByGastoId(gastoId: GastoId): Gasto?
}

@Repository
class GastoRepositoryJpa(
        private val repository: GastoRepositorySpringData
) : GastoRepository {

    override suspend fun findByGastoId(gastoId: GastoId): Gasto? = repository.findByGastoId(gastoId)
}
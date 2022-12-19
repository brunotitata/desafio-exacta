package com.exacta.desafio.domain.gasto

interface GastoRepository {

//    fun findAllGastos(limit: Int, pageNumber: Int): Page<Gasto>
    suspend fun findByGastoId(gastoId: GastoId): Gasto?
}
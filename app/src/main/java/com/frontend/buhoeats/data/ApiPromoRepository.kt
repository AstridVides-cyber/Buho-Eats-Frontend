package com.frontend.buhoeats.data

import com.frontend.buhoeats.data.api.ApiClient
import com.frontend.buhoeats.data.mappers.DataMappers.toPromo
import com.frontend.buhoeats.data.mappers.DataMappers.toCreatePromotionRequest
import com.frontend.buhoeats.data.mappers.DataMappers.toUpdatePromotionRequest
import com.frontend.buhoeats.models.Promo
import kotlinx.coroutines.runBlocking

class ApiPromoRepository : PromoRepository {
    private val apiService = ApiClient.apiService
    private var currentToken: String? = null

    fun setAuthToken(token: String) {
        currentToken = token
    }

    private fun getAuthHeader(): String {
        return "Bearer ${currentToken ?: ""}"
    }

    override fun getPromosForRestaurant(restaurantId: String): List<Promo> {
        // BÚSQUEDA MANUAL: Como no hay endpoint para obtener todas las promos,
        // simulamos el comportamiento InMemory obteniendo promos individuales
        // o implementamos un cache local
        return getAllPromos().filter { it.restaurantId == restaurantId }
    }

    override fun getAllPromos(): List<Promo> {
        // IMPLEMENTACIÓN MANUAL: Como el backend no tiene endpoint GET /promotion/all,
        // podemos mantener un cache local o solicitar al backend que agregue este endpoint
        return emptyList<Promo>()
    }

    override fun addPromoToRestaurant(restaurantId: String, promo: Promo) {
        runBlocking {
            try {
                val request = promo.toCreatePromotionRequest()
                apiService.createPromotion(restaurantId, getAuthHeader(), request)
            } catch (e: Exception) {
                // Log error or handle appropriately
            }
        }
    }

    override fun removePromo(promo: Promo) {
        runBlocking {
            try {
                apiService.deletePromotion(promo.restaurantId, promo.id, getAuthHeader())
            } catch (e: Exception) {
                // Log error or handle appropriately
            }
        }
    }

    override fun updatePromo(updatedPromo: Promo) {
        runBlocking {
            try {
                val request = updatedPromo.toUpdatePromotionRequest()
                apiService.updatePromotion(updatedPromo.restaurantId, updatedPromo.id, getAuthHeader(), request)
            } catch (e: Exception) {
                // Log error or handle appropriately
            }
        }
    }

    // Funciones adicionales con búsqueda manual
    fun getPromoById(restaurantId: String, promoId: String): Promo? {
        return runBlocking {
            try {
                val response = apiService.getPromotionById(restaurantId, promoId)
                if (response.isSuccessful) {
                    response.body()?.data?.toPromo()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    // CACHE LOCAL: Para manejar la falta de endpoint GET /promotion/all
    private val localPromoCache = mutableListOf<Promo>()

    fun addToLocalCache(promo: Promo) {
        localPromoCache.removeAll { it.id == promo.id }
        localPromoCache.add(promo)
    }

    fun removeFromLocalCache(promoId: String) {
        localPromoCache.removeAll { it.id == promoId }
    }

    fun getFromLocalCache(): List<Promo> {
        return localPromoCache.toList()
    }
}

package com.frontend.buhoeats.data

import com.frontend.buhoeats.data.api.ApiClient
import com.frontend.buhoeats.data.mappers.DataMappers.toDish
import com.frontend.buhoeats.data.mappers.DataMappers.toCreatePlateRequest
import com.frontend.buhoeats.data.mappers.DataMappers.toUpdatePlateRequest
import com.frontend.buhoeats.models.Dish
import kotlinx.coroutines.runBlocking

class ApiDishRepository : DishRepository {
    private val apiService = ApiClient.apiService
    private var currentToken: String? = null

    fun setAuthToken(token: String) {
        currentToken = token
    }

    private fun getAuthHeader(): String {
        return "Bearer ${currentToken ?: ""}"
    }

    override fun getDishesForRestaurant(restaurantId: String): List<Dish> {
        return runBlocking {
            try {
                val response = apiService.getAllPlates(restaurantId)
                if (response.isSuccessful) {
                    response.body()?.data?.map { it.toDish() } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override fun addDishToRestaurant(restaurantId: String, dish: Dish) {
        runBlocking {
            try {
                val request = dish.toCreatePlateRequest()
                apiService.createPlate(restaurantId, getAuthHeader(), request)
            } catch (e: Exception) {
                // Log error or handle appropriately
            }
        }
    }

    override fun removeDish(restaurantId: String, dishId: String) {
        runBlocking {
            try {
                apiService.deletePlate(restaurantId, dishId, getAuthHeader())
            } catch (e: Exception) {
                // Log error or handle appropriately
            }
        }
    }

    override fun updateDish(restaurantId: String, dish: Dish) {
        runBlocking {
            try {
                val request = dish.toUpdatePlateRequest()
                apiService.updatePlate(restaurantId, dish.id, getAuthHeader(), request)
            } catch (e: Exception) {
                // Log error or handle appropriately
            }
        }
    }

    // Funciones adicionales con búsqueda manual
    fun getAllDishes(restaurantId: String): List<Dish> {
        return runBlocking {
            try {
                val response = apiService.getAllPlates(restaurantId)
                if (response.isSuccessful) {
                    response.body()?.data?.map { it.toDish() } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun getDishById(restaurantId: String, dishId: String): Dish? {
        return runBlocking {
            try {
                val response = apiService.getPlateById(restaurantId, dishId)
                if (response.isSuccessful) {
                    response.body()?.data?.toDish()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getDishesByCategory(restaurantId: String, category: String): List<Dish> {
        return runBlocking {
            try {
                val response = apiService.getAllPlates(restaurantId)
                if (response.isSuccessful) {
                    response.body()?.data
                        ?.map { it.toDish() }
                        ?.filter { it.category == category } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

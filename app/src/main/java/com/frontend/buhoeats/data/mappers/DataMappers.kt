package com.frontend.buhoeats.data.mappers

import com.frontend.buhoeats.data.dto.*
import com.frontend.buhoeats.models.*

object DataMappers {

    // USER MAPPERS
    fun BackendUserDTO.toUser(): User {
        return User(
            id = this.id,
            name = this.name,
            lastName = this.lastName,
            email = this.email,
            password = this.password ?: "",
            imageProfile = this.picture ?: "", // MAPEO: backend.picture → modelo.imageProfile
            rol = this.rol,
            favoritos = this.favorites.toMutableList() // MAPEO: backend.favorites → modelo.favoritos
        )
    }

    fun User.toCreateUserRequest(): CreateUserRequest {
        return CreateUserRequest(
            name = this.name,
            lastName = this.lastName,
            email = this.email,
            password = this.password,
            rol = this.rol,
            picture = if (this.imageProfile.isNotEmpty()) this.imageProfile else null // MAPEO: modelo.imageProfile → backend.picture
        )
    }

    fun User.toUpdateUserRequest(): UpdateUserRequest {
        return UpdateUserRequest(
            name = this.name,
            lastName = this.lastName,
            email = this.email,
            picture = if (this.imageProfile.isNotEmpty()) this.imageProfile else null // MAPEO: modelo.imageProfile → backend.picture
        )
    }

    // RESTAURANT MAPPERS
    fun BackendRestaurantDTO.toRestaurant(): Restaurant {
        return Restaurant(
            id = this.id,
            name = this.name,
            description = this.description,
            imageUrl = this.image ?: "", // MAPEO: backend.image → modelo.imageUrl
            category = this.category,
            contactInfo = ContactInfo(
                email = this.email,
                phone = this.phone ?: "",
                hours = "", // No viene del backend, valor por defecto
                address = this.direction
            ),
            ratings = mutableListOf(), // Se poblará con datos adicionales si es necesario
            comments = mutableListOf(), // Se poblará con datos adicionales si es necesario
            menu = emptyList(), // Se poblará con datos adicionales si es necesario
            promos = emptyList(), // Se poblará con datos adicionales si es necesario
            latitud = 0.0, // Se poblará con coordenadas si están disponibles
            longitud = 0.0, // Se poblará con coordenadas si están disponibles
            admin = this.idUser,
            blockedUsers = emptyList() // Se poblará con datos adicionales si es necesario
        )
    }

    fun Restaurant.toCreateRestaurantRequest(): CreateRestaurantRequest {
        return CreateRestaurantRequest(
            name = this.name,
            description = this.description,
            category = this.category,
            email = this.contactInfo.email,
            phone = if (this.contactInfo.phone.isNotEmpty()) this.contactInfo.phone else null,
            direction = this.contactInfo.address,
            image = if (this.imageUrl.isNotEmpty()) this.imageUrl else null // MAPEO: modelo.imageUrl → backend.image
        )
    }

    fun Restaurant.toUpdateRestaurantRequest(): UpdateRestaurantRequest {
        return UpdateRestaurantRequest(
            name = this.name,
            description = this.description,
            category = this.category,
            email = this.contactInfo.email,
            phone = if (this.contactInfo.phone.isNotEmpty()) this.contactInfo.phone else null,
            direction = this.contactInfo.address,
            image = if (this.imageUrl.isNotEmpty()) this.imageUrl else null // MAPEO: modelo.imageUrl → backend.image
        )
    }

    // DISH/PLATE MAPPERS
    fun BackendPlateDTO.toDish(): Dish {
        return Dish(
            id = this.id,
            name = this.name,
            description = this.description,
            imageUrl = this.image ?: "", // MAPEO: backend.image → modelo.imageUrl (era picture, ahora es image)
            price = this.price.toString() ,// Convertir de Double a String como espera tu modelo,
            category = this.category
        )
    }

    fun Dish.toCreatePlateRequest(): CreatePlateRequest {
        return CreatePlateRequest(
            name = this.name,
            description = this.description,
            category = "General", // Valor por defecto ya que tu modelo Dish no tiene category
            price = this.price.toDoubleOrNull() ?: 0.0,
            image = if (this.imageUrl.isNotEmpty()) this.imageUrl else null // MAPEO: modelo.imageUrl → backend.image
        )
    }

    fun Dish.toUpdatePlateRequest(): UpdatePlateRequest {
        return UpdatePlateRequest(
            name = this.name,
            description = this.description,
            category = "General", // Valor por defecto
            price = this.price.toDoubleOrNull(),
            image = if (this.imageUrl.isNotEmpty()) this.imageUrl else null // MAPEO: modelo.imageUrl → backend.image
        )
    }

    // PROMO/PROMOTION MAPPERS
    fun BackendPromotionDTO.toPromo(): Promo {
        return Promo(
            id = this.id,
            name = this.title, // MAPEO: backend.title → modelo.name
            description = this.description,
            imageUrl = this.image ?: "", // MAPEO: backend.image → modelo.imageUrl
            price = this.price.before.toString(), // MAPEO: backend.price.before → modelo.price
            promprice = this.price.now.toString(), // MAPEO: backend.price.now → modelo.promprice
            reglas = this.rules, // MAPEO: backend.rules → modelo.reglas
            restaurantId = this.restaurantId
        )
    }

    fun Promo.toCreatePromotionRequest(): CreatePromotionRequest {
        return CreatePromotionRequest(
            title = this.name, // MAPEO: modelo.name → backend.title
            description = this.description,
            price = PriceDTO(
                before = this.price.toDoubleOrNull() ?: 0.0, // MAPEO: modelo.price → backend.price.before
                now = this.promprice.toDoubleOrNull() ?: 0.0 // MAPEO: modelo.promprice → backend.price.now
            ),
            rules = this.reglas, // MAPEO: modelo.reglas → backend.rules
            restaurantId = this.restaurantId,
            image = if (this.imageUrl.isNotEmpty()) this.imageUrl else null // MAPEO: modelo.imageUrl → backend.image
        )
    }

    fun Promo.toUpdatePromotionRequest(): UpdatePromotionRequest {
        return UpdatePromotionRequest(
            title = this.name, // MAPEO: modelo.name → backend.title
            description = this.description,
            price = PriceDTO(
                before = this.price.toDoubleOrNull() ?: 0.0, // MAPEO: modelo.price → backend.price.before
                now = this.promprice.toDoubleOrNull() ?: 0.0 // MAPEO: modelo.promprice → backend.price.now
            ),
            rules = this.reglas, // MAPEO: modelo.reglas → backend.rules
            image = if (this.imageUrl.isNotEmpty()) this.imageUrl else null // MAPEO: modelo.imageUrl → backend.image
        )
    }

    // HELPER MAPPERS
    fun Restaurant.updateWithCoordinate(coordinate: BackendCoordinateDTO?): Restaurant {
        return if (coordinate != null) {
            this.copy(
                latitud = coordinate.lat,
                longitud = coordinate.lng
            )
        } else {
            this
        }
    }

    fun Restaurant.updateWithMenu(dishes: List<Dish>): Restaurant {
        return this.copy(menu = dishes)
    }

    fun Restaurant.updateWithPromos(promos: List<Promo>): Restaurant {
        return this.copy(promos = promos)
    }

    // LOGIN HELPERS
    fun createLoginRequest(email: String, password: String): LoginRequest {
        return LoginRequest(email = email, password = password)
    }

    fun createAddFavoriteRequest(restaurantId: String): AddFavoriteRequest {
        return AddFavoriteRequest(idRestaurant = restaurantId)
    }

    fun createRemoveFavoriteRequest(restaurantId: String): RemoveFavoriteRequest {
        return RemoveFavoriteRequest(idRestaurant = restaurantId)
    }

    fun createChangeRoleRequest(role: String): ChangeRoleRequest {
        return ChangeRoleRequest(rol = role)
    }
}

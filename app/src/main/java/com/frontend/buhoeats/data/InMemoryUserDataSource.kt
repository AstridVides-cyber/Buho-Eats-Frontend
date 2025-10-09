package com.frontend.buhoeats.data

import com.frontend.buhoeats.models.Comment
import com.frontend.buhoeats.models.Restaurant
import com.frontend.buhoeats.models.User
import com.frontend.buhoeats.models.ContactInfo
import com.frontend.buhoeats.models.Dish
import com.frontend.buhoeats.models.Promo
import com.frontend.buhoeats.models.Rating

object InMemoryUserDataSource {

         private var users = mutableListOf(
        User(
            id = "1",
            name = "Astrid",
            lastName = "Vides",
            imageProfile = "",
            email = "vides67@gmail.com",
            password = "astrid",
            rol = "admin"
        ),
        User(
            id = "2",
            name = "Michelle",
            lastName = "Maltez",
            imageProfile = "",
            email = "michelle@gmail.com",
            password = "michelle123",
            rol = "usuario",
            favoritos = mutableListOf("1", "2")

    ),
        User(
            id = "3",
            name = "Dayalin",
            lastName = "Hernandez",
            imageProfile = "",
            email = "dayalin@correo.com",
            password = "dayalin123",
            rol = "superadmin"
        ),
        User(
            id = "4",
            name = "Diego",
            lastName = "Alvarenga",
            imageProfile = "",
            email = "diego@hotmail.com",
            password = "diego123",
            rol = "admin"
        ),
        User(
            id = "5",
            name = "Mauricio",
            lastName = "Apellidoxd",
            imageProfile = "",
            email = "mauri@hotmail.com",
            password = "mauri123",
            rol = "usuario",
            favoritos = mutableListOf("1")
        ),
             User(
                 id = "6",
                 name = "Dani",
                 lastName = "Apellidoxd",
                 imageProfile = "",
                 email = "dani@hotmail.com",
                 password = "dani123",
                 rol = "usuario",
                 favoritos = mutableListOf("2")
             ),
             User(
                 id = "7",
                 name = "spike",
                 lastName = "maltez",
                 imageProfile = "",
                 email = "spike@gmail.com",
                 password = "spike123",
                 rol = "admin"
             ),
    )

    fun getUsers(): List<User> {
        return users.toList()
    }

    private var restaurants = mutableListOf(
        Restaurant(
            id = "1",
            name = "Restaurante El Buen Sabor",
            description = "Comida típica salvadoreña con un toque moderno.",
            imageUrl = "https://images.unsplash.com/photo-1600891964599-f61ba0e24092",
            category = "Desayuno",
            contactInfo = ContactInfo(
                email = "contacto@elbuen.sv",
                phone = "2222-3333",
                hours = "Lunes a Domingo, 8:00 AM - 9:00 PM",
                address = "Calle El Progreso #123, San Salvador"
            ),
            ratings = mutableListOf(
                Rating(userId = "2", rating = 5),
                Rating(userId = "5", rating = 4)
            ),
            comments = mutableListOf(
                Comment(userId = "2", comment = "Excelente atención y la comida deliciosa."),
                Comment(userId = "5", comment = "Muy bonito el lugar y buen ambiente")
            ),
            menu = listOf(
                Dish(
                    id = "1",
                    name = "Pupusas revueltas",
                    description = "Pupusas con chicharrón, frijoles y queso.",
                    imageUrl = "https://images.unsplash.com/photo-1617902271850-21d560795d60",
                    price = "5.00",
                    category = "Desayuno"
                ),
                Dish(
                    id = "2",
                    name = "Sopa de gallina india",
                    description = "Sopa tradicional con vegetales frescos.",
                    imageUrl = "https://images.unsplash.com/photo-1665594051407-7385d281ad76",
                    price = "5.00",
                    category = "Desayuno"
                )
            ),
                promos = listOf(
                    Promo(
                        id = "1",
                        name = "Pupusas de loroco",
                        description = "Pupusas con chicharrón, frijoles y queso.",
                        imageUrl = "https://images.unsplash.com/photo-1617902271850-21d560795d60",
                        price = "5.00",
                        promprice = "3.00",
                        reglas = "Solo de lunes a viernes desde las 7 am",
                        restaurantId = "1"
                    ),
                    Promo(
                        id = "2",
                        name = "Sopa de gallina india",
                        description = "Sopa tradicional con vegetales frescos.",
                        imageUrl = "https://images.unsplash.com/photo-1665594051407-7385d281ad76",
                        price = "5.00",
                        promprice = "3.00",
                        reglas = "Solo de lunes a viernes desde la 1 pm",
                        restaurantId = "1"
                    )
            ),
            latitud = 13.6929,
            longitud = -89.2182,
            admin = "1",
            blockedUsers = mutableListOf("2")
        ),
        Restaurant(
            id = "2",
            name = "La Esquinita Gourmet",
            description = "Fusión de sabores latinos e internacionales.",
            imageUrl = "https://images.unsplash.com/photo-1466978913421-dad2ebd01d17",
            category =  "Almuerzo",
            contactInfo = ContactInfo(
                email = "info@laesquinita.com",
                phone = "2444-5566",
                hours = "Martes a Domingo, 12:00 PM - 10:00 PM",
                address = "Avenida Central #456, Santa Tecla"
            ),
            ratings = mutableListOf(
                Rating(userId = "5", rating = 4),
                Rating(userId = "2", rating = 3)
            ),
            comments = mutableListOf(
                Comment(userId = "2", comment = "El mejor ceviche que he probado."),
                Comment(userId = "5", comment = "Un poco caro, pero vale la pena.")
            ),
            menu = listOf(
                Dish(
                    id = "3",
                    name = "Ceviche mixto",
                    description = "Pescado, camarones y calamares en jugo de limón.",
                    imageUrl = "https://images.unsplash.com/photo-1626663011519-b42e5ee10056",
                    price = "5.00",
                    category = "Almuerzo"
                ),
                Dish(
                    id = "4",
                    name = "Tacos al pastor",
                    description = "Tacos con carne de cerdo marinada al estilo mexicano.",
                    imageUrl = "https://images.unsplash.com/photo-1552332386-f8dd00dc2f85",
                    price = "5.00",
                    category = "Almuerzo"
                )
            ),
            promos = listOf(
                Promo(
                    id = "3",
                        name = "Ceviche mixto",
                        description = "Pescado, camarones y calamares en jugo de limón.",
                        imageUrl = "https://images.unsplash.com/photo-1626663011519-b42e5ee10056",
                        price = "5.00",
                        promprice = "3.00",
                        reglas = "Solo de lunes a viernes desde las 4 pm",
                        restaurantId = "2"

                    ),
                    Promo(
                        id = "4",
                        name = "Tacos al pastor",
                        description = "Tacos con carne de cerdo marinada al estilo mexicano.",
                        imageUrl = "https://images.unsplash.com/photo-1552332386-f8dd00dc2f85",
                        price = "5.00",
                        promprice = "3.00",
                        reglas = "Solo de lunes a viernes desde las 12 pm",
                        restaurantId = "2"
                  )
            ),
            latitud = 13.7929,
            longitud = -89.2182,
            admin = "4",
            blockedUsers = mutableListOf()
        )
    )
    fun getRestaurants(): List<Restaurant> {
        return restaurants.toList()
    }

    fun updateRestaurant(updatedRestaurant: Restaurant) {
        val index = restaurants.indexOfFirst { it.id == updatedRestaurant.id }
        if (index != -1) {
            restaurants[index] = updatedRestaurant
        }
    }
    fun getUserEmailById(userId: String): String {
        return getUsers().find { it.id == userId }?.email ?: ""
    }
    fun addRestaurant(restaurant: Restaurant) {
        restaurants.add(restaurant)
    }
    fun getNextRestaurantId(): String {
        return ((restaurants.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0) + 1).toString()
    }
    fun deleteRestaurant(restaurantId: String) {
        restaurants.removeIf { it.id == restaurantId }
    }
    fun setUsers(updatedUsers: List<User>) {
        users = updatedUsers.toMutableList()
    }
    fun getRestaurantById(restaurantId: String): Restaurant? {
        return restaurants.find { it.id == restaurantId }
    }
    fun getUserById(userId: String): User? {
        return users.find { it.id == userId }
    }
    fun blockUserFromRestaurant(userId: String, restaurantId: String) {
        val restaurant = getRestaurantById(restaurantId)
        val user = getUserById(userId)

        restaurant?.let {
            if (!it.blockedUsers.contains(userId.toString())) {
                val mutableBlockedUsers = it.blockedUsers.toMutableList()
                mutableBlockedUsers.add(userId.toString())
                it.blockedUsers = mutableBlockedUsers
            }
        }

        user?.let { userToUpdate ->
            if (userToUpdate.favoritos.contains(restaurantId.toString())) {
                val mutableFavorites = userToUpdate.favoritos.toMutableList()
                mutableFavorites.remove(restaurantId.toString())
                val userIndex = users.indexOfFirst { it.id == userId.toString() }
                if (userIndex != -1) {
                    users[userIndex] = userToUpdate.copy(favoritos = mutableFavorites)
                }
            }
        }
    }
    fun unblockUserFromRestaurant(userId: String, restaurantId: String) {
        val restaurant = getRestaurantById(restaurantId)
        restaurant?.let {
            val mutableBlockedUsers = it.blockedUsers.toMutableList()
            mutableBlockedUsers.remove(userId.toString())
            it.blockedUsers = mutableBlockedUsers
        }
    }
    fun addPromoToRestaurant(restaurantId: String, promo: Promo) {
        restaurants.find { it.id == restaurantId }?.let { restaurant ->
            restaurant.promos = restaurant.promos.toMutableList().apply { add(promo) }
        }
    }
    fun removePromo(promo: Promo) {
        restaurants = restaurants.map { restaurant ->
            if (restaurant.promos.any { it.id == promo.id }) {
                restaurant.copy(promos = restaurant.promos.filterNot { it.id == promo.id })
            } else restaurant
        }.toMutableList()
    }
    fun updatePromo(updatedPromo: Promo) {
        restaurants = restaurants.map { restaurant ->
            if (restaurant.promos.any { it.id == updatedPromo.id }) {
                restaurant.copy(promos = restaurant.promos.map { if (it.id == updatedPromo.id) updatedPromo else it })
            } else restaurant
        }.toMutableList()





}
}

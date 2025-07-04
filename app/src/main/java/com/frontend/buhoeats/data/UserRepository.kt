package com.frontend.buhoeats.data

import com.frontend.buhoeats.models.User

interface UserRepository {
    fun getUsers(): List<User>
    fun getUserByEmail(email: String): User?
    fun registerUser(newUser: User): Boolean
    fun updateUser(user: User): Boolean
    fun assignRoleToUser(email: String, newRole: String): Boolean
    fun getUserById(userId: String): User?
    fun blockUserFromRestaurant(userId: String, restaurantId: String)
    fun unblockUserFromRestaurant(userId: String, restaurantId: String)
}

package com.datastax.restaurant_reviews.json_types

data class AuthResponse(
    var authToken: String = ""
)

data class Restaurants (
    var name: String,
    var city: String,
    var state: String,
    var cuisine: String,
    var rating: Int,
    var review: String
)

data class RestaurantsWrapper (
    var count: Int,
    var rows: Array<Restaurants>
)
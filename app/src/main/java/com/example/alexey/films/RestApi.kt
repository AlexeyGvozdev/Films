package com.example.alexey.films

import retrofit2.Call
import retrofit2.http.GET


interface RestApi {

    @GET("./3/discover/movie?api_key=6ccd72a2a8fc239b13f209408fc31c33&language=ru")
    fun getFilms(): Call<Data>
}
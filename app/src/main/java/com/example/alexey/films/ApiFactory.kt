package com.example.alexey.films

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ApiFactory {
    companion object {

        fun getFulmsService(): RestApi = buildRetrofit().create(RestApi::class.java)

        private fun buildRetrofit(): Retrofit = Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }
}
package com.example.soulmatchapp.api

import com.example.soulmatchapp.model.Quote
import retrofit2.Call
import retrofit2.http.GET

interface QuoteApi {
    @GET("random?tags=love")
    fun getRandomLoveQuote(): Call<Quote>
}

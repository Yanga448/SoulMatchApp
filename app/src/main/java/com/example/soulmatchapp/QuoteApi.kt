package com.example.soulmatchapp.api

import com.example.soulmatchapp.model.Quote
import retrofit2.Call
import retrofit2.http.GET

interface QuoteApi {
    // ZenQuotes returns a JSON array of quotes
    @GET("random")
    fun getRandomLoveQuote(): Call<List<Quote>>
}

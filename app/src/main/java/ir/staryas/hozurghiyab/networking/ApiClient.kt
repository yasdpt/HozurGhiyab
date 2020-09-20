package ir.staryas.hozurghiyab.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiClient {
    private val baseUrl:String = "http://staryas.ir/hgh/api/"

    fun getClient():Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}
package com.example.thikaeshop.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PaymentApiService {
    @POST("initiate-payment")
    suspend fun initiatePayment(@Body request: PaymentRequest): PaymentResponse

    @GET("payment-status/{orderId}")
    suspend fun getPaymentStatus(@Path("orderId") orderId: String): PaymentStatusResponse
}
data class PaymentRequest(
    val phone: String,
    val amount: Int,
    val orderId: String
)
data class PaymentResponse(
    val tracking_id: String? = null,
    val success: Boolean? = null,
    val error: String? = null,
    val message: String? = null,
    val simulated: Boolean? = null,
    val always_active: Boolean? = null
)
data class PaymentStatusResponse(
    val orderId: String,
    val status: String, // "pending", "success", "failed"
    val tracking_id: String? = null,
    val always_active: Boolean? = null,
    val mpesa_receipt: String? = null
)

object PaymentApiClient {
    private const val BASE_URL = "https://mpesa-backend-q32d.onrender.com/"

    val instance: PaymentApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(PaymentApiService::class.java)
    }
}
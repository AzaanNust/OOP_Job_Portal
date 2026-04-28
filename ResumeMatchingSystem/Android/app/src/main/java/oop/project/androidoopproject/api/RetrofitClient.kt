package oop.project.androidoopproject.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /**
     * BASE_URL — change this to match your setup:
     *   Android Emulator  : "http://10.0.2.2:8080/"
     *   Real phone (WiFi) : "http://192.168.1.X:8080/"  (use your PC's local IP)
     */
    private const val BASE_URL = "https://oop-job-portal.onrender.com/api/"

    // Creates a logging interceptor for network calls (used with OkHttp/Retrofit).
// HttpLoggingInterceptor() → initializes the interceptor object.
//
// .apply { ... } → Kotlin scope function used to configure the object immediately
// after creation and return the same instance.
//
// level = HttpLoggingInterceptor.Level.BODY → sets logging level to the most detailed:
// logs request URL, headers, request body, response body, and status.
//
// NOTE:
// - Useful for debugging API calls during development.
// - Should be avoided or reduced (e.g., BASIC/NONE) in production
//   because it can expose sensitive data and impact performance.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val baseClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()


    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(baseClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
        // .create(ApiService::class.java)
// → Tells Retrofit to generate a runtime implementation of the ApiService interface
// → Allows you to call API endpoints (defined with annotations like @GET, @POST) as normal Kotlin functions
    }
    // Retrofit is a high-level library that simplifies API calls by handling requests, responses, and JSON conversion automatically.
// HTTP client (e.g., OkHttp) is a low-level tool that sends HTTP requests but requires manual handling of data and responses.

    /** Authenticated API — adds JWT token to every request */
    /**
     * Creates a Retrofit API service that automatically includes a JWT token
     * in every request for authenticated (protected) endpoints.
     */
    fun apiWithAuth(token: String): ApiService {

        // Create a new OkHttpClient based on the existing baseClient
        // newBuilder() → clones baseClient so we can modify it without affecting the original
        val authClient = baseClient.newBuilder()
            // Add an interceptor to modify every outgoing request
            .addInterceptor { chain ->
                // Get the original request
                val request = chain.request().newBuilder()
                    // Add Authorization header with Bearer token
                    // Format: Authorization: Bearer <token>
                    .addHeader("Authorization", "Bearer $token")
                    // Build the modified request
                    .build()
                // Proceed with the modified request
                chain.proceed(request)
            }
            // Build the new client with authentication capability
            .build()
        // Create and return a Retrofit instance using the authenticated client
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

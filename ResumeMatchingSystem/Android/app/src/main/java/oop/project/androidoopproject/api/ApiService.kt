package oop.project.androidoopproject.api

import oop.project.androidoopproject.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ─────────────────────────────────────────────────────
    @POST("auth/register/seeker")
    suspend fun registerSeeker(@Body request: RegisterSeekerRequest): Response<AuthResponse>

    @POST("auth/register/employer")
    suspend fun registerEmployer(@Body request: RegisterEmployerRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ── Profile ───────────────────────────────────────────────────
    @GET("profile/seeker")
    suspend fun getSeekerProfile(): Response<SeekerProfile>

    @PUT("profile/seeker")
    suspend fun updateSeekerProfile(@Body request: UpdateSeekerRequest): Response<SeekerProfile>

    @GET("profile/employer")
    suspend fun getEmployerProfile(): Response<EmployerProfile>

    @PUT("profile/employer")
    suspend fun updateEmployerProfile(@Body request: UpdateEmployerRequest): Response<EmployerProfile>

    // ── Jobs ─────────────────────────────────────────────────────
    @GET("jobs")
    suspend fun searchJobs(
        @Query("title")    title: String?    = null,
        @Query("location") location: String? = null,
        @Query("shift")    shift: String?    = null,
        @Query("page")     page: Int         = 0,
        @Query("size")     size: Int         = 20
    ): Response<PageResponse<JobListingResponse>>

    @GET("jobs/{id}")
    suspend fun getJobById(@Path("id") id: Long): Response<JobListingResponse>

    @POST("jobs")
    suspend fun postJob(@Body request: PostJobRequest): Response<JobListingResponse>

    @GET("jobs/my-jobs")
    suspend fun getMyJobs(): Response<List<JobListingResponse>>

    @PUT("jobs/{id}")
    suspend fun updateJob(@Path("id") id: Long, @Body request: PostJobRequest): Response<JobListingResponse>

    @PATCH("jobs/{id}/close")
    suspend fun closeJob(@Path("id") id: Long): Response<JobListingResponse>

    @PATCH("jobs/{id}/reactivate")
    suspend fun reactivateJob(@Path("id") id: Long): Response<JobListingResponse>

    @DELETE("jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long): Response<Void>

    // ── Resume ───────────────────────────────────────────────────
    @GET("resume/my")
    suspend fun getMyResume(): Response<ResumeResponse>

    @POST("resume/save")
    suspend fun saveResume(@Body request: SaveResumeRequest): Response<ResumeResponse>

    // Downloads resume as a professionally formatted PDF (no AI needed)
    @GET("resume/download")
    suspend fun downloadMyResume(): Response<ResponseBody>

    // ── Applications ─────────────────────────────────────────────
    @POST("applications")
    suspend fun applyForJob(@Body request: ApplyRequest): Response<ApplicationResponse>

    @GET("applications/my")
    suspend fun getMyApplications(): Response<List<ApplicationResponse>>

    @GET("applications/job/{jobId}")
    suspend fun getJobApplicants(@Path("jobId") jobId: Long): Response<List<ApplicationResponse>>

    @PATCH("applications/{id}/advance")
    suspend fun advanceApplication(@Path("id") id: Long): Response<ApplicationResponse>

    @PATCH("applications/{id}/reject")
    suspend fun rejectApplication(@Path("id") id: Long): Response<ApplicationResponse>

    // ── Notifications ─────────────────────────────────────────────
    @GET("notifications")
    suspend fun getNotifications(): Response<List<NotificationItem>>

    @GET("notifications/count")
    suspend fun getNotificationCount(): Response<NotificationCount>

    @PATCH("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: Long): Response<Void>

    @PATCH("notifications/read-all")
    suspend fun markAllRead(): Response<Void>
}
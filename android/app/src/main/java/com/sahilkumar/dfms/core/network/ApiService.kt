package com.sahilkumar.dfms.core.network

import com.sahilkumar.dfms.model.AuthResponse
import com.sahilkumar.dfms.model.BillResponse
import com.sahilkumar.dfms.model.CowDetailResponse
import com.sahilkumar.dfms.model.CowHealthLogRequest
import com.sahilkumar.dfms.model.CowHealthLogResponse
import com.sahilkumar.dfms.model.CowMilkProductionRequest
import com.sahilkumar.dfms.model.CowMilkProductionResponse
import com.sahilkumar.dfms.model.CowRequest
import com.sahilkumar.dfms.model.CowResponse
import com.sahilkumar.dfms.model.CowSummary
import com.sahilkumar.dfms.model.CreateCustomerLoginRequest
import com.sahilkumar.dfms.model.CustomerRequest
import com.sahilkumar.dfms.model.CustomerResponse
import com.sahilkumar.dfms.model.DashboardSummary
import com.sahilkumar.dfms.model.ExpenseRequest
import com.sahilkumar.dfms.model.ExpenseResponse
import com.sahilkumar.dfms.model.GenerateBillsRequest
import com.sahilkumar.dfms.model.LoginRequest
import com.sahilkumar.dfms.model.MilkEntryRequest
import com.sahilkumar.dfms.model.MilkEntryResponse
import com.sahilkumar.dfms.model.PaymentRequest
import com.sahilkumar.dfms.model.PaymentResponse
import com.sahilkumar.dfms.model.ResetPasswordRequest
import com.sahilkumar.dfms.model.StaffCreateRequest
import com.sahilkumar.dfms.model.StaffUpdateRequest
import com.sahilkumar.dfms.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ---- Auth ----
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun me(): AuthResponse

    // ---- Dashboard ----
    @GET("api/dashboard/summary")
    suspend fun dashboardSummary(): DashboardSummary

    // ---- Customers ----
    @GET("api/customers")
    suspend fun customers(
        @Query("q") q: String?,
        @Query("status") status: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "name,asc",
    ): Page<CustomerResponse>

    @GET("api/customers/{id}")
    suspend fun customer(@Path("id") id: String): CustomerResponse

    @POST("api/customers")
    suspend fun createCustomer(@Body body: CustomerRequest): CustomerResponse

    @PUT("api/customers/{id}")
    suspend fun updateCustomer(@Path("id") id: String, @Body body: CustomerRequest): CustomerResponse

    @DELETE("api/customers/{id}")
    suspend fun stopCustomer(@Path("id") id: String)

    @POST("api/customers/{id}/login")
    suspend fun createCustomerLogin(
        @Path("id") id: String,
        @Body body: CreateCustomerLoginRequest,
    ): CustomerResponse

    @POST("api/customers/{id}/reactivate")
    suspend fun reactivateCustomer(@Path("id") id: String): CustomerResponse

    @DELETE("api/customers/{id}/permanent")
    suspend fun deleteCustomerPermanent(@Path("id") id: String)

    // ---- Milk entries ----
    @GET("api/milk-entries")
    suspend fun milkEntries(
        @Query("customerId") customerId: String?,
        @Query("from") from: String?,
        @Query("to") to: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "entryDate,desc",
    ): Page<MilkEntryResponse>

    @POST("api/milk-entries")
    suspend fun createMilkEntry(@Body body: MilkEntryRequest): MilkEntryResponse

    @PUT("api/milk-entries/{id}")
    suspend fun updateMilkEntry(@Path("id") id: String, @Body body: MilkEntryRequest): MilkEntryResponse

    @DELETE("api/milk-entries/{id}")
    suspend fun deleteMilkEntry(@Path("id") id: String)

    // ---- Expenses ----
    @GET("api/expenses")
    suspend fun expenses(
        @Query("category") category: String?,
        @Query("from") from: String?,
        @Query("to") to: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "expenseDate,desc",
    ): Page<ExpenseResponse>

    @POST("api/expenses")
    suspend fun createExpense(@Body body: ExpenseRequest): ExpenseResponse

    @PUT("api/expenses/{id}")
    suspend fun updateExpense(@Path("id") id: String, @Body body: ExpenseRequest): ExpenseResponse

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String)

    // ---- Bills ----
    @GET("api/bills")
    suspend fun bills(
        @Query("customerId") customerId: String?,
        @Query("status") status: String?,
        @Query("year") year: Int?,
        @Query("month") month: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "generatedAt,desc",
    ): Page<BillResponse>

    @GET("api/bills/{id}")
    suspend fun bill(@Path("id") id: String): BillResponse

    @GET("api/bills/{id}/payments")
    suspend fun billPayments(@Path("id") id: String): List<PaymentResponse>

    @POST("api/bills/{id}/payments")
    suspend fun recordPayment(@Path("id") id: String, @Body body: PaymentRequest): PaymentResponse

    @POST("api/bills/generate")
    suspend fun generateBills(@Body body: GenerateBillsRequest): List<BillResponse>

    // ---- Users / staff ----
    @GET("api/users/staff")
    suspend fun staff(): List<UserResponse>

    @POST("api/users/staff")
    suspend fun createStaff(@Body body: StaffCreateRequest): UserResponse

    @PUT("api/users/{id}")
    suspend fun updateStaff(@Path("id") id: String, @Body body: StaffUpdateRequest): UserResponse

    @POST("api/users/{id}/enable")
    suspend fun enableStaff(@Path("id") id: String)

    @POST("api/users/{id}/disable")
    suspend fun disableStaff(@Path("id") id: String)

    @DELETE("api/users/{id}")
    suspend fun deleteStaff(@Path("id") id: String)

    @POST("api/users/{id}/password")
    suspend fun resetStaffPassword(@Path("id") id: String, @Body body: ResetPasswordRequest)

    // ---- Herd / cows ----
    @GET("api/cows")
    suspend fun cows(
        @Query("q") q: String?,
        @Query("status") status: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "tagNo,asc",
    ): Page<CowResponse>

    @GET("api/cows/summary")
    suspend fun cowSummary(): CowSummary

    @GET("api/cows/{id}")
    suspend fun cow(@Path("id") id: String): CowResponse

    @GET("api/cows/{id}/detail")
    suspend fun cowDetail(@Path("id") id: String): CowDetailResponse

    @POST("api/cows")
    suspend fun createCow(@Body body: CowRequest): CowResponse

    @PUT("api/cows/{id}")
    suspend fun updateCow(@Path("id") id: String, @Body body: CowRequest): CowResponse

    @DELETE("api/cows/{id}")
    suspend fun deleteCow(@Path("id") id: String)

    // ---- Cow health ----
    @GET("api/cow-health")
    suspend fun cowHealth(
        @Query("cowId") cowId: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "eventDate,desc",
    ): Page<CowHealthLogResponse>

    @GET("api/cow-health/upcoming")
    suspend fun upcomingHealth(@Query("daysAhead") daysAhead: Int = 14): List<CowHealthLogResponse>

    @POST("api/cow-health")
    suspend fun createCowHealth(@Body body: CowHealthLogRequest): CowHealthLogResponse

    @DELETE("api/cow-health/{id}")
    suspend fun deleteCowHealth(@Path("id") id: String)

    // ---- Cow production ----
    @GET("api/cow-production")
    suspend fun cowProduction(
        @Query("cowId") cowId: String,
        @Query("from") from: String?,
        @Query("to") to: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "productionDate,desc",
    ): Page<CowMilkProductionResponse>

    @POST("api/cow-production")
    suspend fun createCowProduction(@Body body: CowMilkProductionRequest): CowMilkProductionResponse

    @DELETE("api/cow-production/{id}")
    suspend fun deleteCowProduction(@Path("id") id: String)
}

package com.example.greenbuyapp.data.user

import com.example.greenbuyapp.data.shop.model.Shop
import com.example.greenbuyapp.data.user.model.AddressAddRequest
import com.example.greenbuyapp.data.user.model.AddressDetailResponse
import com.example.greenbuyapp.data.user.model.AddressResponse
import com.example.greenbuyapp.data.user.model.AddressUpdateRequest
import com.example.greenbuyapp.data.user.model.ChangeRoleRequest
import com.example.greenbuyapp.data.user.model.CustomerOrderDetail
import com.example.greenbuyapp.data.user.model.CustomerOrderResponse
import com.example.greenbuyapp.data.user.model.Me
import com.example.greenbuyapp.data.user.model.UpdateUserProfileRequest
import com.example.greenbuyapp.data.user.model.UpdateUserProfileResponse


import com.example.greenbuyapp.data.user.model.User
import com.example.greenbuyapp.data.user.model.UserMe
import com.example.greenbuyapp.data.user.model.UserMeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
//thư viện Retrofit (một thư viện phổ biến trong Android để gọi API RESTful).
// bằng cách ánh xạ các yêu cầu HTTP sang các phương thức Java/Kotlin thông qua chú thích
//Interface này định nghĩa các phương thức để tương tác với một API
// remote data source: sd retrofit để gọi API từ dịch vụ Web(web service), lấy dữ liệu từ máy chủ
interface UserService {
    @GET("api/user/me")
    suspend fun getUserMe(): UserMeResponse

    @GET("api/user/me")
    suspend fun getMe(): UserMe

    @PATCH("api/user/me/change-role")
    suspend fun changeRole(
        @Body request: ChangeRoleRequest
    ): UserMeResponse
    
    @GET("api/order/")
    suspend fun getCustomerOrders(
        @Query("status_filter") statusFilter: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): CustomerOrderResponse
    
    @GET("api/order/{orderId}")
    suspend fun getCustomerOrderDetail(
        @Path("orderId") orderId: Int
    ): CustomerOrderDetail

    /**
     * Cập nhật thông tin hồ sơ người dùng.
     */
    @Multipart
    @PUT("api/user/me")
    suspend fun updateUserProfile(
        @Part avatar: MultipartBody.Part?,
        @Part("first_name") fistName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("phone_number") phone: RequestBody,
        @Part("birth_date") birthDate: RequestBody
    ): UpdateUserProfileResponse

   // Gọi GET request đến endpoint api/addresses/ để lấy danh sách địa chỉ
    @GET("api/addresses/")
    suspend fun getAddresses(): List<AddressResponse>

    @POST("api/addresses/")
    suspend fun addAddress(
        @Body request: AddressAddRequest
    ): AddressResponse

    @GET("api/addresses/{id}")
    suspend fun getAddressDetail(
        @Path("id") addressId: Int
    ): AddressDetailResponse
//    Gửi một PUT request đến api/addresses/{id} với dữ liệu từ AddressUpdateRequest để cập nhật địa chỉ của người dùng.
//    @Body chỉ định dữ liệu yêu cầu được gửi trong body HTTP.
    @PUT("api/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: Int,
        @Body request: AddressUpdateRequest
    ): AddressDetailResponse // trả về
  //  Gọi các phương thức trong interface để thực hiện yêu cầu HTTP, trả về dữ liệu dưới dạng đối tượng Kotlin.


//    Interface này thường được sử dụng trong Remote Data Source của Repository. Repository sẽ:
//    Gọi các phương thức từ UserService.
//    Kết hợp với Local Data Source (Room) để lưu trữ hoặc đồng bộ dữ liệu.
//    Cung cấp dữ liệu cho ViewModel qua  StateFlow.

//    StateFlow là một luồng dữ liệu (data flow) trong thư viện Kotlin Coroutines
//    để quản lý trạng thái (state) trong ứng dụng một cách hiệu quả.
//    duy trì một giá trị trạng thái hiện tại (current state) và phát ra giá trị mới mỗi khi trạng thái thay đổi.
//    Trong ViewModel, StateFlow thường được sử dụng để giữ trạng thái và thông báo cho View (Activity/Fragment) khi có thay đổi.
}

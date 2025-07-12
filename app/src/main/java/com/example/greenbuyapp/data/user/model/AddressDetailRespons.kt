package com.example.greenbuyapp.data.user.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//data class Kotlin đại diện cho response (dữ liệu trả về) từ server khi lấy chi tiết địa chỉ của người dùng
@JsonClass(generateAdapter = true)
//Là class chứa dữ liệu phản hồi từ server (API trả về khi gọi GET /address/{id}
data class AddressDetailResponse(
    val id: Int,
    //ID của người dùng sở hữu địa chỉ
    @Json(name = "user_id") val userId: Int,
    val street: String,
    val city: String,
    val state: String,
    val zipcode: String,
    val country: String,
    @Json(name = "phone_number") val phoneNumber: String,
    // địa chỉ mặc định
    @Json(name = "is_default") val isDefault: Boolean,
    @Json(name = "created_at") val createdAt: String
)


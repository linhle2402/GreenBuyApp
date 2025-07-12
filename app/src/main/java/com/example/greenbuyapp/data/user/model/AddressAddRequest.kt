package com.example.greenbuyapp.data.user.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// đóng gói dữ liệu gửi lên server khi người dùng thêm địa chỉ mới
//sử dụng thư viện Moshi để dễ dàng convert qua lại giữa JSON ↔ Kotlin.
@JsonClass(generateAdapter = true)
//data class trong Kotlin, dùng để chứa dữ liệu.
data class AddressAddRequest(
    val street: String,
    val city: String,
    val state: String,
    val zipcode: String,
    val country: String,
    //map tên trường JSON "phone_number" sang thuộc tính phone trong Kotlin.
    @field:Json(name = "phone_number")
    val phone: String
)
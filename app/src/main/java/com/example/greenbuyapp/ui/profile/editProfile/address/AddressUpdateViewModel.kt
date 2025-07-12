package com.example.greenbuyapp.ui.profile.editProfile.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenbuyapp.data.user.model.AddressDetailResponse
import com.example.greenbuyapp.data.user.model.AddressUpdateRequest
import com.example.greenbuyapp.domain.user.UserRepository
import com.example.greenbuyapp.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


//Lấy thông tin địa chỉ theo ID
//
//Hiển thị lên UI cho người dùng sửa
//
//Cho phép cập nhật thông tin
//
//Nếu người dùng chọn "Đặt làm mặc định", phải huỷ mặc định các địa chỉ khác
//
//Cập nhật UI thông qua StateFlow: loading, thành công, lỗi

//Thành phần	Vai trò
//ViewModel	Lưu trữ và xử lý logic không liên quan đến UI
//viewModelScope	Coroutine chạy theo vòng đời ViewModel
//StateFlow	Dữ liệu có thể quan sát được từ UI
//userRepository	Interface chứa hàm API xử lý dữ liệu người dùng
//Result<T>	Wrapper kết quả trả về từ Repository (Success, Error, NetworkError)

class AddressUpdateViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _address = MutableStateFlow<AddressDetailResponse?>(null)
    val address: StateFlow<AddressDetailResponse?> = _address.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Lấy thông tin địa chỉ theo ID
     */
    fun getAddressById(id: Int) {
        //Bắt đầu loading, xoá lỗi cũ.
        //
        //Gọi userRepository.getAddressById(id).
        //
        //Nếu thành công → gán vào _address (UI sẽ hiển thị).
        //
        //Nếu lỗi → gán thông báo lỗi vào _errorMessage.
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                when (val result = userRepository.getAddressById(id)) {
                    is Result.Success -> {
                        _address.value = result.value
                        println("✅ Lấy địa chỉ thành công: ID $id")
                    }
                    is Result.Error -> {
                        _errorMessage.value = "Lỗi khi lấy địa chỉ: ${result.error}"
                    }
                    is Result.NetworkError -> {
                        _errorMessage.value = "Lỗi mạng khi lấy địa chỉ"
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi không xác định: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cập nhật địa chỉ
     */
    fun updateAddress(id: Int, request: AddressUpdateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false

//Bắt đầu loading, xoá lỗi, reset trạng thái updateSuccess.
//
//Nếu người dùng chọn "Đặt làm mặc định":
//
//Gọi API lấy danh sách tất cả địa chỉ (getListAddress)
//
//Tìm các địa chỉ khác đang là mặc định
//
//Gọi updateAddress từng địa chỉ để huỷ mặc định

            try {
                // Nếu đang đặt địa chỉ này làm mặc định
                if (request.isDefault) {
                    // Bước 1: Lấy danh sách tất cả địa chỉ
                    when (val allAddressesResult = userRepository.getListAddress()) {
                        is Result.Success -> {
                            // Bước 2: Tìm các địa chỉ khác đang là mặc định
                            val otherDefaultAddresses = allAddressesResult.value
                                .filter { it.id != id && it.is_default }
//Sau đó, gọi cập nhật địa chỉ hiện tại:
                            // Bước 3: Cập nhật các địa chỉ khác thành không mặc định
                            for (address in otherDefaultAddresses) {
                                val updateOtherRequest = AddressUpdateRequest(
                                    street = address.street,
                                    city = address.city,
                                    state = address.state,
                                    zipcode = address.zipcode,
                                    country = address.country,
                                    phoneNumber = address.phone,
                                    isDefault = false // Đặt thành không mặc định
                                )

                                // Gọi API cập nhật cho từng địa chỉ
                                userRepository.updateAddress(address.id, updateOtherRequest)
                            }
                        }
                        //Nếu thành công → gán lại _address và _updateSuccess = true.
                        //
                        //Nếu lỗi → cập nhật _errorMessage.
                        is Result.Error -> {
                            _errorMessage.value = "Lỗi khi lấy danh sách địa chỉ: ${allAddressesResult.error}"
                            return@launch
                        }
                        else -> {
                            // Xử lý các trường hợp khác nếu cần
                        }
                    }
                }

                // Bước 4: Cập nhật địa chỉ hiện tại
                when (val result = userRepository.updateAddress(id, request)) {
                    is Result.Success -> {
                        _address.value = result.value
                        _updateSuccess.value = true
                    }
                    is Result.Error -> {
                        _errorMessage.value = "Lỗi khi cập nhật: ${result.error}"
                    }
                    is Result.NetworkError -> {
                        _errorMessage.value = "Lỗi mạng khi cập nhật"
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi không xác định: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Xoá thông báo lỗi (nếu có)
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Gọi lại nếu cần reset trạng thái cập nhật
     */
    fun resetUpdateStatus() {
        _updateSuccess.value = false
    }
}
//Activity gọi getAddressById(id)
//↓
//ViewModel gọi API lấy địa chỉ → UI hiển thị lên form
//
//Người dùng chỉnh sửa → bấm "Lưu"
//↓
//ViewModel kiểm tra nếu chọn "Mặc định":
//→ gọi getListAddress
//→ tìm các địa chỉ khác đang mặc định → gọi update (set false)
//↓
//Gọi updateAddress cho địa chỉ hiện tại
//↓
//Gửi trạng thái updateSuccess / errorMessage về UI

//_address	MutableStateFlow<AddressDetailResponse?>	Dữ liệu địa chỉ hiện tại
//_updateSuccess	MutableStateFlow<Boolean>	Thông báo cập nhật thành công
//_errorMessage	MutableStateFlow<String?>	Thông báo lỗi
//_isLoading	MutableStateFlow<Boolean>	Trạng thái đang tải
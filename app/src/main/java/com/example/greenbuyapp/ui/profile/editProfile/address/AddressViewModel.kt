package com.example.greenbuyapp.ui.profile.editProfile.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenbuyapp.data.user.model.AddressResponse
import com.example.greenbuyapp.domain.user.UserRepository
import com.example.greenbuyapp.ui.profile.AuthState
import com.example.greenbuyapp.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    //Lấy danh sách địa chỉ
    //Lấy tên người dùng
    //Gửi trạng thái tải, lỗi, xác thực, địa chỉ mặc định về cho UI
    // Authentication state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    // danh sách địa chỉ trả về từ API
    private val _addresses = MutableStateFlow<List<AddressResponse>>(emptyList())
    val addresses: StateFlow<List<AddressResponse>> = _addresses.asStateFlow()
    //lấy tên người dùng
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    //gán địa chỉ mặc định
    private val _defaultAddress = MutableStateFlow<AddressResponse?>(null)
    val defaultAddress: StateFlow<AddressResponse?> = _defaultAddress.asStateFlow()
    //Gọi API qua userRepository.getListAddress()
    fun loadAddresses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = userRepository.getListAddress()

                when (result) {
                    is Result.Success -> {
                        _addresses.value = result.value
                        println("✅ Loaded ${result.value.size} addresses")
                    }

                    is Result.Error -> {
                        val msg = "Lỗi khi tải địa chỉ: ${result.error}"
                        _errorMessage.value = msg
                        println("❌ $msg")
                    }

                    is Result.NetworkError -> {
                        val msg = "Lỗi mạng, vui lòng kiểm tra kết nối"
                        _errorMessage.value = msg
                        println("🌐 $msg")
                    }

                    is Result.Loading -> {
                        // Đã xử lý ở bên ngoài
                    }
                }
            } catch (e: Exception) {
                val msg = "Lỗi không xác định: ${e.message}"
                _errorMessage.value = msg
                println("💥 $msg")
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Gọi API userRepository.getUserMe() để lấy thông tin người dùng hiện tại
    fun loadUserInfor() {
        viewModelScope.launch {
            try {
                when (val result = userRepository.getUserMe()) {
                    is Result.Success -> {
                        val user = result.value.user
                        _username.value = user.username ?: ""
                        println("✅ Tên người dùng: ${user.username}")
                        _authState.value = AuthState.Authenticated
                    }

                    is Result.Error -> {
                        println("❌ Không thể lấy thông tin người dùng: ${result.error}")
                    }

                    is Result.NetworkError -> {
                        println("🌐 Lỗi mạng khi tải thông tin người dùng")
                    }

                    is Result.Loading -> {
                        // Đã xử lý
                    }
                }
            } catch (e: Exception) {
                println("💥 Lỗi không xác định khi load user me: ${e.message}")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshAddresses() {
        loadAddresses()
    }
}

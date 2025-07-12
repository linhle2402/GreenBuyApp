package com.example.greenbuyapp.ui.profile.editProfile.address

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.greenbuyapp.data.user.model.AddressUpdateRequest
import com.example.greenbuyapp.databinding.ActivityAddressUpdateBinding
import com.example.greenbuyapp.ui.base.BaseActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

//triển khai màn hình AddressUpdateActivity để cập nhật địa chỉ người dùng.
//Hoạt động này bao gồm việc lấy địa chỉ theo id, hiển thị lên giao diện, cho phép người dùng sửa, và gửi lại dữ liệu mới về server qua ViewModel.

//Lấy addressId từ Intent  ->	Dùng để gọi API lấy địa chỉ tương ứng
//Hiển thị thông tin lên các EditText	-> Để người dùng sửa địa chỉ
//Cập nhật dữ liệu qua ViewModel	-> Sau khi nhấn nút Lưu
//Quan sát các StateFlow để biết thành công / lỗi	-> Cập nhật UI tương ứng

class AddressUpdateActivity : BaseActivity<ActivityAddressUpdateBinding>() {

    override val binding: ActivityAddressUpdateBinding by lazy {
        ActivityAddressUpdateBinding.inflate(layoutInflater)
    }
   // Dùng ViewBinding để truy cập các View từ XML.
    override val viewModel: AddressUpdateViewModel by viewModel()

    private var addressId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)


//        Lấy address_id từ Intent (được gửi từ màn RecyclerView).
//        Nếu tồn tại thì gọi ViewModel để lấy dữ liệu địa chỉ từ API

        addressId = intent.getIntExtra("address_id", -1)
        if (addressId != -1) {
            viewModel.getAddressById(addressId)
        }
// xử lí người dùng khi nhấn nút lưu
        binding.btnSaveAddress.setOnClickListener {
            updateAddress()
        }
        initViews()
        setViewModel()
    }
    override fun initViews() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setViewModel() {
        lifecycleScope.launch {
            // Quan sát dữ liệu địa chỉ
            // hiện thị địa chỉ lên giao diện
            launch {
                //address là StateFlow từ ViewModel (chứa dữ liệu từ API).
                //
                //Gán dữ liệu vào các EditText và Switch tương ứng.
                viewModel.address.collectLatest { address ->
                    address?.let {
                        binding.edtPhone.setText(it.phoneNumber)
                        binding.edtStreet.setText(it.street)
                        binding.edtCity.setText(it.city)
                        binding.edtState.setText(it.state)
                        binding.edtZipcode.setText(it.zipcode)
                        binding.edtCountry.setText(it.country)
                        binding.switchDefault.isChecked = it.isDefault == true
                    }
                }
            }

            // Quan sát khi cập nhật thành công
            launch {
                // quan sát trạng thái ViewModel
                //Khi cập nhật thành công → hiện thông báo → đóng màn hình.
                viewModel.updateSuccess.collectLatest { success ->
                    if (success) {
                        showToast("✅ Cập nhật địa chỉ thành công")
                        finish()
                    }
                }
            }

            // Quan sát khi có lỗi
            launch {
                viewModel.errorMessage.collectLatest { msg ->
                    msg?.let {
                        showToast("❌ $it")
                        viewModel.clearError()
                        //Khi có lỗi → hiện lỗi và reset trạng thái lỗi trong ViewModel.
                    }
                }
            }
        }
    }

    private fun updateAddress() {
        val street = binding.edtStreet.text.toString().trim()
        val city = binding.edtCity.text.toString().trim()
        val state = binding.edtState.text.toString().trim()
        val zipcode = binding.edtZipcode.text.toString().trim()
        val country = binding.edtCountry.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()

        if (!validateInput(street, city, state, zipcode, country, phone)) return

        val request = AddressUpdateRequest(
            street = street,
            city = city,
            state = state,
            zipcode = zipcode,
            country = country,
            phoneNumber = phone,
            isDefault = binding.switchDefault.isChecked
        )
        viewModel.updateAddress(addressId, request)
    }

    private fun validateInput(
        street: String,
        city: String,
        state: String,
        zipcode: String,
        country: String,
        phone: String
    ): Boolean {
        if (street.isEmpty() || city.isEmpty() || state.isEmpty() ||
            zipcode.isEmpty() || country.isEmpty() || phone.isEmpty()
        ) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return false
        }

        // Kiểm tra số điện thoại chỉ chứa số
        if (!phone.matches(Regex("^\\d{8,15}$"))) {
            Toast.makeText(this, "Số điện thoại không hợp lệ (chỉ nhập số, tối thiểu 8 số)", Toast.LENGTH_SHORT).show()
            return false
        }

        // Kiểm tra country chỉ chứa chữ cái (kể cả tiếng Việt có dấu)
        if (!country.matches(Regex("^[\\p{L} ]+$"))) {
            Toast.makeText(this, "Quốc gia chỉ được nhập chữ cái", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
//Intent.putExtra("address_id", 123)
//↓
//AddressUpdateActivity nhận id
//↓
//ViewModel.getAddressById(id)
//↓
//API trả về Address → hiển thị lên UI
//↓
//Người dùng sửa → nhấn Lưu
//↓
//Gọi ViewModel.updateAddress(id, data)
//↓
//Nếu thành công → Toast + finish()
//Nếu lỗi → Toast lỗi

//AddressUpdateActivity ->	Giao diện nhập/sửa địa chỉ
//ViewBinding	-> Truy cập các view XML
//ViewModel (AddressUpdateViewModel)	-> Xử lý lấy và cập nhật dữ liệu
//StateFlow	-> Truyền dữ liệu trạng thái về UI
//AddressUpdateRequest	-> Đối tượng chứa dữ liệu gửi về API
//lifecycleScope.launch -> 	Quan sát dữ liệu an toàn theo vòng đời

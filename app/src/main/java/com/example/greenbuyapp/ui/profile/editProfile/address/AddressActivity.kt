package com.example.greenbuyapp.ui.profile.editProfile.address

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenbuyapp.databinding.ActivityAddressBinding
import com.example.greenbuyapp.ui.base.BaseActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
//AddressActivity kế thừa BaseActivity, sử dụng ViewBinding (ActivityAddressBinding) để thao tác layout.
//Là màn hình hiển thị danh sách địa chỉ của người dùng.

// hàm viết chung để các activity sd lại
class AddressActivity : BaseActivity<ActivityAddressBinding>() {
    // Khởi tạo viewmodel
    // bieen : kieu dl
    override val viewModel: AddressViewModel by viewModel()
//    Lớp được tạo tự động bởi View Binding, tương ứng với tệp layout XML có tên activity_address.xml.
//    Lớp này chứa các tham chiếu trực tiếp đến các view trong layout (như textView, button, v.v.).
    override val binding: ActivityAddressBinding by lazy {
//    Lớp được tạo tự động bởi View Binding, tương ứng với tệp layout XML có tên activity_address.xml.
//    Lớp này chứa các tham chiếu trực tiếp đến các view trong layout (như textView, button, v.v.).
        //Sử dụng lazy initialization, nghĩa là binding chỉ được khởi tạo khi nó được truy cập lần đầu tiên
        ActivityAddressBinding.inflate(layoutInflater)
   // Tạo instance của ActivityAddressBinding bằng cách inflate (phân tích và nạp) layout activity_address.xml từ layoutInflater (cung cấp bởi Activity).
    }
    private lateinit var adapter: AddressAdapter
    // Khởi tạo hoạt động chính
    //Là phương thức vòng đời đầu tiên của Activity, được gọi khi Activity được tạo.

    override fun onCreate(savedInstanceState: Bundle?) {
        // gán layout cho Activity bằng ViewBinding
        //Gắn root view của layout (được tạo bởi View Binding) vào Activity để hiển thị giao diện.
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        // Gọi viewModel để lấy thông tin người dùng
        viewModel.loadUserInfor()
        // Khởi tạo RecyclerView - một thành phần hiển thị danh sách cuộn được và Adapter
        initRecyclerView()
        // Lắng nghe dữ liệu từ ViewModel để hiện thị
        observeData()
        // Gọi lấy danh sách địa chỉ từ API or DB
        viewModel.loadAddresses()

//        View Binding được thiết kế để đơn giản hóa và an toàn hóa việc truy cập các view trong layout XML của ứng dụng Android.
//        Nó tự động tạo các lớp binding từ các tệp layout, cho phép bạn tương tác với các thành phần giao diện
    }
//Khi màn hình quay lại (từ màn hình thêm/sửa địa chỉ), nó sẽ tải lại dữ liệu để cập nhật UI.
    override fun onResume() {
        super.onResume()
        viewModel.loadUserInfor()
        viewModel.loadAddresses()
    }
// thiết lập các tương tác UI
    override fun initViews() {
        //Nhấn nút quay lại toolbar → thoát khỏi màn hình.
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    //Nhấn nút Thêm địa chỉ → chuyển sang AddressAddActivity.
        binding.btnAddAddresss.setOnClickListener {
            val intent = Intent(this, AddressAddActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initRecyclerView() {
        //Gán AddressAdapter vào RecyclerView.
        adapter = AddressAdapter()
        binding.recyclerViewAddress.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAddress.adapter = adapter
        // Xử lý khi giữ vào 1 item
        //mở màn hình sửa địa chỉ (AddressUpdateActivity) và truyền addressId qua Intent.
        adapter.onItemLongClick = { addressId ->
            val intent = Intent(this, AddressUpdateActivity::class.java)
            intent.putExtra("address_id", addressId)
            startActivity(intent)
        }
    }
    // nhận dữ liệu từ viewmodel
    // Sử dụng Coroutine + StateFlow để lắng nghe addresses từ ViewModel.
//    Khi dữ liệu thay đổi, gọi adapter.submitListWithName(...) để hiển thị danh sách mới.
//    Kèm theo tên người dùng từ viewModel.username.

    private fun observeData() {
//        lifecycleScope.launch: Khởi chạy một coroutine được ràng buộc với vòng đời của Activity/Fragment.
//        Coroutine này sẽ tự động hủy khi Activity/Fragment bị destroy
        //Thu thập luồng dữ liệu từ addresses (thường là một StateFlow ) trong ViewModel.
        // collectLatest đảm bảo chỉ xử lý giá trị mới nhất nếu có nhiều cập nhật liên tiếp.
        lifecycleScope.launch {
            launch {
                viewModel.addresses.collectLatest { addressList ->
                    val fullName = "${viewModel.username.value.orEmpty()}".trim()
                    adapter.submitListWithName(addressList, fullName)
                }
            }
        }
    }
//    viewModel.addresses:
//    Giả sử là một StateFlow<List<Address>> hoặc Flow<List<Address>> trong ViewModel, phát ra danh sách địa chỉ từ Repository (như getAddresses() trong UserRepository).
//    viewModel.username:
//    Có thể là  StateFlow<String>, chứa tên người dùng (từ getUserMe()).
//    .value.orEmpty(): Lấy giá trị hiện tại, trả về chuỗi rỗng nếu null.
}
//Khi mở màn hình ->	Gọi API lấy tên người dùng và danh sách địa chỉ
//Khi có dữ liệu ->	Lắng nghe qua stateFlow và cập nhật giao diện
//Nhấn giữ vào 1 item -> Mở màn hình sửa địa chỉ kèm theo ID
//Nhấn thêm địa chỉ	-> Mở màn hình thêm mới địa chỉ
//Quay lại màn hình	-> Tải lại dữ liệu (ở onResume)
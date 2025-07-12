package com.example.greenbuyapp.ui.profile.editProfile.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.greenbuyapp.data.user.model.AddressResponse
import com.example.greenbuyapp.databinding.ItemAddressBinding

//một RecyclerView.Adapter hiện đại sử dụng ListAdapter, dùng để hiển thị danh sách địa chỉ của người dùng trong app.
class AddressAdapter :
// Kế thừa từ ListAdapter: hỗ trợ tự động cập nhật danh sách khi dữ liệu thay đổi nhờ DiffUtil.
//AddressResponse: là kiểu dữ liệu từng item.
//AddressDiffCallback(): dùng để xác định sự thay đổi giữa 2 item.
    ListAdapter<AddressResponse, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {
     //userFullName: tên người dùng sẽ hiển thị cho từng địa chỉ.
        // onItemLongClick: callback khi nhấn vào 1 item – truyền addressId về activity.
    private var userFullName: String = ""
    var onItemLongClick: ((Int) -> Unit)? = null
// Giúp hiển thị tên trên từng item.
//Thay vì chỉ gọi submitList(list), ta lưu lại tên người dùng, rồi truyền cả tên và danh sách cho adapter.
    fun submitListWithName(list: List<AddressResponse>, name: String) {
        userFullName = name
        submitList(list)
    }
    //Tạo ViewHolder từ ItemAddressBinding – là layout XML cho từng dòng trong danh sách địa chỉ.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }
//Gọi bind() để hiển thị dữ liệu lên giao diện.
//Khi click vào item, sẽ:
//Lấy id của địa chỉ
//Gọi callback onItemLongClick?.invoke(addressId) → để activity xử lý (mở màn hình sửa)
    //onItemClick
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position), userFullName)
        holder.itemView.setOnLongClickListener() {
            val addressId = getItem(holder.adapterPosition).id
            onItemLongClick?.invoke(addressId)
            true
        }
    }
    //xử lý từng item
    //Là một lớp nội bộ (inner class) trong Adapter, cho phép truy cập trực tiếp vào các thành phần của Adapter (như dữ liệu)
    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) { //Kế thừa từ RecyclerView.ViewHolder, lớp cơ bản để giữ tham chiếu đến view của item trong RecyclerView.
        fun bind(address: AddressResponse, fullName: String) {
//            binding.tvid.text = address.id.toString()
            binding.tvName.text = fullName
            binding.tvStreet.text = address.street
            binding.tvWard.text = "${address.city}, ${address.state}, ${address.zipcode}"
            binding.tvPhone.text = address.phone
            if(address.is_default) {
                binding.tvDefault.text = "Mặc định"
                binding.tvDefault.visibility = View.VISIBLE
            }else{
                binding.tvDefault.visibility = View.GONE
            }
        }
    }

//    định nghĩa của một inner class AddressViewHolder trong một RecyclerView.Adapter,
//    được sử dụng để hiển thị các mục (items) trong danh sách địa chỉ.
//    Class này sử dụng View Binding để ánh xạ layout cho mỗi item (tệp item_address.xml)
//    và cung cấp phương thức bind để cập nhật dữ liệu vào các view.
//    Sử dụng View Binding để ánh xạ layout item_address.xml.
//    binding chứa các tham chiếu đến các view trong layout

// Kiểm tra thay đổi dữ liệu
    //dùng DiffUtil.ItemCallback để tối ưu cập nhật dữ liệu trong RecyclerView, đây là cách rất chuẩn khi dùng với ListAdapter
    class AddressDiffCallback : DiffUtil.ItemCallback<AddressResponse>() {
        //areItemsTheSame: kiểm tra 2 địa chỉ có cùng ID không.

        //areContentsTheSame: kiểm tra toàn bộ dữ liệu giống nhau không.
        override fun areItemsTheSame(oldItem: AddressResponse, newItem: AddressResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AddressResponse, newItem: AddressResponse) =
            oldItem == newItem
    }
}

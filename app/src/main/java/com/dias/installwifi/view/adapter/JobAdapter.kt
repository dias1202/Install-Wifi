import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.data.model.Order
import com.dias.installwifi.databinding.ItemJobBinding
import com.dias.installwifi.utils.FormatterUtils.formatDate
import com.dias.installwifi.utils.FormatterUtils.formatPrice

class JobAdapter() : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    private var jobList: List<Order> = listOf()

    class JobViewHolder(val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.binding.apply {
            tvOrderNumber.text = job.id
            tvProductName.text = job.packageId
            tvCustomerName.text = job.userId
            tvAddress.text = job.address
            tvOrderDate.text = formatDate(job.orderDate)
            tvTotalPrice.text = formatPrice(job.totalPrice)
            tvStatus.text = job.status
        }
    }

    override fun getItemCount(): Int = jobList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Order>) {
        jobList = newData
        notifyDataSetChanged()
    }
}
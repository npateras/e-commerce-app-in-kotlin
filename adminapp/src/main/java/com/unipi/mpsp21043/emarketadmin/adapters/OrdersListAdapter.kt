package com.unipi.mpsp21043.emarketadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.databinding.ItemOrderBinding
import com.unipi.mpsp21043.emarketadmin.models.Order
import com.unipi.mpsp21043.emarketadmin.models.Product
import com.unipi.mpsp21043.emarketadmin.utils.Constants
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils
import java.util.*
import kotlin.collections.ArrayList


/**
 * A adapter class for categories list items.
 */
open class OrdersListAdapter(
    private val context: Context,
    private var list: ArrayList<Order>
) : RecyclerView.Adapter<OrdersListAdapter.OrdersViewHolder>(), Filterable {

    var listFiltered: ArrayList<Order> = list

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ProductsViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    open fun setList(context: Context?, ordersList: ArrayList<Order>){
        val  result: DiffUtil.DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun getOldListSize(): Int{
                return this@OrdersListAdapter.list.size
            }

            override fun getNewListSize(): Int{
                return list.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean{
                return this@OrdersListAdapter.list[oldItemPosition].title === list[newItemPosition].title
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean{
                val newOrder: Order = this@OrdersListAdapter.list[oldItemPosition]
                val oldOrder: Order = list[newItemPosition]
                return newOrder.title === oldOrder.title
            }
        })
        this.listFiltered = list
        result.dispatchUpdatesTo(this)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val model = list[position]

        holder.binding.apply {
            txtViewName.text = model.title
            textViewTotalAmountValue.text = String.format(
                context.getString(R.string.txt_format_price),
                Constants.DEFAULT_CURRENCY,
                model.totalAmount
            )
            textViewClientValue.text = model.address.fullName
            txtViewDate.text = Constants.standardSimpleDateFormat.format(model.orderDate)
            when (model.orderStatus) {
                0 -> {
                    textViewOrderStatusValue.text = context.getString(R.string.txt_pending)
                    textViewOrderStatusValue.setTextColor(context.getColor(R.color.colorRed))
                }
                1 -> {
                    textViewOrderStatusValue.text = context.getString(R.string.txt_processing)
                    textViewOrderStatusValue.setTextColor(context.getColor(R.color.colorYellowOrange))
                }
                2 -> {
                    textViewOrderStatusValue.text = context.getString(R.string.txt_completed)
                    textViewOrderStatusValue.setTextColor(context.getColor(R.color.colorGreen))
                }
            }

            cardViewMain.setOnClickListener { IntentUtils().goToOrderDetailsActivity(context, model) }
        }

        /*holder.itemView.setOnClickListener {
            IntentUtils().goToOrderDetailsActivity(context, model)
        }*/
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return listFiltered.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class OrdersViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString: String = constraint.toString()
                listFiltered = if (charString.isEmpty()) {
                    list
                } else {
                    val filteredList: ArrayList<Order> = ArrayList()
                    for (item in list) {
                        if (item.title.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        if (item.orderDate.toString().lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        if (item.address.fullName.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        if (item.address.phoneNumber.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        if (item.address.zipCode.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        if (item.paymentMethod.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        if (item.id.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        if (item.userId.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        if (item.totalAmount.toString().lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        for (product in item.cartItems) {
                            if (product.name.lowercase()
                                   .contains(charString.lowercase(Locale.getDefault()))
                            ) {
                                filteredList.add(item)
                            }
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = listFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                listFiltered = results?.values as ArrayList<Order>

                notifyDataSetChanged()
            }
        }
    }

}

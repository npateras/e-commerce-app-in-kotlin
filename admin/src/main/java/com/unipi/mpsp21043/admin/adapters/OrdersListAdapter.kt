package com.unipi.mpsp21043.admin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.admin.R
import com.unipi.mpsp21043.admin.databinding.ItemOrderBinding
import com.unipi.mpsp21043.admin.models.Order
import com.unipi.mpsp21043.admin.utils.Constants
import com.unipi.mpsp21043.admin.utils.IntentUtils
import java.util.*


/**
 * A adapter class for categories list items.
 */
open class OrdersListAdapter(
    private val context: Context,
    list: ArrayList<Order>
) : RecyclerView.Adapter<OrdersListAdapter.OrdersViewHolder>(), Filterable {

    var ordersList: ArrayList<Order> = ArrayList()
    var ordersListAll: ArrayList<Order> = ArrayList()

    init {
        this.ordersList = list
        ordersListAll = ArrayList()
        ordersListAll.addAll(ordersList)
    }

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link OrdersViewHolder} and initializes some private fields to be used by RecyclerView.
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
        val model = ordersList[position]

        holder.binding.apply {
            txtViewName.text = model.title
            textViewTotalAmountValue.text = String.format(
                context.getString(R.string.text_format_price),
                Constants.DEFAULT_CURRENCY,
                model.totalAmount
            )
            textViewClientValue.text = model.address.fullName
            txtViewDate.text = Constants.standardSimpleDateFormat.format(model.orderDate)
            when (model.orderStatus) {
                0 -> {
                    textViewOrderStatusValue.text = context.getString(R.string.text_pending)
                    textViewOrderStatusValue.setTextColor(context.getColor(R.color.colorRed))
                }
                1 -> {
                    textViewOrderStatusValue.text = context.getString(R.string.text_processing)
                    textViewOrderStatusValue.setTextColor(context.getColor(R.color.colorYellowOrange))
                }
                2 -> {
                    textViewOrderStatusValue.text = context.getString(R.string.text_completed)
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
        return ordersList.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class OrdersViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return searchFilter
    }

    private var searchFilter: Filter = object : Filter() {
        //Automatic on background thread
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filteredList: ArrayList<Order> = ArrayList()
            if (charSequence.isEmpty()) {
                filteredList.addAll(ordersListAll)
            }
            else {
                val text = charSequence.toString().lowercase(Locale.getDefault())
                for (item in ordersListAll) {
                    if ((item.userId.lowercase(Locale.getDefault()).contains(text))
                        || (item.title.lowercase(Locale.getDefault()).contains(text))
                        || (item.address.fullName.lowercase(Locale.getDefault()).contains(text))
                        || (item.address.phoneNumber.lowercase(Locale.getDefault()).contains(text))
                        || (item.address.zipCode.lowercase(Locale.getDefault()).contains(text))
                        || (item.paymentMethod.lowercase(Locale.getDefault()).contains(text))
                        || (item.orderDate.toString().lowercase(Locale.getDefault()).contains(text))
                        || (item.id.lowercase(Locale.getDefault()).contains(text))
                    ) {
                        filteredList.add(item)
                    }
                    else for (product in item.cartItems) {
                        if (product.name.lowercase()
                                .contains(text)
                        ) {
                            filteredList.add(item)
                        }
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        //Automatic on UI thread
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            ordersList.clear()
            ordersList.addAll(filterResults.values as Collection<Order>)
            notifyDataSetChanged()
        }
    }

}

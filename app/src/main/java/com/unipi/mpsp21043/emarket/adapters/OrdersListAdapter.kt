package com.unipi.mpsp21043.emarket.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.databinding.ItemOrderBinding
import com.unipi.mpsp21043.emarket.models.Order
import com.unipi.mpsp21043.emarket.utils.Constants
import com.unipi.mpsp21043.emarket.utils.IntentUtils


/**
 * A adapter class for categories list items.
 */
open class OrdersListAdapter(
    private val context: Context,
    private var list: ArrayList<Order>
) : RecyclerView.Adapter<OrdersListAdapter.OrdersViewHolder>() {

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
            txtViewPriceValue.text = model.totalAmount
            txtViewDate.text = Constants.standardSimpleDateFormat.format(model.orderDate)
            when (model.orderStatus) {
                0 -> {
                    txtViewStatusValue.text = context.getString(R.string.txt_pending)
                    txtViewStatusValue.setTextColor(context.getColor(R.color.colorSecondary2))
                }
                1 -> {
                    txtViewStatusValue.text = context.getString(R.string.txt_processing)
                    txtViewStatusValue.setTextColor(context.getColor(R.color.colorYellowOrange))
                }
                2 -> {
                    txtViewStatusValue.text = context.getString(R.string.txt_completed)
                    txtViewStatusValue.setTextColor(context.getColor(R.color.colorPrimary))
                }
            }
        }
        holder.itemView.setOnClickListener {
            IntentUtils().goToOrderDetailsActivity(context, model)
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class OrdersViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)
}

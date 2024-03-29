package com.unipi.mpsp21043.client.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.databinding.ItemOrderBinding
import com.unipi.mpsp21043.client.models.Order
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.IntentUtils


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
            textViewOrderSerialNumber.text = model.title
            textViewTotalAmountValue.text =
                String.format(
                    context.getString(R.string.text_format_price),
                    Constants.DEFAULT_CURRENCY,
                    model.totalAmount
                )
            textViewDate.text = Constants.DATE_FORMAT.format(model.orderDate)
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
                    textViewOrderStatusValue.setTextColor(context.getColor(R.color.colorPrimary))
                }
            }

            cardViewMain.setOnClickListener { IntentUtils().goToOrderDetailsActivity(context, model) }
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

package com.unipi.p17172.emarket.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.databinding.RecyclerItemProductBinding
import com.unipi.p17172.emarket.models.Product

/**
 * A adapter class for products list items.
 */
open class ProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private val fragment: Fragment
) : RecyclerView.Adapter<ProductsListAdapter.ProductsViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ProductsViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view: RecyclerItemProductBinding =
            RecyclerItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductsViewHolder(view)
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
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val model = list[position]

        Log.e("test", model.price.toString())

        holder.binding.apply {
            txtViewName.text = model.name
            txtViewPrice.text = String.format(
                context.getString(R.string.txt_format_price),
                model.price
            )
            holder.binding.txtViewWeight.text = String.format(
                context.getString(R.string.txt_format_weight),
                model.weight,
                model.weightUnit
            )
        }
    }

    /* fun addProfiles(products: List<Product>) {
        this.model.addAll(profiles)
        notifyDataSetChanged()
    }*/

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ProductsViewHolder(val binding: RecyclerItemProductBinding) : RecyclerView.ViewHolder(binding.root)
}
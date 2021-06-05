package com.unipi.p17172.emarket.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.databinding.RecyclerItemProductWideFavoritesBinding
import com.unipi.p17172.emarket.models.Favorite
import com.unipi.p17172.emarket.ui.activities.ProductDetailsActivity
import com.unipi.p17172.emarket.utils.Constants
import com.unipi.p17172.emarket.utils.GlideLoader


/**
 * A adapter class for products list items.
 */
open class FavoritesListAdapter(
    private val context: Context,
    private var list: ArrayList<Favorite>
) : RecyclerView.Adapter<FavoritesListAdapter.FavoritesViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ProductsViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(
            RecyclerItemProductWideFavoritesBinding.inflate(
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
    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val model = list[position]

        holder.binding.apply {
            GlideLoader(context).loadProductPictureWide(model.iconUrl, imgViewProduct)
            txtViewName.text = model.name
            txtViewPrice.text = String.format(
                context.getString(R.string.txt_format_price),
                context.getString(R.string.curr_eur),
                model.price
            )
        }
        holder.itemView.setOnClickListener {
            // Launch Product details screen.
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.id)
            intent.putExtra(Constants.EXTRA_IS_IN_FAVORITES, true) // true : since we are already in favorites.
            context.startActivity(intent)
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
    class FavoritesViewHolder(val binding: RecyclerItemProductWideFavoritesBinding) : RecyclerView.ViewHolder(binding.root)
}
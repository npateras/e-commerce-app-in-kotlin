package com.unipi.p17172.emarket.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.databinding.ItemFavoriteProductBinding
import com.unipi.p17172.emarket.models.Favorite
import com.unipi.p17172.emarket.utils.GlideLoader
import com.unipi.p17172.emarket.utils.IntentUtils


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
            ItemFavoriteProductBinding.inflate(
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
        var priceReduced = 0.00

        holder.binding.apply {
            GlideLoader(context).loadProductPictureWide(model.imgUrl, imgViewProduct)
            txtViewName.text = model.name
            if (model.sale != 0.0) {
                txtViewPriceReduced.apply {
                    visibility = View.VISIBLE
                    foreground = AppCompatResources.getDrawable(context, R.drawable.striking_red_text)
                    text = String.format(
                        context.getString(R.string.txt_format_price),
                        context.getString(R.string.curr_eur),
                        model.price
                    )
                }
                priceReduced = model.price - (model.price * model.sale)
            }
            txtViewPrice.text = String.format(
                context.getString(R.string.txt_format_price),
                context.getString(R.string.curr_eur),
                priceReduced
            )
        }
        holder.itemView.setOnClickListener { IntentUtils().goToProductDetailsActivity(context, model.productId) } // true : since we are already in favorites.
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
    class FavoritesViewHolder(val binding: ItemFavoriteProductBinding) : RecyclerView.ViewHolder(binding.root)
}

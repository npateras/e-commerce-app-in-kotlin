package com.unipi.p17172.emarket.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.database.FirestoreHelper.IsFavoriteCallback
import com.unipi.p17172.emarket.databinding.ItemProductBinding
import com.unipi.p17172.emarket.models.Favorite
import com.unipi.p17172.emarket.models.Product
import com.unipi.p17172.emarket.utils.GlideLoader
import com.unipi.p17172.emarket.utils.IntentUtils


/**
 * A adapter class for products list items.
 */
open class ProductsListActivityAdapter(
    private val activity: Activity,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<ProductsListActivityAdapter.ProductsViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ProductsViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
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
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val model = list[position]
        var isInFavorites = false
        var priceReduced = 0.00

        holder.binding.apply {
            // Check if user has this item in their favorites
            FirestoreHelper().isFavorite(model.id, object : IsFavoriteCallback {
                override fun onCallback(isFavorite: Boolean) {
                    if (isFavorite) {
                        checkboxFavorite.isChecked = true
                        isInFavorites = true
                    }
                }
            })

            GlideLoader(activity).loadProductPictureWide(model.iconUrl, imgViewIcon)
            txtViewName.text = model.name
            if (model.sale != 0f) {
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
            txtViewPrice.apply {
                text = String.format(
                    context.getString(R.string.txt_format_price),
                    context.getString(R.string.curr_eur),
                    priceReduced
                )
            }

            txtViewWeight.text = String.format(
                activity.getString(R.string.txt_format_weight),
                model.weight,
                model.weightUnit
            )

            checkboxFavorite.setOnClickListener {
                if (isInFavorites)
                    FirestoreHelper().deleteFavoriteProduct(activity, model.id)
                else {
                    val favorite = Favorite(
                        FirestoreHelper().getCurrentUserID(),
                        model.id,
                        model.iconUrl,
                        model.name,
                        model.price,
                        model.sale
                    )
                    FirestoreHelper().addToFavorites(activity, favorite)
                }
            }
        }
        holder.itemView.setOnClickListener { IntentUtils().goToProductDetailsActivity(activity, model.id, isInFavorites) }
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
    class ProductsViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)
}

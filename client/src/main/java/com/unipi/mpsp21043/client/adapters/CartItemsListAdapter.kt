package com.unipi.mpsp21043.client.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityListCartItemsBinding
import com.unipi.mpsp21043.client.databinding.ItemCartProductBinding
import com.unipi.mpsp21043.client.models.Cart
import com.unipi.mpsp21043.client.ui.activities.ListCartItemsActivity
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.GlideLoader
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.snackBarErrorClass


/**
 * A adapter class for products list items.
 */
open class CartItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Cart>,
    private val bindingListCartItemsActivity: ActivityListCartItemsBinding
) : RecyclerView.Adapter<CartItemsListAdapter.CartItemsViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ProductsViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsViewHolder {
        return CartItemsViewHolder(
            ItemCartProductBinding.inflate(
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
    override fun onBindViewHolder(holder: CartItemsViewHolder, position: Int) {
        val model = list[position]
        var priceReduced = model.price

        holder.binding.apply {
            // Product img
            GlideLoader(context).loadProductPictureWide(model.imgUrl, imageViewIcon)
            // Product name
            txtViewName.text = model.name
            // Check if product has off sale
            if (model.sale != 0.0)
                priceReduced = model.price - (model.price * model.sale)
            // Product final price
            txtViewPrice.apply {
                text = String.format(
                    context.getString(R.string.text_format_price),
                    context.getString(R.string.curr_eur),
                    priceReduced
                )
            }

            // Cart product quantity
            txtViewQuantityValue.text = model.cartQuantity.toString()

            // Increase product quantity
            imgBtnPlus.setOnClickListener {

                val cartQuantity: Int = model.cartQuantity

                if (cartQuantity <= model.stock) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.FIELD_CART_QUANTITY] = cartQuantity + 1

                    FirestoreHelper().updateMyCart(context, model.id, itemHashMap)
                }
                else {
                    if (context is ListCartItemsActivity) {
                        snackBarErrorClass(bindingListCartItemsActivity.root,
                            context.getString(R.string.text_error_max_stock),
                            bindingListCartItemsActivity.constraintLayoutAddToCart
                        )
                    }
                }
            }

            // Decrease product quantity
            imgBtnMinus.setOnClickListener {

                if (model.cartQuantity == 1)
                    FirestoreHelper().deleteItemFromCart(context, model.productId)
                else {
                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.FIELD_CART_QUANTITY] = model.cartQuantity - 1

                    FirestoreHelper().updateMyCart(context, model.id, itemHashMap)
                }
            }

            holder.itemView.setOnClickListener {
                IntentUtils().goToProductDetailsActivity(context, model.productId)
            }
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
    class CartItemsViewHolder(val binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root)
}

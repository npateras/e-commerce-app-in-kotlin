package com.unipi.p17172.emarket.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.ItemCartProductBinding
import com.unipi.p17172.emarket.models.Cart
import com.unipi.p17172.emarket.ui.activities.ListCartItemsActivity
import com.unipi.p17172.emarket.utils.Constants
import com.unipi.p17172.emarket.utils.GlideLoader


/**
 * A adapter class for products list items.
 */
open class CartItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Cart>,
    private val updateCartItems: Boolean
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
            GlideLoader(context).loadProductPictureWide(model.imgUrl, imgViewProduct)
            // Product name
            txtViewName.text = model.name
            // Check if product has off sale
            if (model.sale != 0.0) {
                txtViewPriceReduced.apply {
                    visibility = View.VISIBLE
                    foreground =
                        AppCompatResources.getDrawable(context, R.drawable.striking_red_text)
                    text = String.format(
                        context.getString(R.string.txt_format_price),
                        context.getString(R.string.curr_eur),
                        model.price
                    )
                }
                priceReduced = model.price - (model.price * model.sale)
            }
            // Product final price
            txtViewPrice.apply {
                text = String.format(
                    context.getString(R.string.txt_format_price),
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

                    // Show the progress dialog.
                    if (context is ListCartItemsActivity) {
                        context.showProgressDialog()
                    }

                    FirestoreHelper().updateMyCart(context, model.id, itemHashMap)
                }
                else {
                    if (context is ListCartItemsActivity) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.txt_error_max_stock),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            // Decrease product quantity
            imgBtnMinus.setOnClickListener {

                if (context is ListCartItemsActivity) {
                    context.showProgressDialog()
                }

                if (model.cartQuantity == 1)
                    FirestoreHelper().deleteItemFromCart(context, model.productId)
                else {
                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.FIELD_CART_QUANTITY] = model.cartQuantity - 1

                    // Show the progress dialog.
                    if (context is ListCartItemsActivity) {
                        context.showProgressDialog()
                    }

                    FirestoreHelper().updateMyCart(context, model.id, itemHashMap)
                }
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

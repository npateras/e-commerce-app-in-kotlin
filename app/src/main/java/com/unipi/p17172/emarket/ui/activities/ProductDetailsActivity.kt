package com.unipi.p17172.emarket.ui.activities

import android.os.Bundle
import android.view.View
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.ActivityProductDetailsBinding
import com.unipi.p17172.emarket.models.Cart
import com.unipi.p17172.emarket.models.Product
import com.unipi.p17172.emarket.utils.Constants
import com.unipi.p17172.emarket.utils.GlideLoader
import com.unipi.p17172.emarket.utils.SnackBarErrorClass
import java.util.*

class ProductDetailsActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * @see modelProduct
     * @see productId A global variable for product id.
     * */
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var modelProduct: Product
    private lateinit var modelCart: Cart

    private var productId: String = ""
    private var isInFavorites: Boolean = false
    private var cartId: String = ""
    private val userId = FirestoreHelper().getCurrentUserID()

    private var cartProductQuantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
        setupUI()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            productId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        if (intent.hasExtra(Constants.EXTRA_IS_IN_FAVORITES)) {
            isInFavorites =
                intent.extras?.getBoolean(Constants.EXTRA_IS_IN_FAVORITES)!!
        }

    }

    private fun setupUI() {
        binding.apply {
            vLayoutHead.veil()
            vLayoutBody.veil()
        }

        setUpActionBar()
        setupClickListeners()
        getProductDetails()
    }

    private fun setupClickListeners() {
        if (userId != "")
            binding.apply {
                btnAddToCart.setOnClickListener { updateCart() }
                btnRemoveFromCart.setOnClickListener { removeFromCart() }
                imgBtnPlus.setOnClickListener { changeItemQuantity(imgBtnPlus) }
                imgBtnMinus.setOnClickListener { changeItemQuantity(imgBtnMinus) }

                // ActionBar
                toolbar.actionBarImgBtnMyCart.setOnClickListener { goToCartActivity(this@ProductDetailsActivity) }
                toolbar.actionBarCheckboxFavorite.setOnClickListener {
                    if (toolbar.actionBarCheckboxFavorite.isChecked) {
                        FirestoreHelper().removeFromFavorites(
                            this@ProductDetailsActivity,
                            productId,
                            userId
                        )
                    }
                    else {
                        FirestoreHelper().addToFavorites(
                            this@ProductDetailsActivity,
                            productId,
                            userId
                        )
                    }
                }
            }
        else
            binding.apply {
                listOf(btnAddToCart, imgBtnPlus, imgBtnMinus, toolbar.actionBarCheckboxFavorite, toolbar.actionBarImgBtnMyCart)
                    .forEach {
                        it.setOnClickListener {
                            goToSignInActivity(this@ProductDetailsActivity)
                        }
                }
            }
    }

    private fun getProductDetails() {
        // Check if user has this item in their favorites
        if (isInFavorites)
            binding.toolbar.actionBarCheckboxFavorite.isChecked = true

        FirestoreHelper().getProductDetails(this@ProductDetailsActivity, productId)
    }

    /**
     * A function to notify the success result of the product details based on the product id.
     *
     * @param product A model class with product details.
     */
    fun productDetailsSuccess(product: Product) {

        modelProduct = product

        binding.apply {
            // Populate the product details in the UI.
            GlideLoader(this@ProductDetailsActivity).loadProductPictureWide(
                product.iconUrl,
                imgViewPicture
            )
            txtViewName.text = product.name
            txtViewDescription.text = product.description
            txtViewPrice.text = String.format(
                getString(R.string.txt_format_price),
                Constants.DEFAULT_CURRENCY,
                product.price
            )
            txtViewWeight.text = String.format(
                getString(R.string.txt_format_weight),
                product.weight,
                product.weightUnit
            )

            if (product.stock > 0)
                txtViewStock.text = getString(R.string.txt_available)
        }
        FirestoreHelper().getCartId(this@ProductDetailsActivity, userId, productId)
    }

    fun cartIdSuccess(id: String) {

        // If ID is not null then it means the item is already in cart
        if (id != "") {
            cartId = id
            hideAddToCart()
            FirestoreHelper().getCartProductDetails(this@ProductDetailsActivity, cartId)
        }
        else {
            unveilDetails()
        }
    }

    private fun hideAddToCart() {
        binding.apply {
            btnAddToCart.visibility = View.INVISIBLE
            btnRemoveFromCart.visibility = View.VISIBLE
        }
    }
    private fun showAddToCart() {
        binding.apply {
            btnAddToCart.visibility = View.VISIBLE
            btnRemoveFromCart.visibility = View.GONE
        }
    }

    fun cartProductDetailsSuccess(cart: Cart) {
        modelCart = cart

        cartProductQuantity = modelCart.cartQuantity
        unveilDetails()
    }

    fun unveilDetails() {
        binding.apply {
            vLayoutHead.unVeil()
            vLayoutBody.unVeil()
        }
    }

    private fun updateCart() {
        if (cartId != "") {
            if (cartProductQuantity == 0)
                showAddToCart()
            else if (cartProductQuantity >= 1)
                hideAddToCart()
        }
        else {
            hideAddToCart()
        }
    }
    private fun removeFromCart() {
        showAddToCart()
    }

    private fun changeItemQuantity(view: View) {
        binding.apply {
            when (view) {
                imgBtnPlus -> {
                    // If next item quantity is not exceeding the max stock quantity and max default item number.
                    if (cartProductQuantity + 1 < Constants.DEFAULT_MAX_ITEM_CART_QUANTITY)
                        if (cartProductQuantity + 1 < modelProduct.stock)
                            cartProductQuantity++
                        else {
                            // TODO: Show snack bar error, cant exceed max stock
                            return
                        }
                    else {
                        // TODO: Show snack bar error, max purchasable quantity is 99
                        return
                    }
                }
                imgBtnMinus -> {
                    if (cartProductQuantity > 0) {
                        cartProductQuantity--
                    }
                    else {
                        SnackBarErrorClass
                            .make(view, getString(R.string.txt_error_selecting_below_zero))
                            .setAnchorView(binding.constraintLayoutBottom)
                            .show()
                        return
                    }
                }
            }
            updateCart()
            /*FirestoreHelper().updateCartProduct(
                this@ProductDetailsActivity,
                "test",
                cartProductQuantity
            )*/
            txtViewQuantityValue.text = cartProductQuantity.toString()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }
}
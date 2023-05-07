package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.badge.BadgeDrawable
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityProductDetailsBinding
import com.unipi.mpsp21043.client.models.Cart
import com.unipi.mpsp21043.client.models.Favorite
import com.unipi.mpsp21043.client.models.Product
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.GlideLoader
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.createBadge
import com.unipi.mpsp21043.client.utils.snackBarErrorClass
import com.unipi.mpsp21043.client.utils.snackBarSuccessClass

class ProductDetailsActivity2 : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * @see modelProduct
     * */
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var productId: String

    private lateinit var modelProduct: Product
    private var modelCart: Cart? = null
    private lateinit var modelFavorite: Favorite

    private var isInFavorites: Boolean = false
    private var priceReduced: Double = 0.00
    private var cartProductQuantity = 0

    private lateinit var cartBadge: BadgeDrawable


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

        getProductDetails()
    }

    private fun getProductDetails() {
        FirestoreHelper().getProductDetails(this, productId)
    }

    private fun loadProductDetails() {
        binding.apply {
            // Populate the product details in the UI.
            GlideLoader(this@ProductDetailsActivity2).loadProductPictureWide(
                modelProduct.iconUrl,
                imgViewPicture
            )
            txtViewName.text = modelProduct.name
            txtViewDescription.text = modelProduct.description
            txtViewPriceReduced.apply {
                visibility = View.VISIBLE
                foreground =
                    AppCompatResources.getDrawable(context, R.drawable.striking_red_text)
                text = String.format(
                    context.getString(R.string.text_format_price),
                    context.getString(R.string.curr_eur),
                    modelProduct.price
                )
            }
            priceReduced = modelProduct.price - (modelProduct.price * modelProduct.sale)
            txtViewPrice.text = String.format(
                getString(R.string.text_format_price),
                Constants.DEFAULT_CURRENCY,
                priceReduced
            )
            txtViewWeight.text = String.format(
                getString(R.string.text_format_weight),
                modelProduct.weight,
                modelProduct.weightUnit
            )

            if (modelProduct.stock > 0)
                txtViewStock.text = getString(R.string.text_available)
        }

        if (FirestoreHelper().getCurrentUserID() != "")
            checkIfProductIsInFavorites()
        else
            hideShimmerUI()
    }

    private fun getTotalCartItems() {
        FirestoreHelper().getCartItemsList(this@ProductDetailsActivity2)
    }

    private fun checkIfProductIsInFavorites() {
        FirestoreHelper().getFavoriteProduct(this, modelProduct.id)
    }

    private fun checkIfProductIsInCart() {
        FirestoreHelper().checkIfProductIsInCart(this, modelProduct.id)
    }

    fun successProductDetailsFromFirestore(product: Product) {
        modelProduct = product
        loadProductDetails()
    }

    fun successProductFavorite(favoriteProduct: Favorite) {
        modelFavorite = favoriteProduct

        if (modelFavorite.productId != "") {
            isInFavorites = true
            binding.toolbar.checkboxFavorite.isChecked = true
        }

        checkIfProductIsInCart()
    }

    fun successProductAddedToFavorites() {
        snackBarSuccessClass(binding.root, getString(R.string.text_product_added_to_favorites), binding.constraintLayoutAddToCart)
    }

    fun successProductRemovedFromFavorites() {
        snackBarSuccessClass(binding.root, getString(R.string.text_product_removed_from_favorites), binding.constraintLayoutAddToCart)
    }

    fun successCartItem(cartItem: Cart) {

        modelCart = cartItem

        if (modelCart?.userId != "") {
            cartProductQuantity = modelCart!!.cartQuantity
            updateCart()
        }

        getTotalCartItems()
    }

    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    fun successUserTotalCartItems(cartItems: ArrayList<Cart>) {
        if (cartItems.isNotEmpty()) {
            cartBadge = createBadge(this@ProductDetailsActivity2, binding.toolbar.imageButtonMyCart, cartItems.size)
        }

        hideShimmerUI()
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

    private fun updateCart() {
        if (modelCart != null && modelCart?.userId != "") {
            if (modelCart?.cartQuantity == 0)
                showAddToCart()
            else if (modelCart?.cartQuantity!! >= 1) {
                hideAddToCart()
                cartBadge.number += cartProductQuantity
                binding.txtViewQuantityValue.text = modelCart?.cartQuantity.toString()
            }
        }
        else {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        showProgressDialog()
        hideAddToCart()

        if (cartProductQuantity == 0)
            cartProductQuantity++

        modelCart = Cart(
            userId = FirestoreHelper().getCurrentUserID(),
            productId = modelProduct.id,
            imgUrl = modelProduct.iconUrl,
            name = modelProduct.name,
            price = modelProduct.price,
            sale = modelProduct.sale,
            stock = modelProduct.stock,
            weight = modelProduct.weight,
            weightUnit = modelProduct.weightUnit,
            cartQuantity = cartProductQuantity
        )

        FirestoreHelper().addItemToCart(this, modelCart!!)
    }

    fun successItemAddedToCart() {
        hideProgressDialog()

        hideAddToCart()
        cartBadge.number += cartProductQuantity
        binding.txtViewQuantityValue.text = cartProductQuantity.toString()
    }

    private fun updateItemToCart() {
        modelCart = Cart(
            userId = FirestoreHelper().getCurrentUserID(),
            productId = modelProduct.id,
            imgUrl = modelProduct.iconUrl,
            name = modelProduct.name,
            price = modelProduct.price,
            sale = modelProduct.sale,
            stock = modelProduct.stock,
            weight = modelProduct.weight,
            weightUnit = modelProduct.weightUnit,
            cartQuantity = cartProductQuantity
        )

        FirestoreHelper().updateItemFromCart(this, modelCart!!, modelProduct.id)
    }

    private fun deleteItemFromCard() {
        FirestoreHelper().deleteItemFromCart(this, modelProduct.id)
    }

    fun successItemDeletedFromCart() {
        showAddToCart()
        snackBarSuccessClass(binding.root, getString(R.string.text_product_removed_from_cart), binding.constraintLayoutAddToCart)

        modelCart = null
    }

    private fun changeItemQuantity(view: View) {
        binding.apply {
            when (view) {
                imgBtnPlus -> {
                    if (modelCart != null)  {
                        // If next item quantity is not exceeding the max stock quantity and max default item number.
                        // Basically, it let's you pick a max quantity of 99 for the same product.
                        if (cartProductQuantity + 1 <= Constants.DEFAULT_MAX_ITEM_CART_QUANTITY)
                        // If the selected quantity doesn't exceed the max stock number.
                            if (cartProductQuantity + 1 <= modelProduct.stock) {
                                cartProductQuantity++
                                updateItemToCart()
                            }
                            else {
                                // If it does, show a snackbar and explain the issue.
                                snackBarErrorClass(root, getString(R.string.text_error_max_stock), constraintLayoutAddToCart)
                                return
                            }
                        else {
                            // If the user is trying to select a quantity of more than 99,
                            // Show a snackbar and explain the issue.
                            snackBarErrorClass(root, getString(R.string.text_error_max_quantity), constraintLayoutAddToCart)
                            return
                        }
                    }
                    else {
                        addItemToCart()
                    }
                }
                imgBtnMinus -> {
                    when {
                        cartProductQuantity - 1 == 0 -> {
                            cartProductQuantity = 0
                            deleteItemFromCard()
                        }
                        cartProductQuantity > 0 -> {
                            cartProductQuantity--
                            updateItemToCart()
                        }
                        else -> {
                            snackBarErrorClass(root, getString(R.string.text_error_selecting_below_zero), constraintLayoutAddToCart)
                            return
                        }
                    }
                }
            }
            updateCart()
            cartBadge.number += cartProductQuantity
            txtViewQuantityValue.text = cartProductQuantity.toString()
        }
    }

    private fun setupUI() {
        // Display shimmer effect until all data is loaded
        showShimmerUI()

        setupActionBar()
        setupClickListeners()
    }

    private fun showShimmerUI() {
        binding.apply {
            layoutErrorState.root.visibility = View.GONE
            constraintLayoutDetails.visibility = View.INVISIBLE
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            constraintLayoutDetails.visibility = View.VISIBLE
            shimmerLayout.visibility = View.GONE
            shimmerLayout.stopShimmer()
        }
    }

    fun showErrorUI() {
        hideShimmerUI()
        binding.apply {
            layoutErrorState.root.visibility = View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        // If user IS logged in
        if (FirestoreHelper().getCurrentUserID() != "")
            binding.apply {
                btnAddToCart.setOnClickListener { changeItemQuantity(btnAddToCart) }
                btnRemoveFromCart.setOnClickListener { deleteItemFromCard() }
                imgBtnPlus.setOnClickListener { changeItemQuantity(imgBtnPlus) }
                imgBtnMinus.setOnClickListener { changeItemQuantity(imgBtnMinus) }

                // ActionBar
                toolbar.imageButtonMyCart.setOnClickListener { IntentUtils().goToListCartItemsActivity(this@ProductDetailsActivity2) }
                toolbar.checkboxFavorite.setOnClickListener {
                    if (!toolbar.checkboxFavorite.isChecked) {
                        FirestoreHelper().removeProductFromUserFavorites(
                            this@ProductDetailsActivity2,
                            modelProduct.id
                        )
                    }
                    else {
                        val favorite = Favorite(
                            FirestoreHelper().getCurrentUserID(),
                            modelProduct.id,
                            modelProduct.iconUrl,
                            modelProduct.name,
                            modelProduct.price,
                            modelProduct.sale
                        )
                        FirestoreHelper().addProductToUserFavorites(
                            this@ProductDetailsActivity2,
                            favorite
                        )
                    }
                }
            }
        else
            binding.apply {
                listOf(btnAddToCart, imgBtnPlus, imgBtnMinus, toolbar.checkboxFavorite, toolbar.imageButtonMyCart)
                    .forEach {
                        it.setOnClickListener {
                            toolbar.checkboxFavorite.isChecked = false
                            goToSignInActivity(this@ProductDetailsActivity2)
                        }
                    }
            }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }

    override fun onResume() {
        super.onResume()

        getProductDetails()
    }
}

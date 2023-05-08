package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
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

class ProductDetailsActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * @see modelProduct
     * */
    private lateinit var binding: ActivityProductDetailsBinding

    private lateinit var modelProduct: Product
    private lateinit var modelFavorite: Favorite
    private var modelCart: Cart? = null

    private lateinit var productId: String
    private var totalCartItems: Int = 0
    private var priceReduced: Double = 0.00

    private var cartBadge: BadgeDrawable? = null


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
        showShimmerUI()

        FirestoreHelper().getProductDetails(this, productId)
    }

    fun successProductDetailsFromFirestore(product: Product) {
        modelProduct = product

        binding.apply {
            // Populate the product details in the UI.
            GlideLoader(this@ProductDetailsActivity).loadProductPictureWide(
                modelProduct.iconUrl,
                imgViewPicture
            )
            txtViewName.text = modelProduct.name
            txtViewDescription.text = modelProduct.description
            // If item is on sale
            if (modelProduct.sale > 0) {
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
        else {
            hideShimmerUI()
        }
    }

    private fun checkIfProductIsInFavorites() {
        FirestoreHelper().getFavoriteProduct(this, modelProduct.id)
    }

    fun successCheckProductFavorite(favoriteProduct: Favorite) {
        modelFavorite = favoriteProduct

        if (modelFavorite.productId != "")
            binding.toolbar.checkboxFavorite.isChecked = true

        getTotalCartItems()
    }

    private fun getTotalCartItems() {
        FirestoreHelper().getCartItemsList(this@ProductDetailsActivity)
    }

    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    fun successUserTotalCartItems(cartItems: ArrayList<Cart>) {

        totalCartItems = cartItems.size
        setCartBadge(totalCartItems)

        val filteredCartItem = cartItems.filter { (key, _) -> key.contains(modelProduct.id) }

        val productInCart = let {
            filteredCartItem.isNotEmpty()
        }

        if (productInCart) {
            modelCart = filteredCartItem[0]

            Toast.makeText(this@ProductDetailsActivity, "in cart", Toast.LENGTH_LONG).show()

            binding.txtViewQuantityValue.text = modelCart!!.cartQuantity.toString()
        }

        updateButtonsUI()
        hideShimmerUI()
    }

    fun successCartItemUpdated() {
        hideProgressBar()

        updateButtonsUI()

        binding.apply {
            txtViewQuantityValue.text = modelCart!!.cartQuantity.toString()
        }
    }

    fun successProductAddedToFavorites() {
        hideProgressBar()

        snackBarSuccessClass(
            binding.root,
            getString(R.string.text_product_added_to_favorites),
            binding.constraintLayoutAddToCart
        )
    }

    fun successProductRemovedFromFavorites() {
        hideProgressBar()

        snackBarSuccessClass(
            binding.root,
            getString(R.string.text_product_removed_from_favorites),
            binding.constraintLayoutAddToCart
        )
    }

    private fun addItemToCart() {
        binding.apply {
            if (modelProduct.stock == 0) {
                // If product isn't in stock.
                snackBarErrorClass(
                    root,
                    getString(R.string.text_error_out_of_stock),
                    constraintLayoutAddToCart
                )
                return
            }
        }

        // Start process
        showProgressBar()

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
            cartQuantity = when (modelCart) {
                null -> 1
                else -> modelCart!!.cartQuantity++
            }
        )

        totalCartItems++

        FirestoreHelper().addItemToCart(this, modelCart!!)
    }

    private fun showProgressBar() {
        binding.apply {
            progressBarLayout.progressBarHorizontal.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            progressBarLayout.progressBarHorizontal.visibility = View.INVISIBLE
        }
    }

    fun successItemAddedToCart() {
        hideProgressBar()

        updateButtonsUI()
        setCartBadge(totalCartItems)
        binding.txtViewQuantityValue.text = modelCart!!.cartQuantity.toString()
    }

    private fun updateItemToCart() {
        showProgressBar()

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
            cartQuantity = modelCart!!.cartQuantity++
        )

        FirestoreHelper().updateItemFromCart(this, modelCart!!, modelProduct.id)
    }

    private fun deleteItemFromCard() {
        showProgressBar()

        totalCartItems--
        setCartBadge(totalCartItems)

        FirestoreHelper().deleteItemFromCart(this, modelProduct.id)
    }

    fun successItemDeletedFromCart() {
        hideProgressBar()

        snackBarSuccessClass(
            binding.root,
            getString(R.string.text_product_removed_from_cart),
            binding.constraintLayoutAddToCart
        )

        // Remove cart model
        modelCart = null

        binding.apply {
            // Set text view's quantity to zero.
            txtViewQuantityValue.text = "0"
        }

        updateButtonsUI()
    }

    private fun updateButtonsUI() {
        binding.apply {
            if (modelCart == null && buttonRemoveFromCart.isVisible) {
                buttonRemoveFromCart.visibility = View.GONE
                buttonRemoveFromCart.isEnabled = true
                buttonAddToCart.visibility = View.VISIBLE
                when {
                    cartBadge?.number?.minus(1) == 0 -> cartBadge?.clearNumber()
                    cartBadge?.number?.minus(1) != 0 -> cartBadge?.number = cartBadge?.number!! - 1
                }
            } else if (modelCart != null && buttonAddToCart.isVisible) {
                buttonAddToCart.visibility = View.INVISIBLE
                buttonAddToCart.isEnabled = true
                buttonRemoveFromCart.visibility = View.VISIBLE
                setCartBadge(totalCartItems)
            }
        }
    }

    private fun setCartBadge(totalCartItems: Int) {
        if (totalCartItems > 0) {
            if (cartBadge == null) {
                cartBadge = createBadge(
                    this@ProductDetailsActivity,
                    binding.toolbar.imageButtonMyCart,
                    totalCartItems
                )
                return
            }
            cartBadge?.isVisible = true
            cartBadge?.number = totalCartItems
            return
        }
        cartBadge?.clearNumber()
        cartBadge?.isVisible = false
    }

    private fun changeItemQuantity(view: View) {
        binding.apply {
            when (view) {
                buttonAddToCart -> {
                    addItemToCart()
                }

                buttonRemoveFromCart -> {
                    deleteItemFromCard()
                }

                imgBtnPlus -> {
                    if (modelCart != null) {
                        if (modelProduct.stock == 0) {
                            // If product isn't in stock.
                            snackBarErrorClass(
                                root,
                                getString(R.string.text_error_out_of_stock),
                                constraintLayoutAddToCart
                            )
                            return
                        }
                        // If next item quantity is not exceeding the max stock quantity and max default item number.
                        // Basically, it let's you pick a max quantity of 99 for the same product.
                        if (modelCart!!.cartQuantity + 1 <= Constants.DEFAULT_MAX_ITEM_CART_QUANTITY)
                        // If the selected quantity doesn't exceed the max stock number.
                            if (modelCart!!.cartQuantity + 1 <= modelProduct.stock) {
                                modelCart!!.cartQuantity++
                                updateItemToCart()
                            } else {
                                // If it does, show a snackbar and explain the issue.
                                snackBarErrorClass(
                                    root,
                                    getString(R.string.text_error_max_stock),
                                    constraintLayoutAddToCart
                                )
                                return
                            }
                        else {
                            // If the user is trying to select a quantity of more than 99,
                            // Show a snackbar and explain the issue.
                            snackBarErrorClass(
                                root,
                                getString(R.string.text_error_max_quantity),
                                constraintLayoutAddToCart
                            )
                            return
                        }
                    } else
                        addItemToCart()
                }

                imgBtnMinus -> {
                    if (modelCart != null) {
                        when {
                            modelCart!!.cartQuantity - 1 == 0 -> {
                                modelCart!!.cartQuantity = 0
                                deleteItemFromCard()
                            }

                            modelCart!!.cartQuantity > 0 -> {
                                modelCart!!.cartQuantity--
                                updateItemToCart()
                            }

                            else -> {
                                snackBarErrorClass(
                                    root,
                                    getString(R.string.text_error_selecting_below_zero),
                                    constraintLayoutAddToCart
                                )
                                return
                            }
                        }
                    }
                }
            }
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
                listOf(buttonAddToCart, buttonRemoveFromCart, imgBtnPlus, imgBtnMinus)
                    .forEach { button ->
                        button.setOnClickListener { changeItemQuantity(button) }
                    }

                // ActionBar
                toolbar.imageButtonMyCart.setOnClickListener {
                    IntentUtils().goToListCartItemsActivity(
                        this@ProductDetailsActivity
                    )
                }
                toolbar.checkboxFavorite.apply {
                    setOnClickListener {
                        showProgressBar()

                        if (!isChecked)
                            FirestoreHelper().removeProductFromUserFavorites(
                                this@ProductDetailsActivity,
                                modelProduct.id
                            )
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
                                this@ProductDetailsActivity,
                                favorite
                            )
                        }

                    }
                }
            return
            }
        binding.apply {
            listOf(
                buttonAddToCart,
                imgBtnPlus,
                imgBtnMinus,
                toolbar.checkboxFavorite,
                toolbar.imageButtonMyCart
            )
                .forEach {
                    it.setOnClickListener {
                        toolbar.checkboxFavorite.isChecked = false
                        goToSignInActivity(this@ProductDetailsActivity)
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

        // Reset these just in case user deletes them from cart and comes back.
        modelCart = null
        binding.txtViewQuantityValue.text = "0"
        getProductDetails()
    }
}

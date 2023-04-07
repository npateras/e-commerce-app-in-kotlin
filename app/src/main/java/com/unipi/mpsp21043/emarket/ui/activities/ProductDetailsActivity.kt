package com.unipi.mpsp21043.emarket.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.database.FirestoreHelper
import com.unipi.mpsp21043.emarket.databinding.ActivityProductDetailsBinding
import com.unipi.mpsp21043.emarket.models.Cart
import com.unipi.mpsp21043.emarket.models.Favorite
import com.unipi.mpsp21043.emarket.models.Product
import com.unipi.mpsp21043.emarket.utils.Constants
import com.unipi.mpsp21043.emarket.utils.GlideLoader
import com.unipi.mpsp21043.emarket.utils.IntentUtils
import com.unipi.mpsp21043.emarket.utils.SnackBarErrorClass
import java.util.*

class ProductDetailsActivity : BaseActivity() {

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

    fun successProductDetailsFromFirestore(product: Product) {
        modelProduct = product
        loadProductDetails()
    }

    private fun loadProductDetails() {
        binding.apply {
            // Populate the product details in the UI.
            GlideLoader(this@ProductDetailsActivity).loadProductPictureWide(
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

        checkIfProductIsInFavorites()
    }

    private fun checkIfProductIsInFavorites() {
        FirestoreHelper().getFavoriteProduct(this, modelProduct.id)
    }

    fun successFavoriteProductFromFirestore(favoriteProduct: Favorite) {
        modelFavorite = favoriteProduct

        if (modelFavorite.productId != "") {
            isInFavorites = true
            binding.toolbar.actionBarCheckboxFavorite.isChecked = true
        }

        checkIfProductIsInCart()
    }

    private fun checkIfProductIsInCart() {
        FirestoreHelper().checkIfProductIsInCart(this, modelProduct.id)
    }

    fun successCartItemFromFirestore(cartItem: Cart) {

        modelCart = cartItem

        if (modelCart?.userId != "") {
            cartProductQuantity = modelCart!!.cartQuantity
            updateCart()
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
        hideAddToCart()
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
                                SnackBarErrorClass
                                    .make(view, getString(R.string.text_error_max_stock))
                                    .setAnchorView(constraintLayoutAddToCart)
                                    .show()
                                return
                            }
                        else {
                            // If the user is trying to select a quantity of more than 99,
                            // Show a snackbar and explain the issue.
                            SnackBarErrorClass
                                .make(view, getString(R.string.text_error_max_quantity))
                                .setAnchorView(constraintLayoutAddToCart)
                                .show()
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
                            SnackBarErrorClass
                                .make(view, getString(R.string.text_error_selecting_below_zero))
                                .setAnchorView(constraintLayoutAddToCart)
                                .show()
                            return
                        }
                    }
                }
            }
            updateCart()

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
            layoutStateError.root.visibility = View.GONE
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
        binding.apply {
            layoutStateError.root.visibility = View.VISIBLE
            shimmerLayout.visibility = View.GONE
            shimmerLayout.stopShimmer()
        }
    }

    private fun setupClickListeners() {
        // If user IS logged in
        if (FirestoreHelper().getCurrentUserID() != "")
            binding.apply {
                btnAddToCart.setOnClickListener { updateCart() }
                btnRemoveFromCart.setOnClickListener { deleteItemFromCard() }
                imgBtnPlus.setOnClickListener { changeItemQuantity(imgBtnPlus) }
                imgBtnMinus.setOnClickListener { changeItemQuantity(imgBtnMinus) }

                // ActionBar
                toolbar.actionBarImgBtnMyCart.setOnClickListener { IntentUtils().goToListCartItemsActivity(this@ProductDetailsActivity) }
                toolbar.actionBarCheckboxFavorite.setOnClickListener {
                    if (!toolbar.actionBarCheckboxFavorite.isChecked) {
                        FirestoreHelper().removeProductFromUserFavorites(
                            this@ProductDetailsActivity,
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
                            this@ProductDetailsActivity,
                            favorite
                        )
                    }
                }
            }
        else
            binding.apply {
                listOf(btnAddToCart, imgBtnPlus, imgBtnMinus, toolbar.actionBarCheckboxFavorite, toolbar.actionBarImgBtnMyCart)
                    .forEach {
                        it.setOnClickListener {
                            toolbar.actionBarCheckboxFavorite.isChecked = false
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
            it.setCustomView(R.layout.toolbar_product_details)
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

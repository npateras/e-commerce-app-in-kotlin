package com.unipi.p17172.emarket.ui.activities

import android.os.Bundle
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.ActivityProductDetailsBinding
import com.unipi.p17172.emarket.models.Product
import com.unipi.p17172.emarket.utils.Constants
import com.unipi.p17172.emarket.utils.GlideLoader

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
    private var productId: String = ""

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
    }

    private fun setupUI() {
        setupClickListeners()

        getProductDetails()
    }

    private fun setupClickListeners() {
        binding.btnAddToCart.setOnClickListener { addToCart() }
        binding.btnRemoveFromCart.setOnClickListener { removeFromCart() }
        binding.imgBtnPlus.setOnClickListener { changeItemQuantity(true) }
        binding.imgBtnMinus.setOnClickListener { changeItemQuantity(false) }

        // ActionBar
        binding.toolbar.actionBarImgBtnMyCart.setOnClickListener { goToCartActivity(this@ProductDetailsActivity)}
        binding.toolbar.actionBarCheckboxFavorite.setOnClickListener { FirestoreHelper().addToFavorites(productId) }
    }

    private fun getProductDetails() {
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
            txtViewPrice.text = product.price.toString()
            txtViewWeight.text = String.format(
                getString(R.string.txt_format_weight),
                product.weight,
                product.weightUnit
            )

            if (product.stock > 0)
                txtViewStock.text = product.stock.toString()
        }
    }

    private fun addToCart() {

    }
    private fun removeFromCart() {

    }

    private fun changeItemQuantity(increase: Boolean) {
        when (increase) {
            true -> {

            }
            false -> {

            }
        }
    }
}
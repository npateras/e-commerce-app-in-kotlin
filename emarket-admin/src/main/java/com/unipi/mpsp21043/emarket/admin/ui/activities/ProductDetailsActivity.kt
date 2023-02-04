package com.unipi.mpsp21043.emarket.admin.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.unipi.mpsp21043.emarket.admin.R
import com.unipi.mpsp21043.emarket.admin.database.FirestoreHelper
import com.unipi.mpsp21043.emarket.admin.databinding.ActivityProductDetailsBinding
import com.unipi.mpsp21043.emarket.admin.models.Cart
import com.unipi.mpsp21043.emarket.admin.models.Product
import com.unipi.mpsp21043.emarket.admin.utils.Constants
import com.unipi.mpsp21043.emarket.admin.utils.GlideLoader
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
        showProgressDialog()

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
                    context.getString(R.string.txt_format_price),
                    context.getString(R.string.curr_eur),
                    modelProduct.price
                )
            }
            priceReduced = modelProduct.price - (modelProduct.price * modelProduct.sale)
            txtViewPrice.text = String.format(
                getString(R.string.txt_format_price),
                Constants.DEFAULT_CURRENCY,
                priceReduced
            )
            txtViewWeight.text = String.format(
                getString(R.string.txt_format_weight),
                modelProduct.weight,
                modelProduct.weightUnit
            )

            if (modelProduct.stock > 0)
                txtViewStock.text = getString(R.string.txt_available)
        }

        hideProgressDialog()
        unveilDetails()
    }

    private fun unveilDetails() {
        binding.apply {
            vLayoutHead.unVeil()
            vLayoutBody.unVeil()
        }
    }


    private fun setupUI() {
        // Veil the layout until all data is loaded
        binding.apply {
            vLayoutHead.veil()
            vLayoutBody.veil()
        }

        setupActionBar()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // If user IS logged in
        /*if (FirestoreHelper().getCurrentUserID() != "") {
            binding.apply {
                btnAddToCart.setOnClickListener { updateCart() }
                btnRemoveFromCart.setOnClickListener { deleteItemFromCard() }
                imgBtnPlus.setOnClickListener { changeItemQuantity(imgBtnPlus) }
                imgBtnMinus.setOnClickListener { changeItemQuantity(imgBtnMinus) }
            }
        } else
            binding.apply {
                listOf(
                    btnAddToCart,
                    imgBtnPlus,
                    imgBtnMinus,
                    toolbar.actionBarCheckboxFavorite,
                    toolbar.actionBarImgBtnMyCart
                )
                    .forEach {
                        it.setOnClickListener {
                            toolbar.actionBarCheckboxFavorite.isChecked = false
                            goToSignInActivity(this@ProductDetailsActivity)
                        }
                    }
            }*/
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }

    override fun onResume() {
        super.onResume()

        getProductDetails()
    }
}

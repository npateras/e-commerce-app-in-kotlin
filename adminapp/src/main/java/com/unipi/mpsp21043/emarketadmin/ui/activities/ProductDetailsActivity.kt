package com.unipi.mpsp21043.emarketadmin.ui.activities

import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.content.res.AppCompatResources
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import com.unipi.mpsp21043.emarketadmin.databinding.ActivityProductDetailsBinding
import com.unipi.mpsp21043.emarketadmin.models.Cart
import com.unipi.mpsp21043.emarketadmin.models.Favorite
import com.unipi.mpsp21043.emarketadmin.models.Product
import com.unipi.mpsp21043.emarketadmin.models.User
import com.unipi.mpsp21043.emarketadmin.utils.Constants
import com.unipi.mpsp21043.emarketadmin.utils.GlideLoader
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils

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
    private lateinit var mAddedByUser: User
    private lateinit var mLastModifiedByUser: User

    private var priceReduced: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        setupUI()
        init()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            productId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        getProductDetails()
    }

    private fun getProductDetails() {
        binding.txtViewPriceReduced.visibility = View.GONE
        FirestoreHelper().getProductDetails(this, productId)
    }

    fun successProductDetailsFromFirestore(product: Product) {
        modelProduct = product

        // Since we set the product model, we can setup our click listeners for the toolbar.
        setupClickListeners()

        loadProductDetails()
    }

    private fun loadProductDetails() {
        binding.apply {
            // Populate the product details in the UI.
            GlideLoader(this@ProductDetailsActivity).loadProductPictureWide(
                modelProduct.iconUrl,
                imgViewPicture
            )

            toolbar.textViewActionBarLabel.text = modelProduct.name
            txtViewName.text = modelProduct.name
            txtViewDescription.text = modelProduct.description

            if (modelProduct.sale != 0.0) {
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

        binding.apply {

            if (modelProduct.addedByUser.isEmpty()) {
                textViewAddedByUser.visibility = View.INVISIBLE
                imageViewArrowAddedBy.visibility = View.INVISIBLE
            }
            if (modelProduct.lastModifiedBy.isEmpty()) {
                textViewLastModifiedByUser.visibility = View.INVISIBLE
                imageViewArrowModifiedBy.visibility = View.INVISIBLE
            }
        }

        if (modelProduct.addedByUser.isNotEmpty()){
            FirestoreHelper().getUserDetails(this@ProductDetailsActivity, modelProduct.addedByUser, "added")
            binding.textViewAddedByUser.visibility = View.VISIBLE
            binding.imageViewArrowAddedBy.visibility = View.VISIBLE
        }
        else if (modelProduct.lastModifiedBy.isNotEmpty()) {
            FirestoreHelper().getUserDetails(this@ProductDetailsActivity, modelProduct.lastModifiedBy, "modified")
            binding.textViewLastModifiedByUser.visibility = View.VISIBLE
            binding.imageViewArrowModifiedBy.visibility = View.VISIBLE
        }
        else
            unveilDetails()
    }

    fun successAddedByUserDetailsFromFirestore(user: User) {
        mAddedByUser = user

        binding.textViewAddedByUser.text = String.format(getString(R.string.txt_format_added_by_user), mAddedByUser.fullName)

        if (modelProduct.lastModifiedBy.isNotEmpty()) {
            FirestoreHelper().getUserDetails(this@ProductDetailsActivity, modelProduct.lastModifiedBy, "modified")
            binding.textViewLastModifiedByUser.visibility = View.VISIBLE
        }
        else
            unveilDetails()
    }

    fun successLastModifiedByUserDetailsFromFirestore(user: User) {
        mLastModifiedByUser = user

        binding.apply {
            textViewLastModifiedByUser.text = String.format(getString(R.string.txt_format_last_modified_by_user), mLastModifiedByUser.fullName)
            textViewLastModifiedByUser.visibility = View.VISIBLE
            imageViewArrowModifiedBy.visibility = View.VISIBLE
        }

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
    }

    private fun setupClickListeners() {
        binding.apply {

            // ActionBar
            toolbar.actionBarImgBtnEdit.setOnClickListener { IntentUtils().goToEditProductActivity(this@ProductDetailsActivity, modelProduct) }
            toolbar.actionBarImgBtnDelete.setOnClickListener {  }
        }
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()

        getProductDetails()
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        finish()
        return super.getOnBackInvokedDispatcher()
    }
}

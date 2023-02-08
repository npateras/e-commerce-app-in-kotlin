package com.unipi.mpsp21043.emarketadmin.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import com.unipi.mpsp21043.emarketadmin.databinding.ActivityPayCreditCardBinding
import com.unipi.mpsp21043.emarketadmin.models.Address
import com.unipi.mpsp21043.emarketadmin.models.Cart
import com.unipi.mpsp21043.emarketadmin.models.Order
import com.unipi.mpsp21043.emarketadmin.models.Product
import com.unipi.mpsp21043.emarketadmin.utils.Constants
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils
import com.unipi.mpsp21043.emarketadmin.utils.SnackBarErrorClass

class PayWithCreditCardActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityPayCreditCardBinding
    // A global variable for the selected address details.
    private var mAddressDetails: Address? = null

    // A global variable for the product list.
    private lateinit var mProductsList: ArrayList<Product>

    // A global variable for the cart list.
    private lateinit var mCartItemsList: ArrayList<Cart>

    // A global variable for the SubTotal Amount.
    private var mSubTotal: Double = 0.0

    // A global variable for the Total Amount.
    private var mTotalAmount: Double = 0.0

    // A global variable for Order details.
    private lateinit var mOrderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityPayCreditCardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
        setupUI()
    }

    private fun init() {
        getProductList()
    }

    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog()

        FirestoreHelper().getAllProductsList(this)
    }

    /**
     * A function to get the success result of product list.
     *
     * @param productsList
     */
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        mProductsList = productsList

        getCartItemsList()
    }

    /**
     * A function to get the list of cart items in the activity.
     */
    private fun getCartItemsList() {
        FirestoreHelper().getCartItemsList(this@PayWithCreditCardActivity)
    }

    /**
     * A function to notify the success result of the cart items list from cloud firestore.
     *
     * @param cartList
     */
    fun successCartItemsList(cartList: ArrayList<Cart>) {

        // Hide progress dialog.
        hideProgressDialog()

        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.id == cart.productId) {
                    cart.stock = product.stock
                }
            }
        }

        mCartItemsList = cartList

        for (item in mCartItemsList) {

            val availableQuantity = item.stock

            if (availableQuantity > 0) {
                var price = item.price
                val quantity = item.cartQuantity

                if (item.sale != 0.0)
                    price -= (price * item.sale)

                mSubTotal += (price * quantity)
            }
        }

        binding.apply {
            btnAuthorize.text =
                String.format(
                    getString(R.string.txt_format_price_button_authorize),
                    getString(R.string.curr_eur),
                    mSubTotal + Constants.DEFAULT_DELIVERY_COST
                )
        }
    }

    /**
     * A function to prepare the Order details to place an order.
     */
    private fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog()

        mOrderDetails = Order(
            FirestoreHelper().getCurrentUserID(),
            mAddressDetails!!,
            mCartItemsList,
            "Order# ${System.currentTimeMillis()}",
            mSubTotal.toString(),
            Constants.DEFAULT_DELIVERY_COST.toString(), // The Shipping Charge is fixed as $10 for now in our case.
            mTotalAmount.toString(),
            getString(R.string.txt_credit_card)
        )

        FirestoreHelper().placeOrder(this@PayWithCreditCardActivity, mOrderDetails)
    }

    /**
     * A function to notify the success result of the order placed.
     */
    fun orderPlacedSuccess() {

        FirestoreHelper().updateAllDetails(this@PayWithCreditCardActivity, mCartItemsList)
    }

    /**
     * A function to notify the success result after updating all the required details.
     */
    fun allDetailsUpdatedSuccessfully() {

        // Hide the progress dialog.
        hideProgressDialog()

        IntentUtils().goToMainActivity(this, true)
        finish()
    }

    private fun setupUI() {
        setupActionBar()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnAuthorize.setOnClickListener {
                if (validateFields()) {
                    placeAnOrder()
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(inputTxtCreditCardNumber.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.txt_error_empty_password))
                        .show()
                    inputTxtLayoutCreditCardNumber.requestFocus()
                    inputTxtLayoutCreditCardNumber.error = getString(R.string.txt_error_empty_card_number)
                    false
                }

                TextUtils.isEmpty(inputTxtCreditCardFullName.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.txt_error_empty_email))
                        .show()
                    inputTxtLayoutCreditCardFullName.requestFocus()
                    inputTxtLayoutCreditCardFullName.error = getString(R.string.txt_error_empty_card_full_name)
                    false
                }

                TextUtils.isEmpty(inputTxtCreditCardExpDate.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.txt_error_empty_email))
                        .show()
                    inputTxtLayoutCreditCardExpDate.requestFocus()
                    inputTxtLayoutCreditCardExpDate.error = getString(R.string.txt_error_empty_card_exp_date)
                    false
                }

                TextUtils.isEmpty(inputTxtCreditCardCVV.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.txt_error_empty_email))
                        .show()
                    inputTxtLayoutCreditCardCVV.requestFocus()
                    inputTxtLayoutCreditCardCVV.error = getString(R.string.txt_error_empty_card_cvv)
                    false
                }

                else -> true
            }
        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionBarLabel.text = getString(R.string.txt_pay_invoice)
        }
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

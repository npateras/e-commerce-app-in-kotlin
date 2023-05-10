package com.unipi.mpsp21043.client.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityPayCreditCardBinding
import com.unipi.mpsp21043.client.models.Address
import com.unipi.mpsp21043.client.models.Cart
import com.unipi.mpsp21043.client.models.Order
import com.unipi.mpsp21043.client.models.Product
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.CreditCardUtils
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.snackBarErrorClass
import com.unipi.mpsp21043.client.utils.textInputLayoutError

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
            buttonAuthorize.text =
                String.format(
                    getString(R.string.text_format_price_button_authorize),
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
            mSubTotal,
            Constants.DEFAULT_DELIVERY_COST, // The Shipping Charge is fixed as $10 for now in our case.
            mTotalAmount,
            getString(R.string.text_credit_card)
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

        binding.apply {
            textInputEditTextCardNumber.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutCardNumber.apply {
                        when (CreditCardUtils().identifyCardScheme(s.toString())) {
                            CreditCardUtils.CardScheme.VISA -> setStartIconDrawable(R.drawable.svg_credit_card_visa)
                            CreditCardUtils.CardScheme.MASTERCARD -> setStartIconDrawable(R.drawable.svg_credit_card_mastercard)
                            CreditCardUtils.CardScheme.MAESTRO -> setStartIconDrawable(R.drawable.svg_credit_card_maestro)
                            CreditCardUtils.CardScheme.DISCOVER -> setStartIconDrawable(R.drawable.svg_credit_card_discover)
                            CreditCardUtils.CardScheme.DINERS_CLUB -> setStartIconDrawable(R.drawable.svg_credit_card_diners)
                            CreditCardUtils.CardScheme.AMEX -> setStartIconDrawable(R.drawable.svg_credit_card_amex)
                            CreditCardUtils.CardScheme.JCB -> setStartIconDrawable(R.drawable.svg_credit_card_jcb)
                            else -> setStartIconDrawable(R.drawable.svg_credit_card)
                        }
                    }

                }

            })
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonAuthorize.setOnClickListener {
                if (validateFields())
                    placeAnOrder()
            }
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(textInputEditTextCardNumber.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_card_number))
                    textInputLayoutError(textInputLayoutCardNumber, getString(R.string.text_error_empty_card_number))
                    false
                }

                TextUtils.isEmpty(textInputEditTextCardholderName.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_card_full_name))
                    textInputLayoutError(textInputLayoutCardholderName, getString(R.string.text_error_empty_card_full_name))
                    false
                }

                TextUtils.isEmpty(textInputEditTextExpirationDate.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_card_exp_date))
                    textInputLayoutError(textInputLayoutExpirationDate, getString(R.string.text_error_empty_card_exp_date))
                    false
                }

                TextUtils.isEmpty(textInputEditTextCvv.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_card_cvv))
                    textInputLayoutError(textInputLayoutCvv, getString(R.string.text_error_empty_card_cvv))
                    false
                }

                else -> true
            }
        }
    }


    private fun setupActionBar() {
        binding.actionBarWithToolbar.apply {
            setSupportActionBar(toolbar)
            textViewActionLabel.text = getString(R.string.text_pay_invoice)
        }

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
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

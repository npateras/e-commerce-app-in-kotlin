package com.unipi.mpsp21043.emarket.ui.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.database.FirestoreHelper
import com.unipi.mpsp21043.emarket.databinding.ActivityCheckoutBinding
import com.unipi.mpsp21043.emarket.models.Address
import com.unipi.mpsp21043.emarket.models.Cart
import com.unipi.mpsp21043.emarket.models.Order
import com.unipi.mpsp21043.emarket.models.Product
import com.unipi.mpsp21043.emarket.utils.Constants
import com.unipi.mpsp21043.emarket.utils.IntentUtils
import com.unipi.mpsp21043.emarket.utils.SnackBarErrorClass

class CheckoutActivity : BaseActivity() {

    /**
     * Class variables
     *
     * @see binding
     * */
    private lateinit var binding: ActivityCheckoutBinding
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
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
        setupUI()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS, Address::class.java)
            } else {
                intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
            }
        }

        if (mAddressDetails != null) {
            binding.apply {
                txtViewSelectedAddressNameValue.text = mAddressDetails?.fullName
                if (mAddressDetails?.phoneCode != 0)
                    txtViewSelectedAddressPhoneValue.text =
                        String.format(
                            getString(R.string.txt_format_phone),
                            mAddressDetails?.phoneCode,
                            mAddressDetails?.phoneNumber
                        )
                else
                    txtViewSelectedAddressPhoneValue.text = mAddressDetails?.phoneNumber
                txtViewSelectedAddressValue.text =
                    String.format(
                        getString(R.string.txt_format_address),
                        mAddressDetails!!.address,
                        mAddressDetails!!.zipCode
                    )
            }
        }

        getProductList()
    }

    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog()

        FirestoreHelper().getAllProductsList(this@CheckoutActivity)
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
        FirestoreHelper().getCartItemsList(this@CheckoutActivity)
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
            txtViewSubTotalValue.text =
                String.format(getString(R.string.txt_format_price),
                    getString(R.string.curr_eur),
                    mSubTotal
                )
            txtViewDeliveryChargeValue.text =
                String.format(getString(R.string.txt_format_price),
                    getString(R.string.curr_eur),
                    Constants.DEFAULT_DELIVERY_COST
                )
            txtViewTotalAmountValue.text =
                String.format(getString(R.string.txt_format_price),
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
            getString(R.string.txt_cash_on_delivery)
        )

        FirestoreHelper().placeOrder(this@CheckoutActivity, mOrderDetails)
    }

    /**
     * A function to notify the success result of the order placed.
     */
    fun orderPlacedSuccess() {

        FirestoreHelper().updateAllDetails(this@CheckoutActivity, mCartItemsList)
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
            /*radioGroupPaymentMethod.setOnCheckedChangeListener { group, checkedId ->
                // val rb = findViewById<View>(checkedId) as RadioButton
                paymentMethod = if (checkedId == 0)
                    getString(R.string.txt_cash_on_delivery)
                else {
                    getString(R.string.txt_credit_card)
                }
            }*/
            btnContinue.setOnClickListener{
                if (validateFields()) {
                    if (radioBtnCreditCard.isChecked) {
                        IntentUtils().goToPayWithCreditCardActivity(this@CheckoutActivity)
                    }
                    else if (radioBtnCashOnDelivery.isChecked) {
                        placeAnOrder()
                    }
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            if (!radioBtnCashOnDelivery.isChecked && !radioBtnCreditCard.isChecked) {
                SnackBarErrorClass
                    .make(root, getString(R.string.txt_error_payment_method_not_selected))
                    .setAnchorView(constraintLayoutBottom)
                    .show()
                radioGroupPaymentMethod.requestFocus()
                return false
            }

            return true
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.textViewActionBarLabel.text = getString(R.string.button_checkout)
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

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioBtn_Cash_On_Delivery ->
                    if (checked) {
                        binding.txtViewPaymentMethodValue.text = getString(R.string.txt_cash_on_delivery)
                    }
                R.id.radioBtn_Credit_Card ->
                    if (checked) {
                        binding.txtViewPaymentMethodValue.text = getString(R.string.txt_credit_card)
                    }
            }
        }
    }
}

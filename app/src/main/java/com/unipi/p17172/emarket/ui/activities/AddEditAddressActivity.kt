package com.unipi.p17172.emarket.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.ActivityAddEditAddressBinding
import com.unipi.p17172.emarket.models.Address
import com.unipi.p17172.emarket.utils.Constants
import com.unipi.p17172.emarket.utils.SnackBarErrorClass

class AddEditAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityAddEditAddressBinding
    private var mUserAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // This calls the parent constructor
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // This is used to align the xml view to this class

        init()
        setupUI()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mUserAddress =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }
    }

    private fun setupUI() {
        setUpActionBar()
    }

    private fun saveAddressToFirestore() {

        var fullName = ""
        var phoneCode = 0
        var phoneNumber = ""
        var address = ""
        var zipCode = ""
        var additionalNotes = ""

        // Here we get the text from editText and trim the space
        binding.run {
            fullName = inputTxtFullName.text.toString().trim { it <= ' ' }
            phoneCode = countryCodePicker.selectedCountryCodeAsInt
            phoneNumber = inputTxtPhoneNumber.text.toString().trim { it <= ' ' }
            address = inputTxtAddress.text.toString().trim { it <= ' ' }
            zipCode = inputTxtZipCode.text.toString().trim { it <= ' ' }
            additionalNotes = inputTxtAdditionalNotes.text.toString().trim { it <= ' ' }
        }

        if (validateFields()) {
            showProgressDialog()

            val addressModel = Address(
                FirestoreHelper().getCurrentUserID(),
                fullName,
                phoneNumber,
                phoneCode,
                address,
                zipCode,
                additionalNotes
            )

            if (mUserAddress != null && mUserAddress!!.id.isNotEmpty()) {
                FirestoreHelper().updateAddress(
                    this@AddEditAddressActivity,
                    addressModel,
                    mUserAddress!!.id
                )
            } else {
                FirestoreHelper().addAddress(this@AddEditAddressActivity, addressModel)
            }
        }
    }

    fun successUpdateAddressToFirestore() {
        // Hide progress dialog
        hideProgressDialog()

        val notifySuccessMessage: String = if (mUserAddress != null && mUserAddress!!.id.isNotEmpty()) {
            resources.getString(R.string.txt_your_address_updated_successfully)
        } else {
            resources.getString(R.string.txt_your_address_added_successfully)
        }

        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)
        finish()
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(inputTxtFullName.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.txt_error_empty_name))
                        .setBehavior(Constants.SNACKBAR_BEHAVIOR)
                        .show()
                    inputTxtLayoutFullName.requestFocus()
                    inputTxtLayoutFullName.error = getString(R.string.txt_error_empty_name)
                    false
                }

                TextUtils.isEmpty(inputTxtPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.txt_error_empty_email))
                        .setBehavior(Constants.SNACKBAR_BEHAVIOR)
                        .show()
                    inputTxtLayoutPhoneNumber.requestFocus()
                    inputTxtLayoutPhoneNumber.error = getString(R.string.txt_error_empty_phone)
                    false
                }

                TextUtils.isEmpty(inputTxtAddress.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.txt_error_empty_email))
                        .setBehavior(Constants.SNACKBAR_BEHAVIOR)
                        .show()
                    inputTxtLayoutAddress.requestFocus()
                    inputTxtLayoutAddress.error = getString(R.string.txt_error_empty_address)
                    false
                }

                TextUtils.isEmpty(inputTxtZipCode.text.toString().trim { it <= ' ' }) -> {
                    SnackBarErrorClass
                        .make(root, getString(R.string.txt_error_empty_email))
                        .setBehavior(Constants.SNACKBAR_BEHAVIOR)
                        .show()
                    inputTxtLayoutZipCode.requestFocus()
                    inputTxtLayoutZipCode.error = getString(R.string.txt_error_empty_zip_code)
                    false
                }

                else -> true
            }
        }
    }

    private fun loadInputTextDetails() {
        binding.apply {
            inputTxtFullName.setText(mUserAddress?.fullName)
            mUserAddress?.phoneCode?.let { countryCodePicker.setCountryForPhoneCode(it) }
            inputTxtAddress.setText(mUserAddress?.address)
            inputTxtZipCode.setText(mUserAddress?.zipCode)
            inputTxtAdditionalNotes.setText(mUserAddress?.additionalNote)
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.apply {
                if (mUserAddress == null)
                    textViewActionBarLabel.text = getString(R.string.txt_add_new_address)
                else {
                    loadInputTextDetails()
                    textViewActionBarLabel.text = getString(R.string.txt_edit_address)
                }
                imgBtnSave.setOnClickListener{ saveAddressToFirestore() }
            }
        }

        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.toolbar_product_details)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_chevron_left_24dp)
        }
    }

}

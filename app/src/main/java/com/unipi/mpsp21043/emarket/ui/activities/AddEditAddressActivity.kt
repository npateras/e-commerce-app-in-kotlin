package com.unipi.mpsp21043.emarket.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.database.FirestoreHelper
import com.unipi.mpsp21043.emarket.databinding.ActivityAddEditAddressBinding
import com.unipi.mpsp21043.emarket.models.Address
import com.unipi.mpsp21043.emarket.utils.*

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
            mUserAddress = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS, Address::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
            }
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
            fullName = textInputEditTextFullName.text.toString().trim { it <= ' ' }
            phoneCode = countryCodePicker.selectedCountryCodeAsInt
            phoneNumber = textInputEditTextPhoneNumber.text.toString().trim { it <= ' ' }
            address = textInputEditTextAddress.text.toString().trim { it <= ' ' }
            zipCode = textInputEditTextZipCode.text.toString().trim { it <= ' ' }
            additionalNotes = textInputEditTextAdditionalNotes.text.toString().trim { it <= ' ' }
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

        val notifySuccessMessage: String = if (mUserAddress != null && mUserAddress!!.id.isNotEmpty())
            resources.getString(R.string.text_your_address_updated_successfully)
        else
            resources.getString(R.string.text_your_address_added_successfully)

        snackBarSuccessClass(binding.root, notifySuccessMessage)

        setResult(RESULT_OK)
        finish()
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(textInputEditTextFullName.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_name))
                    textInputLayoutError(textInputLayoutFullName, getString(R.string.text_error_empty_name))
                    false
                }

                TextUtils.isEmpty(textInputEditTextPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_phone))
                    textInputLayoutError(textInputLayoutPhoneNumber, getString(R.string.text_error_empty_phone))
                    false
                }

                TextUtils.isEmpty(textInputEditTextAddress.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_address))
                    textInputLayoutError(textInputLayoutAddress, getString(R.string.text_error_empty_address))
                    false
                }

                TextUtils.isEmpty(textInputEditTextZipCode.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_zip_code))
                    textInputLayoutError(textInputLayoutZipCode, getString(R.string.text_error_empty_zip_code))
                    false
                }

                else -> true
            }
        }
    }

    private fun loadInputTextDetails() {
        binding.apply {
            textInputEditTextFullName.setText(mUserAddress?.fullName)
            mUserAddress?.phoneCode?.let { countryCodePicker.setCountryForPhoneCode(it) }
            textInputEditTextAddress.setText(mUserAddress?.address)
            textInputEditTextZipCode.setText(mUserAddress?.zipCode)
            textInputEditTextAdditionalNotes.setText(mUserAddress?.additionalNote)
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar.root)

        val actionBar = supportActionBar
        binding.apply {
            toolbar.apply {
                if (mUserAddress == null)
                    textViewActionHeader.text = getString(R.string.text_add_new_address)
                else {
                    loadInputTextDetails()
                    textViewActionHeader.text = getString(R.string.text_edit_address)
                }
                imageButtonSave.setOnClickListener{ saveAddressToFirestore() }
            }
        }

        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }

}

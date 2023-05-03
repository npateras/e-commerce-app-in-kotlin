package com.unipi.mpsp21043.admin.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RadioButton
import android.window.OnBackInvokedDispatcher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.unipi.mpsp21043.admin.R
import com.unipi.mpsp21043.admin.database.FirestoreHelper
import com.unipi.mpsp21043.admin.databinding.ActivityAddEditProductBinding
import com.unipi.mpsp21043.admin.models.Product
import com.unipi.mpsp21043.admin.utils.*
import java.io.IOException


class AddEditProductActivity : BaseActivity() {
    private lateinit var binding: ActivityAddEditProductBinding

    // A global variable for user submission.
    private var mProduct: Product? = null

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    private var mProductImageURL: String = ""

    private var mProductSelectedCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_MODEL)) {
            mProduct = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(Constants.EXTRA_PRODUCT_MODEL, Product::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Constants.EXTRA_PRODUCT_MODEL)!!
            }
        }

        setupUI()
        setupActionBar()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.apply {
            if (mProduct != null && mProduct!!.id.isNotEmpty()) {
                shimmerViewContainer.startShimmer()
                shimmerViewContainer.visibility = View.VISIBLE
                linearLayoutContainer.visibility = View.GONE

                textViewHeader1.text = getString(R.string.update_a_product)
                textViewHeader2.text = getString(R.string.update_a_product_2)
                buttonAddProduct.visibility = View.GONE
                buttonEditProduct.visibility = View.VISIBLE

                mProductSelectedCategory = mProduct!!.category
            }

            /* Remove the error effects from input texts after text is written */
            /* Product Category */
            autoCompleteTextViewProductCategory.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutProductCategory.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /* Product Name */
            textInputEditTextProductName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutProductName.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /* Product Price */
            textInputEditTextProductPrice.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutProductPrice.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /* Product Sale */
            textInputEditTextProductSale.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutProductSale.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /* Product Stock */
            textInputEditTextProductStock.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutProductStock.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /* Product Weight */
            textInputEditTextProductWeight.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutProductWeight.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /* Product Weight Unit */
            autoCompleteTextViewWeightUnit.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutProductWeightUnit.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /* Product Description */
            textInputEditTextProductDescription.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayoutProductDescription.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            listOf(buttonAddProduct, buttonEditProduct, toolbar.imageButtonSave)
                .forEach {
                    it.setOnClickListener {
                        if (validateDetails()) {
                            checkIfImageWasUploaded()
                        }
                        else
                            buttonAnimationStart()
                    }
                }

            /* Category Dialog Select */
            autoCompleteTextViewProductCategory.setOnClickListener { showSelectCategoryDialog() }

            /* Weight Unit Dialog Select */
            autoCompleteTextViewWeightUnit.setOnClickListener { showSelectWeightUnitDialog() }

            relativeLayoutImageContents.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@AddEditProductActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Constants.showImageChooser(this@AddEditProductActivity)
                } else {
                    /*Requests permissions to be granted to this application. These permissions
                     must be requested in your manifest, they should not be granted to your app,
                     and they should have protection level*/
                    ActivityCompat.requestPermissions(
                        this@AddEditProductActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
        }
    }

    private fun showSelectCategoryDialog() {
        binding.apply {
            val dialog = DialogUtils().showDialogSelectCategory(this@AddEditProductActivity)
            dialog.show()

            val dialogSelectButton = dialog.findViewById<MaterialButton>(R.id.btn_Dialog_Select)
            dialogSelectButton.setOnClickListener {
                if (dialog.findViewById<RadioButton>(R.id.radio_button_beverages).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_beverages)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_beverages))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_chilled).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_chilled)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_chilled))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_fish).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_fish)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_fish))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_frozen).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_frozen)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_frozen))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_fruits).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_fruits)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_fruits))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_grocery).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_grocery)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_grocery))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_homeware).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_homeware)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_homeware))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_household).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_household)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_household))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_liquor).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_liquor)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_liquor))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_meat).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_meat)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_meat))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_pharmacy).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_pharmacy)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_pharmacy))
                }

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_vegetables).isChecked) {
                    mProductSelectedCategory = getString(R.string.text_category_vegetables)
                    autoCompleteTextViewProductCategory.setText(getString(R.string.text_category_vegetables))
                }

                dialog.dismiss()
            }
        }
    }

    private fun showSelectWeightUnitDialog() {

        binding.apply {
            val dialog = DialogUtils().showDialogSelectWeightUnit(this@AddEditProductActivity)
            dialog.show()

            val dialogSelectButton = dialog.findViewById<MaterialButton>(R.id.btn_Dialog_Select)
            dialogSelectButton.setOnClickListener {
                if (dialog.findViewById<RadioButton>(R.id.radio_button_grams).isChecked)
                    autoCompleteTextViewWeightUnit.setText(getString(R.string.text_weight_unit_shortcut_grams))

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_kg).isChecked)
                    autoCompleteTextViewWeightUnit.setText(getString(R.string.text_weight_unit_shortcut_kg))

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_liters).isChecked)
                    autoCompleteTextViewWeightUnit.setText(getString(R.string.text_weight_unit_shortcut_liters))

                else if (dialog.findViewById<RadioButton>(R.id.radio_button_pills).isChecked)
                    autoCompleteTextViewWeightUnit.setText(getString(R.string.text_weight_unit_shortcut_pills))

                dialog.dismiss()
            }
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateDetails(): Boolean {
        binding.apply {
            return when {
                /* Product Category */
                TextUtils.isEmpty(autoCompleteTextViewProductCategory.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_product_category))
                    textInputLayoutError(textInputLayoutProductCategory, getString(R.string.text_error_empty_product_category))
                    false
                }

                /* Product Name */
                TextUtils.isEmpty(textInputEditTextProductName.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_product_name))
                    textInputLayoutError(textInputLayoutProductName, getString(R.string.text_error_empty_product_name))
                    false
                }

                /* Product Price */
                TextUtils.isEmpty(textInputEditTextProductPrice.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_product_price))
                    textInputLayoutError(textInputLayoutProductPrice, getString(R.string.text_error_empty_product_price))
                    false
                }

                /* Product Stock */
                TextUtils.isEmpty(textInputEditTextProductStock.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_product_stock))
                    textInputLayoutError(textInputLayoutProductStock, getString(R.string.text_error_empty_product_stock))
                    false
                }

                /* Product Description */
                TextUtils.isEmpty(textInputEditTextProductDescription.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_product_description))
                    textInputLayoutError(textInputLayoutProductDescription, getString(R.string.text_error_empty_product_description))
                    false
                }

                else -> true
            }
        }
    }

    /**
     * A function to submit the product to Firestore
     */
    private fun saveProductToFirestore() {
        binding.apply  {
            val productHashMap = HashMap<String, Any>()

            val pName: String = textInputEditTextProductName.text.toString().trim { it <= ' ' }
            val pCategory: String = autoCompleteTextViewProductCategory.text.toString().trim { it <= ' ' }
            val pPrice: String = textInputEditTextProductPrice.text.toString().trim { it <= ' ' }
            val pSale: String = textInputEditTextProductSale.text.toString().trim { it <= ' ' }
            val pStock: String = textInputEditTextProductStock.text.toString().trim { it <= ' ' }
            val pWeight: String = textInputEditTextProductWeight.text.toString().trim { it <= ' ' }
            val pWeightUnit: String = autoCompleteTextViewWeightUnit.text.toString().trim { it <= ' ' }
            val pDescription: String = textInputEditTextProductDescription.text.toString().trim { it <= ' ' }

            productHashMap[Constants.FIELD_NAME] = pName
            productHashMap[Constants.FIELD_CATEGORY] = pCategory
            productHashMap[Constants.FIELD_PRICE] = pPrice.toDouble()
            productHashMap[Constants.FIELD_STOCK] = pStock.toInt()
            productHashMap[Constants.FIELD_DESCRIPTION] = pDescription
            productHashMap[Constants.FIELD_LAST_MODIFIED_BY] = FirestoreHelper().getCurrentUserID()
            if (pSale.isNotEmpty())
                productHashMap[Constants.FIELD_SALE] = pSale.toDouble()
            else if (pSale == "")
                productHashMap[Constants.FIELD_SALE] = 0
            if (pWeight.isNotEmpty())
                productHashMap[Constants.FIELD_WEIGHT] = pWeight.toInt()
            if (pWeightUnit.isNotEmpty())
                productHashMap[Constants.FIELD_WEIGHT_UNIT] = pWeightUnit
            if (mSelectedImageFileUri != null && mProduct!!.id.isNotEmpty())
                productHashMap[Constants.FIELD_ICON_URL] = mProductImageURL

            /* If the product is being updated */                /* If the product is new */
            if (mProduct != null && mProduct!!.id.isNotEmpty()) {
                FirestoreHelper().updateProduct(
                    this@AddEditProductActivity,
                    productHashMap,
                    mProduct!!.id
                )
            }
            /* If the product is new */
            else {
                productHashMap[Constants.FIELD_ADDED_BY_USER] = FirestoreHelper().getCurrentUserID()
                FirestoreHelper().addProduct(this@AddEditProductActivity, productHashMap)
            }
        }
    }

    /**
     * A function to notify the success result after completing all required steps.
     */
    fun successUpdateProductToFirestore() {
        // Hide progress dialog
        hideProgressDialog()

        val notifySuccessMessage: String = if (mProduct != null && mProduct!!.id.isNotEmpty()) {
            String.format(getString(R.string.text_product_updated_successfully), mProduct?.name)
        } else {
            String.format(getString(R.string.text_product_added_successfully), mProduct?.name)
        }

        SnackBarSuccessClass
            .make(binding.root, notifySuccessMessage)
            .setBehavior(Constants.SNACKBAR_BEHAVIOR)
            .show()

        /*setResult(RESULT_OK)
        finish()*/
    }

    private fun checkIfImageWasUploaded() {
        // Show the progress dialog.
        showProgressDialog()

        if (mSelectedImageFileUri != null) {

            FirestoreHelper().uploadImageToCloudStorage(
                this@AddEditProductActivity,
                mSelectedImageFileUri,
                mProductSelectedCategory + "/" + Constants.PRODUCT_IMAGE
            )
        }
        else
            saveProductToFirestore()
    }

    private fun buttonAnimationStart() {
        binding.buttonAddProduct.startAnimation(AnimationUtils.loadAnimation(this@AddEditProductActivity, R.anim.shake))
        binding.buttonEditProduct.startAnimation(AnimationUtils.loadAnimation(this@AddEditProductActivity, R.anim.shake))
    }

    /**
     * A function to notify the success result of image upload to the Cloud Storage.
     *
     * @param imageURL After successful upload the Firebase Cloud returns the URL.
     */
    fun imageUploadSuccess(imageURL: String) {
        mProductImageURL = imageURL

        saveProductToFirestore()
    }

    private fun loadInputTextDetails() {
        binding.apply {
            if (mProduct?.iconUrl?.isNotEmpty() == true) {
                binding.apply {
                    GlideLoader(this@AddEditProductActivity).loadAddEditProductPicture(
                        mProduct!!.iconUrl,
                        imageViewPicture
                    )
                    imageViewPicture.scaleType = ImageView.ScaleType.FIT_CENTER;
                }
            }

            autoCompleteTextViewProductCategory.setText(mProduct?.category)
            textInputEditTextProductName.setText(mProduct?.name)
            textInputEditTextProductPrice.setText(mProduct?.price.toString())
            textInputEditTextProductSale.setText(mProduct?.sale.toString())
            textInputEditTextProductStock.setText(mProduct?.stock.toString())
            textInputEditTextProductWeight.setText(mProduct?.weight.toString())
            autoCompleteTextViewWeightUnit.setText(mProduct?.weightUnit)
            textInputEditTextProductDescription.setText(mProduct?.description)

            toolbar.textViewActionLabel.text = String.format(
                getString(R.string.text_format_update),
                mProduct?.name
            )

            shimmerViewContainer.stopShimmer()
            shimmerViewContainer.visibility = View.GONE
            linearLayoutContainer.visibility = View.VISIBLE
        }
    }

    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddEditProductActivity)
            }
            else {
                // Displaying ano1ther snackbar if permission is not granted
                snackBarErrorClass(binding.root, getString(R.string.text_read_storage_permission_denied))
            }
        }
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {

                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this@AddEditProductActivity).loadAddEditProductPicture(
                            mSelectedImageFileUri!!,
                            binding.imageViewPicture
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        snackBarErrorClass(binding.root, getString(R.string.text_image_selection_failed))
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            if (mProduct == null)
                textViewActionLabel.text = getString(R.string.add_product)
            else
                loadInputTextDetails()
        }

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        finish()
        return super.getOnBackInvokedDispatcher()
    }

}

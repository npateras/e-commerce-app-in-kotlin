package com.unipi.mpsp21043.client.ui.activities

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.ActivityEditProfileBinding
import com.unipi.mpsp21043.client.models.User
import com.unipi.mpsp21043.client.utils.Constants
import com.unipi.mpsp21043.client.utils.Constants.STORAGE_PATH_USERS
import com.unipi.mpsp21043.client.utils.GlideLoader
import com.unipi.mpsp21043.client.utils.IntentUtils
import com.unipi.mpsp21043.client.utils.hideProgressBarHorizontalTop
import com.unipi.mpsp21043.client.utils.showProgressBarHorizontalTop
import com.unipi.mpsp21043.client.utils.snackBarErrorClass
import com.unipi.mpsp21043.client.utils.snackBarSuccessLargeClass
import com.unipi.mpsp21043.client.utils.textInputLayoutError
import java.io.IOException


class EditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var mUserDetails: User

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setupUI()
        setupClickListeners()

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            mUserDetails = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS, User::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
            }

            binding.apply {
                textInputEditTextFullName.setText(mUserDetails.fullName)
                countryCodePicker.setCountryForPhoneCode(mUserDetails.phoneCode)
                textInputEditTextPhoneNumber.setText(mUserDetails.phoneNumber)

                if (mUserDetails.profImgUrl.isNotEmpty()) {
                    GlideLoader(this@EditProfileActivity).loadUserPicture(
                        mUserDetails.profImgUrl,
                        circleImageViewUserPicture
                    )
                }
            }
        }
    }

    private fun updateProfileToFirestore() {
        val userHashMap = HashMap<String, Any>()

        // Here we get the text from editText and trim the space
        val inputFullName = binding.textInputEditTextFullName.text.toString().trim { it <= ' ' }
        val inputPhoneCode = binding.countryCodePicker.selectedCountryCodeAsInt
        val inputPhoneNumber = binding.textInputEditTextPhoneNumber.text.toString().trim { it <= ' ' }

        binding.apply {
            if (inputFullName != mUserDetails.fullName) {
                userHashMap[Constants.FIELD_FULL_NAME] = inputFullName
            }

            if (mUserProfileImageURL.isNotEmpty()) {
                userHashMap[Constants.FIELD_PROF_IMG_URL] = mUserProfileImageURL
            }

            if (inputPhoneNumber.isNotEmpty() && inputPhoneNumber != mUserDetails.phoneNumber) {
                userHashMap[Constants.FIELD_PHONE_NUMBER] = inputPhoneNumber
            }

            if (inputPhoneCode != 0) {
                userHashMap[Constants.FIELD_PHONE_CODE] = inputPhoneCode
            }

            // Here if user is about to complete the profile then update the field or else no need.
            // false: User profile is incomplete.
            // true: User profile is completed.
            if (!mUserDetails.profileCompleted && (inputPhoneNumber != "" && inputPhoneCode != 0 && mUserProfileImageURL != "")) {
                userHashMap[Constants.FIELD_COMPLETE_PROFILE] = true
            }

            FirestoreHelper().updateProfile(this@EditProfileActivity, userHashMap)
        }
    }

    fun successUpdateProfileToFirestore() {
        hideProgressBarHorizontalTop(this@EditProfileActivity, binding)

        snackBarSuccessLargeClass(binding.root, getString(R.string.text_profile_updated))
        val buttonSnackBarDismiss = findViewById<MaterialButton>(R.id.button_snackbar_success_large_dismiss)
        buttonSnackBarDismiss.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            setResult(RESULT_OK)
            finish()
        }, 3000)
    }

    private fun validateFields(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(textInputEditTextFullName.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_name))
                    textInputLayoutError(textInputLayoutFullName)
                    false
                }

                TextUtils.isEmpty(textInputEditTextPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                    snackBarErrorClass(root, getString(R.string.text_error_empty_phone))
                    textInputLayoutError(textInputLayoutFullName)
                    false
                }

                else -> true
            }
        }
    }

    private fun uploadPicture() {
        if (validateFields()) {
            showProgressBarHorizontalTop(this@EditProfileActivity, binding)

            if (mSelectedImageFileUri != null) {

                FirestoreHelper().uploadImageToCloudStorage(
                    this@EditProfileActivity,
                    mSelectedImageFileUri,
                    STORAGE_PATH_USERS + FirestoreHelper().getCurrentUserID() + "/" + Constants.FIELD_PROF_IMG_URL
                )
            }
            else {
                updateProfileToFirestore()
            }
        }
    }

    fun showErrorUI() {
        hideProgressBarHorizontalTop(this@EditProfileActivity, binding)
        binding.layoutErrorState.root.visibility = View.VISIBLE
    }

    private val activityResultLauncherGetImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (it.data != null) {
                try {
                    // The uri of selected image from phone storage.
                    mSelectedImageFileUri = it.data!!.data!!

                    GlideLoader(this@EditProfileActivity).loadUserPicture(
                        mSelectedImageFileUri!!,
                        binding.circleImageViewUserPicture
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    snackBarErrorClass(binding.root, getString(R.string.text_image_selection_failed))
                }
            }
        }
        /*else if (it.resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }*/
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
                Constants.showImageChooserV2(activityResultLauncherGetImage)
            } else {
                // Displaying another toast if permission is not granted
                snackBarErrorClass(binding.root, getString(R.string.text_read_storage_permission_denied))
            }
        }
    }

    /**
     * A function to notify the success result of image upload to the Cloud Storage.
     *
     * @param imageURL After successful upload the Firebase Cloud returns the URL.
     */
    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageURL = imageURL

        updateProfileToFirestore()
    }

    private fun setupUI() {
        setupActionBar()
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonSave.setOnClickListener { uploadPicture() }
            listOf(circleImageViewCamera, circleImageViewCamera)
                .forEach {
                    it.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(
                                this@EditProfileActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            Constants.showImageChooserV2(activityResultLauncherGetImage)
                            //Constants.showImageChooser(this@EditProfileActivity)
                        }
                        else {
                            /*Requests permissions to be granted to this application. These permissions
                             must be requested in your manifest, they should not be granted to your app,
                             and they should have protection level*/
                            ActivityCompat.requestPermissions(
                                this@EditProfileActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE
                            )
                        }
                    }
                }
        }
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            textViewActionLabel.text = getString(R.string.text_edit_profile)
            imageButtonSave.setOnClickListener { uploadPicture() }
            imageButtonSettings.setOnClickListener { IntentUtils().goToListSettingsActivity(this@EditProfileActivity) }
        }

        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.svg_chevron_left)
            it.setHomeActionContentDescription(getString(R.string.text_go_back))
        }
    }
}

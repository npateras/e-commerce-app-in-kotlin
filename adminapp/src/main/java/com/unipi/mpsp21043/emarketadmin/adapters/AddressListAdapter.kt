package com.unipi.mpsp21043.emarketadmin.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.databinding.ItemAddressBinding
import com.unipi.mpsp21043.emarketadmin.models.Address
import com.unipi.mpsp21043.emarketadmin.ui.activities.AddEditAddressActivity
import com.unipi.mpsp21043.emarketadmin.utils.Constants
import java.util.*

/**
 * A adapter class for products list items.
 */
open class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>
) : RecyclerView.Adapter<AddressListAdapter.AddressViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ProductsViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            ItemAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val model = list[position]

        holder.binding.apply {
            txtViewFullName.text = model.fullName
            if (model.phoneCode.toString() != "")
                txtViewPhoneNumber.text = String.format(
                    context.getString(R.string.txt_format_phone),
                    model.phoneCode,
                    model.phoneNumber
                )
            else
                txtViewPhoneNumber.text = model.phoneNumber
            txtViewAddress.text = String.format(
                context.getString(R.string.txt_format_address),
                model.address,
                model.zipCode
            )
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function to edit the address details and pass the existing details through intent.
     *
     * @param activity
     * @param position
     */
    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class AddressViewHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root)
}

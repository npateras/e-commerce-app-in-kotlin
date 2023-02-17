package com.unipi.mpsp21043.emarketadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.databinding.ItemSearchBinding
import com.unipi.mpsp21043.emarketadmin.models.Search
import com.unipi.mpsp21043.emarketadmin.utils.GlideLoader
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils


/**
 * A adapter class for products list items.
 */
open class SearchListAdapter(
    private val context: Context,
    private var list: ArrayList<Search>
) : RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link SearchViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemSearchBinding.inflate(
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
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val model = list[position]

        holder.binding.apply {
            GlideLoader(context).loadProductPictureWide(
                model.imgUrl,
                imageViewPicture
            )

            textViewLabel1.text = model.label1
            textViewLabel2.text = model.label2
        }

        // Click listener on list item click
        holder.itemView.setOnClickListener {
            IntentUtils().goToProductDetailsActivity(context, model.id)
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class SearchViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root)
}

package com.unipi.mpsp21043.admin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.admin.R
import com.unipi.mpsp21043.admin.databinding.ItemProductBinding
import com.unipi.mpsp21043.admin.models.Product
import com.unipi.mpsp21043.admin.utils.GlideLoader
import com.unipi.mpsp21043.admin.utils.IntentUtils
import java.util.*
import kotlin.collections.ArrayList


/**
 * A adapter class for products list items.
 */
open class ProductsListAdapter(
    private val context: Context,
    list: ArrayList<Product>
) : RecyclerView.Adapter<ProductsListAdapter.ProductsViewHolder>(), Filterable {

    var productsList: ArrayList<Product> = ArrayList()
    var productsListAll: ArrayList<Product> = ArrayList()

    init {
        this.productsList = list
        productsListAll = ArrayList()
        productsListAll.addAll(productsList)
    }

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ProductsViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            ItemProductBinding.inflate(
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
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val model = productsList[position]
        var priceReduced = model.price

        holder.binding.apply {
            GlideLoader(context).loadProductPictureWide(
                model.iconUrl,
                imageViewPicture
            )
            textViewName.text = model.name
            if (model.sale != 0.0) {
                textViewPriceReduced.apply {
                    visibility = View.VISIBLE
                    foreground =
                        AppCompatResources.getDrawable(context, R.drawable.shape_striking_red_text)
                    text = String.format(
                        context.getString(R.string.text_format_price),
                        context.getString(R.string.curr_eur),
                        model.price
                    )
                }
                priceReduced = model.price - (model.price * model.sale)
            }
            textViewPrice.apply {
                text = String.format(
                    context.getString(R.string.text_format_price),
                    context.getString(R.string.curr_eur),
                    priceReduced
                )
            }

            txtViewWeight.text = String.format(
                context.getString(R.string.text_format_weight),
                model.weight,
                model.weightUnit
            )
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
        return productsList.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ProductsViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return searchFilter
    }

    private var searchFilter: Filter = object : Filter() {
        //Automatic on background thread
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filteredList: ArrayList<Product> = ArrayList()
            if (charSequence.isEmpty()) {
                filteredList.addAll(productsListAll)
            }
            else {
                val text = charSequence.toString().lowercase(Locale.getDefault())
                for (item in productsListAll) {
                    if ((item.name.lowercase(Locale.getDefault()).contains(text))
                        || (item.category.lowercase(Locale.getDefault()).contains(text))
                        || (item.id.lowercase(Locale.getDefault()).contains(text))
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        //Automatic on UI thread
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            productsList.clear()
            productsList.addAll(filterResults.values as Collection<Product>)
            notifyDataSetChanged()
        }
    }

}

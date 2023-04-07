package com.unipi.mpsp21043.emarketadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.emarketadmin.R
import com.unipi.mpsp21043.emarketadmin.databinding.ItemProductBinding
import com.unipi.mpsp21043.emarketadmin.models.Product
import com.unipi.mpsp21043.emarketadmin.utils.GlideLoader
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils
import java.util.*


/**
 * A adapter class for products list items.
 */
open class ProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<ProductsListAdapter.ProductsViewHolder>(), Filterable {

    var listFiltered: ArrayList<Product> = list

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

    open fun setList(context: Context?, productList: ArrayList<Product>){
        val  result: DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun getOldListSize(): Int{
                return this@ProductsListAdapter.list.size
            }
            override fun getNewListSize(): Int{
                return list.size
            }
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean{
                return this@ProductsListAdapter.list[oldItemPosition].name === list[newItemPosition].name
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean{
                val newProduct: Product = this@ProductsListAdapter .list[oldItemPosition]
                val oldProduct: Product = list[newItemPosition]
                return newProduct.name === oldProduct.name
            }
        })
        this.listFiltered = list
        result.dispatchUpdatesTo(this)
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
        val model = list[position]
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
        return listFiltered.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ProductsViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString: String = constraint.toString()
                listFiltered = if (charString.isEmpty()) {
                    list
                } else {
                    val filteredList: ArrayList<Product> = ArrayList()
                    for (item in list) {
                        if (item.name.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.addedByUser.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.category.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.dateAdded.toString().lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.description.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.id.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.weightUnit.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.price.toString().lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = listFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                listFiltered = results?.values as ArrayList<Product>

                notifyDataSetChanged()
            }
        }
    }

}

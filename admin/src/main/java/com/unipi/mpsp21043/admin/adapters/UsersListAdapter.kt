package com.unipi.mpsp21043.admin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.admin.databinding.ItemUserBinding
import com.unipi.mpsp21043.admin.models.User
import com.unipi.mpsp21043.admin.utils.GlideLoader
import com.unipi.mpsp21043.admin.utils.IntentUtils
import java.util.*


/**
 * A adapter class for categories list items.
 */
open class UsersListAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<UsersListAdapter.UsersViewHolder>(), Filterable {

    var listFiltered: ArrayList<User> = list

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link UsersViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    open fun setList(context: Context?, UsersList: ArrayList<User>){
        val  result: DiffUtil.DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun getOldListSize(): Int{
                return this@UsersListAdapter.list.size
            }

            override fun getNewListSize(): Int{
                return list.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean{
                return this@UsersListAdapter.list[oldItemPosition].email === list[newItemPosition].email
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean{
                val newUser: User = this@UsersListAdapter.list[oldItemPosition]
                val oldUser: User = list[newItemPosition]
                return newUser.email === oldUser.email
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
    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val model = list[position]

        holder.binding.apply {
            textViewName.text = model.fullName
            textViewEmail.text = model.email
            GlideLoader(context).loadUserPicture(
                model.profImgUrl,
                circleImageViewUserPicture
            )
        }
        holder.itemView.setOnClickListener {
            IntentUtils().goToUserDetailsActivity(context, model.id)
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
    class UsersViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString: String = constraint.toString()
                listFiltered = if (charString.isEmpty()) {
                    list
                } else {
                    val filteredList: ArrayList<User> = ArrayList()
                    for (item in list) {
                        if (item.email.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.fullName.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.phoneNumber.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.phoneCode.toString().lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.role.lowercase()
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                        else if (item.id.lowercase()
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

                listFiltered = results?.values as ArrayList<User>

                notifyDataSetChanged()
            }
        }
    }
}

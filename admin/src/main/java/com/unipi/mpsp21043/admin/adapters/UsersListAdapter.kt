package com.unipi.mpsp21043.admin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.unipi.mpsp21043.admin.databinding.ItemUserBinding
import com.unipi.mpsp21043.admin.models.Product
import com.unipi.mpsp21043.admin.models.User
import com.unipi.mpsp21043.admin.utils.GlideLoader
import com.unipi.mpsp21043.admin.utils.IntentUtils
import java.util.*


/**
 * A adapter class for categories list items.
 */
open class UsersListAdapter(
    private val context: Context,
    list: ArrayList<User>
) : RecyclerView.Adapter<UsersListAdapter.UsersViewHolder>(), Filterable {

    var usersList: ArrayList<User> = ArrayList()
    var usersListAll: ArrayList<User> = ArrayList()

    init {
        this.usersList = list
        usersListAll = ArrayList()
        usersListAll.addAll(usersList)
    }

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
        val model = usersList[position]

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
        return usersList.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class UsersViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return searchFilter
    }

    private var searchFilter: Filter = object : Filter() {
        //Automatic on background thread
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filteredList: ArrayList<User> = ArrayList()
            if (charSequence.isEmpty()) {
                filteredList.addAll(usersListAll)
            }
            else {
                val text = charSequence.toString().lowercase(Locale.getDefault())
                for (item in usersListAll) {
                    if ((item.fullName.lowercase(Locale.getDefault()).contains(text))
                        || (item.email.lowercase(Locale.getDefault()).contains(text))
                        || (item.phoneCode.toString().lowercase(Locale.getDefault()).contains(text))
                        || (item.phoneNumber.lowercase(Locale.getDefault()).contains(text))
                        || (item.role.lowercase(Locale.getDefault()).contains(text))
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
            usersList.clear()
            usersList.addAll(filterResults.values as Collection<User>)
            notifyDataSetChanged()
        }
    }
}

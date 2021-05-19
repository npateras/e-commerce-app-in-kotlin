package com.unipi.p17172.emarket.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.p17172.emarket.databinding.RecyclerItemProductBinding
import com.unipi.p17172.emarket.holders.ProductViewHolder
import com.unipi.p17172.emarket.models.ProductModel
import java.util.*


class ProductRecyclerViewAdapter(
    private val productsList: MutableList<ProductModel>,
    private val context: Context,
    private val db: FirebaseFirestore) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view: RecyclerItemProductBinding =
            RecyclerItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var recyclerItemProductBinding: RecyclerItemProductBinding

        val product = productsList[position]

        Glide.with(context)
            .load(Objects.requireNonNull<Map<String, Any>>(documentProduct.getData())["imgUrl"].toString())
            .into<Target<Drawable>>(holder.itemCartProductBinding.imageViewCartProductImage)

        holder.edit.setOnClickListener { updateNote(product) }
        holder.delete.setOnClickListener { deleteNote(product.id!!, position) }
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    private fun updateNote(note: ContactsContract.CommonDataKinds.Note) {
        val intent = Intent(context, NoteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateNoteId", note.id)
        intent.putExtra("UpdateNoteTitle", note.title)
        intent.putExtra("UpdateNoteContent", note.content)
        context.startActivity(intent)
    }

    private fun deleteNote(id: String, position: Int) {
        db.collection("notes")
            .document(id)
            .delete()
            .addOnCompleteListener {
                productsList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, productsList.size)
                Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show()
            }
    }


}
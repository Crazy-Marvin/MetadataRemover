package rocks.poopjournal.metadataremover.ui.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rocks.poopjournal.metadataremover.R

class PageAdapter : RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    private val imageUris = mutableListOf<Uri>()
    private var lastItemClickedListener: OnLastItemClickedListener? = null
    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val addNewBody: View = itemView.findViewById(R.id.addNewBody)
        val addNewButton : ImageButton = itemView.findViewById(R.id.addNewButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_child, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val uri = imageUris[position]

        if (isLastItem(position)){
            holder.addNewBody.visibility = View.VISIBLE
            holder.addNewButton.visibility = View.VISIBLE
            holder.addNewBody.setOnClickListener {
                lastItemClickedListener?.onLastItemClicked()
            }
            holder.addNewButton.setOnClickListener {
                lastItemClickedListener?.onLastItemClicked()
            }
        } else {
            holder.addNewBody.visibility = View.GONE
            holder.addNewButton.visibility = View.GONE

            Glide.with(holder.imageView)
                .load(uri)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    fun isLastItem(position: Int) : Boolean {
        return position + 1 == itemCount
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setImageUris(uris: List<Uri>) {
        if (imageUris.isEmpty()){
            imageUris.addAll(uris)
            imageUris.add(Uri.EMPTY)
        } else {
            imageUris.removeLast()
            imageUris.addAll(uris)
            imageUris.add(Uri.EMPTY)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun restart(){
        imageUris.clear()
        notifyDataSetChanged()
    }

    fun setOnLastItemClickedListener(listener: OnLastItemClickedListener) {
        lastItemClickedListener = listener
    }
}

interface OnLastItemClickedListener {
    fun onLastItemClicked()
}
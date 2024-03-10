package rocks.poopjournal.metadataremover.ui.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rocks.poopjournal.metadataremover.R

class PageAdapter : RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    private val imageUris = mutableListOf<Uri>()

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_child, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val uri = imageUris[position]
        Glide.with(holder.imageView)
            .load(uri)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setImageUris(uris: List<Uri>) {
        imageUris.clear()
        imageUris.addAll(uris)
        notifyDataSetChanged()
    }
}

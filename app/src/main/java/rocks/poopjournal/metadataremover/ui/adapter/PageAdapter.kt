package rocks.poopjournal.metadataremover.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.util.SupportedTypes

class PageAdapter : RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    private val imageUris = mutableListOf<Uri>()
    private var mediaType = SupportedTypes.IMAGE

    private var lastItemClickedListener: OnLastItemClickedListener? = null
    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val addNewBody: View = itemView.findViewById(R.id.addNewBody)
        val addNewButton : ImageButton = itemView.findViewById(R.id.addNewButton)
        val playIcon: ImageView = itemView.findViewById(R.id.playIcon)
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

            if (mediaType == SupportedTypes.IMAGE){
                Glide.with(holder.imageView)
                    .load(uri)
                    .into(holder.imageView)
            } else {
                val thumbnail = getThumbnail(holder.imageView.context, uri)
                holder.imageView.setImageBitmap(thumbnail)
                holder.playIcon.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    fun isLastItem(position: Int) : Boolean {
        return position + 1 == itemCount
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUris(uris: List<Uri>, type: SupportedTypes) {
        if (imageUris.isEmpty()){
            imageUris.addAll(uris)
            imageUris.add(Uri.EMPTY)
        } else {
            imageUris.removeLast()
            imageUris.addAll(uris)
            imageUris.add(Uri.EMPTY)
        }
        mediaType = type
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun restart(){
        imageUris.clear()
        notifyDataSetChanged()
    }

    private fun getThumbnail(context: Context, videoUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val bitmap: Bitmap? = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        retriever.release()
        return bitmap
    }

    fun setOnLastItemClickedListener(listener: OnLastItemClickedListener) {
        lastItemClickedListener = listener
    }
}

interface OnLastItemClickedListener {
    fun onLastItemClicked()
}
/*
 * MIT License
 *
 * Copyright (c) 2018 Jan Heinrich Reimer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rocks.poopjournal.metadataremover.ui

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.databinding.ListitemMetaDataBinding
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.ui.MetaAttributeAdapter.ViewHolder
import rocks.poopjournal.metadataremover.util.extensions.android.getThemeColor
import rocks.poopjournal.metadataremover.util.extensions.android.layoutInflater
import rocks.poopjournal.metadataremover.util.extensions.android.setImage

class MetaAttributeAdapter(
        attributes: Set<Metadata.Attribute> = emptySet())
    : RecyclerView.Adapter<ViewHolder>() {

    private var _attributes: List<Metadata.Attribute> = attributes.toList()
    var attributes: Set<Metadata.Attribute>
        get() = _attributes.toSet()
        set(value) {
            _attributes = value.toList()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ListitemMetaDataBinding>(
                parent.layoutInflater,
                R.layout.listitem_meta_data,
                parent,
                false)

        binding.icon.setColorFilter(
                binding.icon.context.getThemeColor(android.R.attr.textColorSecondary)
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = _attributes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attribute = _attributes[position]
        val labelText = attribute.label.load(holder.itemView.context)
        val primaryValue = attribute.primaryValue.load(holder.itemView.context)
        val secondaryValue = attribute.secondaryValue?.load(holder.itemView.context)

        with(holder.binding) {
            title.text = primaryValue
            label.contentDescription = labelText
            icon.contentDescription = labelText
            icon.setImage(attribute.icon)
            description.visibility = if (secondaryValue != null) View.VISIBLE else View.GONE
            description.text = secondaryValue

            executePendingBindings()
        }
    }

    class ViewHolder(internal val binding: ListitemMetaDataBinding)
        : RecyclerView.ViewHolder(binding.root)
}
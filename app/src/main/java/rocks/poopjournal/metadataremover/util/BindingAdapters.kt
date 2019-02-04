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
@file:Suppress("UNUSED")

package rocks.poopjournal.metadataremover.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.extensions.android.setContentDescription
import rocks.poopjournal.metadataremover.util.extensions.android.setImage
import rocks.poopjournal.metadataremover.util.extensions.android.setText
import rocks.poopjournal.metadataremover.util.extensions.android.setTitle

@BindingAdapter("android:src")
fun setImage(view: ImageView, image: Image?) {
    if (image == null) {
        return
    }
    view.setImage(image)
}

@BindingAdapter("srcUrl", "android:src", "roundImage")
fun setImage(imageView: ImageView, url: String?, fallback: Drawable?, roundImage: Boolean?) {
    if (url == null || fallback == null || roundImage == null) {
        return
    }
    Glide.with(imageView)
            .load(url)
            .apply {
                if (roundImage) {
                    apply(RequestOptions.circleCropTransform())
                }
            }
            .error(Glide.with(imageView).load(fallback))
            .into(imageView)
}

@BindingAdapter("srcUrl", "android:src")
fun setImage(imageView: ImageView, url: String?, fallback: Drawable?) =
        setImage(imageView, url, fallback, false)

@BindingAdapter("android:text")
fun setText(view: TextView, text: Text?) {
    if (text == null) {
        return
    }
    view.setText(text)
}

@BindingAdapter("android:text")
fun setTitle(view: Toolbar, text: Text?) {
    if (text == null) {
        return
    }
    view.setTitle(text)
}

@BindingAdapter("android:contentDescription")
fun setContentDescription(view: View, text: Text?) {
    if (text == null) {
        return
    }
    view.setContentDescription(text)
}


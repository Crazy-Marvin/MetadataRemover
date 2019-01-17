package rocks.poopjournal.metadataremover.util

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding

interface AndroidViewDslScope {

    operator fun <T : View> T.invoke(block: T.() -> Unit): Unit =
            DefaultAndroidViewDslScope.run { invoke(block) }

    operator fun <T : ViewDataBinding> T.invoke(block: T.() -> Unit): Unit =
            DefaultAndroidViewDslScope.run { invoke(block) }

    operator fun Menu.invoke(block: Menu.() -> Unit): Unit =
            DefaultAndroidViewDslScope.run { invoke(block) }

    operator fun MenuItem.invoke(block: MenuItem.() -> Unit): Unit =
            DefaultAndroidViewDslScope.run { invoke(block) }

    fun Menu.findItem(@IdRes id: Int, block: MenuItem.() -> Unit): MenuItem =
            DefaultAndroidViewDslScope.run { findItem(id, block) }

    object DefaultAndroidViewDslScope : AndroidViewDslScope {

        override operator fun <T : View> T.invoke(block: T.() -> Unit) {
            apply(block)
        }

        override operator fun <T : ViewDataBinding> T.invoke(block: T.() -> Unit) {
            apply(block)
        }

        override operator fun Menu.invoke(block: Menu.() -> Unit) {
            apply(block)
        }

        override operator fun MenuItem.invoke(block: MenuItem.() -> Unit) {
            apply(block)
        }

        override fun Menu.findItem(@IdRes id: Int, block: MenuItem.() -> Unit): MenuItem =
                findItem(id).apply(block)
    }
}
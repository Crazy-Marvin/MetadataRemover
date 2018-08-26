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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zagum.expandicon.ExpandIconView.LESS
import com.github.zagum.expandicon.ExpandIconView.MORE
import com.google.android.material.snackbar.Snackbar
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.databinding.ActivityMainBinding
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.ActivityLauncher
import rocks.poopjournal.metadataremover.util.ActivityResultLauncher
import rocks.poopjournal.metadataremover.util.Logger
import rocks.poopjournal.metadataremover.util.extensions.android.*
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.get
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.observe
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.observeNotNull
import rocks.poopjournal.metadataremover.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), ActivityResultLauncher {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val panelSlideListener: PanelSlideListener = object : PanelSlideListener {
        override fun onPanelSlide(panel: View, slideOffset: Float) {
            onPanelSlide(slideOffset)
        }

        override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
            binding.bottomSheet.expandCollapse.apply {
                val state = when (newState) {
                    COLLAPSED -> LESS to R.string.title_menu_item_collapse
                    EXPANDED, ANCHORED -> MORE to R.string.title_menu_item_expand
                    HIDDEN, DRAGGING -> return
                }
                setState(state.first, true)
                contentDescription = getText(state.second)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)!!

        initializeViews()
        updatePreview(null)

        viewModel.wrongMimeTypeFileSelectedHint.observeNotNull(this) { event ->
            Snackbar.make(
                    binding.coordinator,
                    getString(R.string.description_snackbar_wrong_mime_type, event.mimeType),
                    Snackbar.LENGTH_LONG
            ).setAction(R.string.title_action_snackbar_wrong_mime_type) {
                viewModel.openFile()
            }
        }
        viewModel.metadata.observe(this, ::updatePreview)
        viewModel.activityLaunchInfo.observeNotNull(this, ::startActivity)
        viewModel.activityResultLaunchInfo.observeNotNull(this, ::startActivityForResult)
    }

    override fun onResume() {
        super.onResume()
        binding.slidingLayout.addPanelSlideListener(panelSlideListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        binding.slidingLayout.removePanelSlideListener(panelSlideListener)
        super.onPause()
    }

    override fun startActivity(launchInfo: ActivityLauncher.LaunchInfo) =
            startActivity(launchInfo.intent, launchInfo.options)

    @SuppressLint("RestrictedApi")
    override fun startActivityForResult(launchInfo: ActivityResultLauncher.LaunchInfo) =
            startActivityForResult(launchInfo.intent, launchInfo.requestCode, launchInfo.options)

    private fun initializeViews() {
        binding.preview.apply {
            toolbar.apply {
                inflateMenu(R.menu.menu_main)
                menu.findItem(R.id.menu_item_about)
                        .onMenuItemClick(viewModel::openAboutScreen)
                        .apply {
                            val tintColor = getThemeColor(android.R.attr.textColorSecondary)
                            val tintedIcon = icon.tint(tintColor)
                            icon = tintedIcon
                        }
            }

            bannerOpenImage.setOnClickListener { viewModel.openFile() }
        }

        binding.slidingLayout.isTouchEnabled = false

        binding.bottomSheet.apply {
            toolbar.apply {
                setOnClickListener { viewModel.openFile() }
                inflateMenu(R.menu.menu_main_bottom_sheet)
                menu.findItem(R.id.menu_item_open_file)
                        .onMenuItemClick(viewModel::openFile)
            }

            expandCollapse.setState(MORE, false)
            expandCollapse.onClick {
                binding.slidingLayout.apply {
                    panelState = when (panelState) {
                        EXPANDED, ANCHORED -> COLLAPSED
                        else -> ANCHORED
                    }
                }
            }

            listMetadata.apply {
                adapter = MetaAttributeAdapter()
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }

        onPanelSlide(slideOffset = 1.0f)
    }

    private fun onPanelSlide(@FloatRange(from = 0.0, to = 1.0) slideOffset: Float) {
        val panelHeight = binding.slidingLayout.height - binding.slidingLayout.panelHeight
        val absoluteOffset = panelHeight * slideOffset
        val reverseAbsoluteOffset = panelHeight * (1 - slideOffset)
        val limitedReverseAbsoluteOffset = reverseAbsoluteOffset
                .coerceAtMost(panelHeight * (1 - binding.slidingLayout.anchorPoint))

        binding.navigationBarShadow.apply {
            alpha = ((absoluteOffset / height) - 1)
                    .coerceAtMost(1f)

        }
        binding.bottomSheet.buttonRemoveMetadata.translationY = -limitedReverseAbsoluteOffset

        if (binding.slidingLayout.panelState == DRAGGING) {
            //val fraction = .5f + speedApproximation * .5f
            val fraction = 1 - (slideOffset
                    .coerceAtMost(binding.slidingLayout.anchorPoint) /
                    binding.slidingLayout.anchorPoint)
            binding.bottomSheet.expandCollapse.setFraction(fraction, false)
        }
    }

    private fun updatePreview(preview: Metadata?) {
        Logger.d("Update preview: $preview")

        binding.preview.apply {
            thumbnail.apply {
                preview?.thumbnail?.let { setImage(it) }
                isVisible = preview != null
            }

            bannerOpenImage.isVisible = preview == null
        }

        binding.slidingLayout.apply {
            isTouchEnabled = preview != null
            if (preview == null) {
                panelState = COLLAPSED
            }
        }

        binding.bottomSheet.apply {
            toolbar.apply {
                setTitle(preview?.title ?: Text(R.string.title_bottom_sheet_open_file))
                menu.findItem(R.id.menu_item_open_file)
                        ?.isVisible = preview == null

                setOnClickListener {
                    if (preview == null) {
                        viewModel.openFile()
                    }
                }
            }

            expandCollapse.isVisible = preview != null

            listMetadata.apply {
                isVisible = preview != null

                if (preview != null) {

                    launch(UI) {
                        Logger.d("${preview.attributes.size} metadata attributes")
                        preview.attributes.forEach {
                            Logger.d("${it.label.load(this@MainActivity.context)}: " +
                                    "${it.primaryValue.load(this@MainActivity.context)}/" +
                                    "${it.secondaryValue?.load(this@MainActivity.context)} " +
                                    "(${it.icon.load(this@MainActivity.context)})")
                        }
                    }

                    val adapter = adapter as MetaAttributeAdapter
                    Logger.d("${preview.attributes.size} metadata attributes")
                    adapter.attributes = preview.attributes
                    Logger.d("${adapter.attributes.size} metadata attributes (adapter)")
                }
            }
            bannerNoMetadata.apply {
                isVisible = preview == null
            }
            buttonRemoveMetadata.apply {
                isEnabled = preview != null
                if (preview != null) {
                    setOnClickListener { viewModel.removeMetadata() }
                }
            }
        }

        /*
        if (preview == null) {
            TODO("Disable file closing buttons.")
        }
        else {
            TODO("Enable file closing buttons.")
        }
        // */
    }
}

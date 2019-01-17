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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.zagum.expandicon.ExpandIconView
import com.google.android.material.snackbar.Snackbar
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.databinding.ActivityMainBinding
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.Resource
import rocks.poopjournal.metadataremover.model.resources.Resource.*
import rocks.poopjournal.metadataremover.util.ActivityLauncher
import rocks.poopjournal.metadataremover.util.ActivityResultLauncher
import rocks.poopjournal.metadataremover.util.AndroidViewDslScope
import rocks.poopjournal.metadataremover.util.Logger
import rocks.poopjournal.metadataremover.util.extensions.android.*
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.get
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.observeNotNull
import rocks.poopjournal.metadataremover.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), ActivityResultLauncher, AndroidViewDslScope {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    companion object {
        private const val KEY_SLIDING_OFFSET = "SLIDING_OFFSET"
    }

    @FloatRange(from = 0.0, to = 1.0)
    private var slidingOffset: Float = 0f

    private val panelSlideListener: PanelSlideListener = InnerPanelSlideListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)

        slidingOffset = savedInstanceState
                ?.getFloat(KEY_SLIDING_OFFSET, slidingOffset)
                ?: slidingOffset

        viewModel = ViewModelProviders.of(this).get()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)!!

        initializeViews()
        panelSlideListener.onPanelSlide(binding.slidingLayout, slidingOffset)

        onPreviewUpdate(Resource.Empty())

        viewModel.wrongMimeTypeFileSelectedHint.observeNotNull(this) { event ->
            Snackbar.make(
                    binding.coordinator,
                    getString(R.string.description_snackbar_wrong_mime_type, event.mediaType),
                    Snackbar.LENGTH_LONG
            ).setAction(R.string.title_action_snackbar_wrong_mime_type) {
                viewModel.openFile()
            }
        }
        viewModel.metadata.observeNotNull(this, ::onPreviewUpdate)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState[KEY_SLIDING_OFFSET] = slidingOffset
    }

    override fun startActivity(launchInfo: ActivityLauncher.LaunchInfo) =
            startActivity(launchInfo.intent, launchInfo.options)

    @SuppressLint("RestrictedApi")
    override fun startActivityForResult(launchInfo: ActivityResultLauncher.LaunchInfo) =
            startActivityForResult(launchInfo.intent, launchInfo.requestCode, launchInfo.options)

    private fun initializeViews() {
        binding {
            preview {
                toolbar {
                    inflateMenu(R.menu.menu_main)
                    menu.findItem(R.id.menu_item_about) {
                        val tintColor = getThemeColor(android.R.attr.textColorSecondary)
                        icon = icon.tint(tintColor)
                    }
                            .onMenuItemClick(viewModel::openAboutScreen)
                }

                bannerOpenImage.setOnClickListener { viewModel.openFile() }
            }

            slidingLayout.isTouchEnabled = false

            bottomSheet {
                toolbar {
                    setOnClickListener { viewModel.openFile() }
                    inflateMenu(R.menu.menu_main_bottom_sheet)
                    menu.findItem(R.id.menu_item_open_file)
                            .onMenuItemClick(viewModel::openFile)
                    menu.findItem(R.id.menu_item_expand_collapse)
                            .actionView
                            .onClick {
                                slidingLayout {
                                    panelState = when (panelState) {
                                        PanelState.EXPANDED, PanelState.ANCHORED -> PanelState.COLLAPSED
                                        else -> PanelState.ANCHORED
                                    }
                                }
                            }
                    setNavigationOnClickListener { viewModel.closeFile() }
                }

                listMetadata {
                    adapter = MetaAttributeAdapter()
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
            }
        }
    }

    private fun onPreviewUpdate(resource: Resource<Metadata>) {
        when (resource) {
            is Empty -> {
                Logger.d("Update preview: empty")
                onPreviewClear()
            }
            is Loading -> {
                Logger.d("Update preview: loading...")
                onPreviewLoading()
            }
            is Success -> {
                val metadata = resource.value
                Logger.d("Update preview: $metadata")
                updatePreviewSuccess(metadata)
            }
        }

    }

    private fun onPreviewClear() {
        binding {
            preview {
                progress.isVisible = false

                thumbnail {
                    setImageDrawable(null)
                    isVisible = false
                }

                bannerOpenImage.isVisible = true
            }

            slidingLayout {
                panelState = PanelState.COLLAPSED
                isTouchEnabled = false
            }

            bottomSheet {
                toolbar {
                    setTitle(R.string.title_bottom_sheet_open_file)
                    navigationIcon = null
                    setOnClickListener { viewModel.openFile() }

                    menu {
                        findItem(R.id.menu_item_open_file)
                                .isVisible = true
                        findItem(R.id.menu_item_expand_collapse) {
                            isVisible = false
                            (actionView as ExpandIconView) {
                                setState(ExpandIconView.LESS, false)
                            }
                        }
                    }
                }

                listMetadata.isVisible = false
                bannerNoMetadata.isVisible = true
                buttonRemoveMetadata.isEnabled = false
            }
        }
    }

    private fun onPreviewLoading() {
        binding {
            preview {
                progress.isVisible = true

                thumbnail {
                    setImageDrawable(null)
                    isVisible = false
                }

                bannerOpenImage.isVisible = false
            }

            slidingLayout {
                panelState = PanelState.COLLAPSED
                isTouchEnabled = false
            }

            bottomSheet {
                toolbar {
                    setTitle(R.string.title_bottom_sheet_open_file)
                    navigationIcon = null
                    setOnClickListener { viewModel.openFile() }

                    menu {
                        findItem(R.id.menu_item_open_file)
                                .isVisible = true
                        findItem(R.id.menu_item_expand_collapse) {
                            isVisible = false
                            (actionView as ExpandIconView) {
                                setState(ExpandIconView.LESS, false)
                            }
                        }
                    }
                }

                listMetadata.isVisible = false
                bannerNoMetadata.isVisible = true
                buttonRemoveMetadata.isEnabled = false
            }
        }
    }

    private fun updatePreviewSuccess(metadata: Metadata) {
        binding {
            preview {
                progress.isVisible = false

                thumbnail {
                    setImage(metadata.thumbnail)
                    isVisible = true
                }

                bannerOpenImage.isVisible = false
            }

            slidingLayout.isTouchEnabled = true

            bottomSheet {
                toolbar {
                    setTitle(metadata.title!!)
                    setNavigationIcon(R.drawable.ic_close)
                    isClickable = false

                    menu {
                        findItem(R.id.menu_item_open_file).isVisible = false
                        findItem(R.id.menu_item_expand_collapse).isVisible = true
                    }
                }

                listMetadata {
                    isVisible = true

                    Logger.d("${metadata.attributes.size} metadata attributes")

                    val adapter = adapter as MetaAttributeAdapter
                    adapter.attributes = metadata.attributes
                    Logger.d("${adapter.attributes.size} metadata attributes (adapter)")
                }

                bannerNoMetadata.isVisible = false
                buttonRemoveMetadata {
                    isEnabled = true
                    setOnClickListener { viewModel.removeMetadata() }
                }
            }
        }
    }

    inner class InnerPanelSlideListener : PanelSlideListener {

        override fun onPanelSlide(
                panel: View,
                @FloatRange(from = 0.0, to = 1.0) slideOffset: Float
        ) {
            slidingOffset = slideOffset

            binding {
                val expandCollapse = bottomSheet
                        .toolbar
                        .menu
                        .findItem(R.id.menu_item_expand_collapse)
                        .actionView
                        .let { it as ExpandIconView }

                val panelHeight = slidingLayout.height - slidingLayout.panelHeight
                val absoluteOffset = panelHeight * slideOffset
                val reverseAbsoluteOffset = panelHeight * (1 - slideOffset)
                val limitedReverseAbsoluteOffset = reverseAbsoluteOffset
                        .coerceAtMost(panelHeight * (1 - slidingLayout.anchorPoint))

                navigationBarShadow {
                    val relativeOffset = absoluteOffset / height
                    alpha = (relativeOffset - 1).coerceAtMost(1f)
                }
                bottomSheet.buttonRemoveMetadata.translationY = -limitedReverseAbsoluteOffset

                if (slidingLayout.panelState == PanelState.DRAGGING) {
                    val fraction = 1 -
                            (slideOffset.coerceAtMost(slidingLayout.anchorPoint) /
                                    slidingLayout.anchorPoint)
                    expandCollapse.setFraction(fraction, false)
                }
            }
        }

        override fun onPanelStateChanged(
                panel: View,
                previousState: SlidingUpPanelLayout.PanelState,
                newState: SlidingUpPanelLayout.PanelState
        ) {
            val expandCollapse = binding.bottomSheet
                    .toolbar
                    .menu
                    .findItem(R.id.menu_item_expand_collapse)
                    .actionView
                    .let { it as ExpandIconView }
            expandCollapse {
                val state = when (newState) {
                    PanelState.COLLAPSED -> ExpandIconView.LESS to R.string.title_menu_item_collapse
                    PanelState.EXPANDED, PanelState.ANCHORED -> ExpandIconView.MORE to R.string.title_menu_item_expand
                    PanelState.HIDDEN, PanelState.DRAGGING -> return@expandCollapse
                }
                setState(state.first, true)
                contentDescription = getText(state.second)
            }

            if (newState == PanelState.COLLAPSED) {
                binding.bottomSheet.listMetadata.scrollToPosition(0)
            }
        }
    }
}

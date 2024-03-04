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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.databinding.ActivityMainBinding
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.Resource
import rocks.poopjournal.metadataremover.ui.adapter.OnLastItemClickedListener
import rocks.poopjournal.metadataremover.ui.adapter.PageAdapter
import rocks.poopjournal.metadataremover.util.extensions.android.getThemeColor
import rocks.poopjournal.metadataremover.util.extensions.android.setText
import rocks.poopjournal.metadataremover.util.extensions.android.tint
import rocks.poopjournal.metadataremover.viewmodel.MainViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnLastItemClickedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var viewPager : ViewPager2
    private lateinit var adapter: PageAdapter

    private val metadata :  ArrayList<Resource<Metadata>> = arrayListOf()

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.getPickedImageUris(uris)
                setAdapter(uris)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        viewPager = binding.preview.viewPager
        adapter = PageAdapter()
        adapter.setOnLastItemClickedListener(this)
        viewPager.adapter = adapter

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        initializeViews()
        initListeners()

        viewModel.outputMetadata.observe(this) { data ->
            metadata.add(data.last())
            setListData(metadata.lastIndex)
        }

        viewModel.clearedFile.observe(this){file ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sendIntent.putExtra(Intent.EXTRA_STREAM, file.uri)
            sendIntent.setDataAndType(file.uri, this.contentResolver.getType(file.uri))

            val chooserIntent = Intent.createChooser(
                sendIntent,
                this.getString(
                    R.string.title_intent_chooser_share_without_metadata,
                    file.name))

            startActivity(chooserIntent)
        }

        viewModel.toast.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun initializeViews() {
        binding.apply {
            val tintColor = getThemeColor(android.R.attr.textColorSecondary)
            preview.toolbar.inflateMenu(R.menu.menu_main)
            preview.toolbar.menu.findItem(R.id.menu_item_about).icon?.tint(tintColor)

            BottomSheetBehavior.from(bottomSheet.frame).apply {
                isGestureInsetBottomIgnored = true
            }

            bottomSheet.listMetadata.apply {
                adapter = MetaAttributeAdapter()
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            preview.bannerImageOpenImage.setOnClickListener {
                launchPhotoPicker()
            }

            bottomSheet.addPicture.setOnClickListener {
                launchPhotoPicker()
            }

            bottomSheet.buttonRemoveAndSave.setOnClickListener {
                viewModel.removeMetadata(preview.viewPager.currentItem, true)
            }

            bottomSheet.buttonRemoveMetadata.setOnClickListener {
                viewModel.removeMetadata(preview.viewPager.currentItem)
            }
        }
    }

    private fun initListeners(){
        binding.bottomSheet.arrowUp.setOnClickListener {
            if (adapter.isLastItem(viewPager.currentItem)){
                return@setOnClickListener
            }

            BottomSheetBehavior.from(binding.bottomSheet.frame).apply {
               state = BottomSheetBehavior.STATE_EXPANDED
            }
            binding.bottomSheet.arrowUp.visibility = View.GONE
            binding.bottomSheet.arrowDown.visibility = View.VISIBLE
        }

        binding.bottomSheet.arrowDown.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheet.frame).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
            binding.bottomSheet.arrowUp.visibility = View.VISIBLE
            binding.bottomSheet.arrowDown.visibility = View.GONE
        }

        viewPager.registerOnPageChangeCallback(object: OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                println("current Item: " + viewPager.currentItem)
                if (!adapter.isLastItem(viewPager.currentItem)){
                    setListData(position)
                    binding.bottomSheet.addPicture.visibility = View.GONE
                    binding.bottomSheet.arrowUp.visibility = View.VISIBLE
                } else {
                    binding.bottomSheet.title.text = getText(R.string.title_bottom_sheet_open_file)
                    binding.bottomSheet.addPicture.visibility = View.VISIBLE
                    binding.bottomSheet.arrowUp.visibility = View.GONE
                }
            }
        })
    }

    private fun setListData(position: Int){
        when (metadata[position]) {
            is Resource.Empty -> {
                //
            }
            is Resource.Loading -> {
                //
            }
            is Resource.Success -> {
                binding.bottomSheet.title.setText((metadata[position] as Resource.Success<Metadata>).value.title!!)
                binding.bottomSheet.listMetadata.apply {
                    val adapter = adapter
                    check(adapter is MetaAttributeAdapter)
                    adapter.attributes = (metadata[position] as Resource.Success<Metadata>).value.attributes
                }
            }
        }
    }
    private fun setAdapter(uris: List<Uri>){
        adapter.setImageUris(uris)
        binding.preview.viewPager.visibility = View.VISIBLE
        binding.preview.bannerImageOpenImage.visibility = View.GONE
        binding.preview.bannerTextOpenImage.visibility = View.GONE


        BottomSheetBehavior.from(binding.bottomSheet.frame).apply {
            isDraggable = true
        }
        binding.bottomSheet.addPicture.visibility = View.GONE
        binding.bottomSheet.arrowUp.visibility = View.VISIBLE
    }

    override fun onLastItemClicked() {
        launchPhotoPicker()
    }
    private fun launchPhotoPicker(){
        pickMultipleMedia.launch(arrayOf("image/png", "image/jpeg"))
    }
}

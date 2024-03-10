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

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import rocks.poopjournal.metadataremover.R

import rocks.poopjournal.metadataremover.databinding.ActivityAboutBinding
import rocks.poopjournal.metadataremover.util.ActivityLauncher
import rocks.poopjournal.metadataremover.util.AndroidViewDslScope
import rocks.poopjournal.metadataremover.util.extensions.android.activity
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.observeNotNull
import rocks.poopjournal.metadataremover.util.extensions.android.onClick
import rocks.poopjournal.metadataremover.viewmodel.AboutViewModel


class AboutActivity : AppCompatActivity(), ActivityLauncher, AndroidViewDslScope {

    private lateinit var binding: ActivityAboutBinding
    private val viewModel: AboutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_about)

        binding.header {
            toolbar.setNavigationOnClickListener {
                NavUtils.navigateUpFromSameTask(activity)
            }
            buttonContact.onClick(viewModel::openContact)
            buttonSupport.onClick(viewModel::openSupport)
        }

        binding.cards {
            cardOwner {
                buttonGithub.onClick(viewModel::openOwnerGithub)
                buttonWebsite.onClick(viewModel::openOwnerWebsite)
            }
            cardDesigner {
                buttonGithub.onClick(viewModel::openDesignerGithub)
                buttonWebsite.onClick(viewModel::openDesignerWebsite)
            }
            cardDeveloper {
                buttonGithub.onClick(viewModel::openDeveloperGithub)
                buttonWebsite.onClick(viewModel::openDeveloperWebsite)
            }
            cardSource {
                buttonCode.onClick(viewModel::openSourceCode)
                buttonLicense.onClick(viewModel::openSourceCodeLicense)
                buttonLibraries.onClick(viewModel::openSourceCodeLibraries)
            }
            cardContribute {
                buttonTranslate.onClick(viewModel::openTranslations)
                buttonIssue.onClick(viewModel::openReportIssue)
            }
        }

        viewModel.activityLaunchInfo.observeNotNull(this, ::startActivity)
    }

    override fun startActivity(launchInfo: ActivityLauncher.LaunchInfo) =
            startActivity(launchInfo.intent, launchInfo.options)
}

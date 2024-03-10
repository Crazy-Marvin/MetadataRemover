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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.net.toUri
import com.mikepenz.aboutlibraries.LibsBuilder
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.databinding.ActivityAboutBinding
import rocks.poopjournal.metadataremover.util.extensions.android.activity


class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.apply {
            toolbar.setNavigationOnClickListener {
                NavUtils.navigateUpFromSameTask(activity)
            }
            buttonContact.setOnClickListener {
                composeEmail()
            }
            buttonSupport.setOnClickListener {
                launchUrl(R.string.url_about_button_support)
            }
        }

        binding.cards.apply {
            cardOwner.apply {
                buttonGithub.setOnClickListener {
                    launchUrl(R.string.url_button_about_owner_github)
                }
                buttonWebsite.setOnClickListener {
                    launchUrl(R.string.url_button_about_owner_website)
                }
            }
            cardDesigner.apply {
                buttonGithub.setOnClickListener {
                    launchUrl(R.string.url_button_about_designer_github)
                }
                buttonWebsite.setOnClickListener {
                    launchUrl(R.string.url_button_about_designer_website)
                }
            }
            cardDeveloper.apply {
                buttonGithub.setOnClickListener {
                    launchUrl(R.string.url_button_about_developer_github)
                }
                buttonWebsite.setOnClickListener {
                    launchUrl(R.string.url_button_about_developer_website)
                }
            }
            cardDeveloper2.apply {
                buttonGithub.setOnClickListener {
                    launchUrl(R.string.url_button_about_developer_github2)
                }
                buttonWebsite.setOnClickListener {
                    launchUrl(R.string.url_button_about_developer_website2)
                }
            }
            cardSource.apply {
                buttonCode.setOnClickListener {
                    launchUrl(R.string.url_button_about_source_code)
                }
                buttonLicense.setOnClickListener {
                    startActivity(
                        Intent(
                            this@AboutActivity,
                            LicenseActivity::class.java
                        )
                    )

                }
                buttonLibraries.setOnClickListener {
                    startActivity(
                        LibsBuilder()
                            .apply {
                                autoDetect = true
                                activityTheme = R.style.Theme_App
                                excludeLibraries = arrayOf(
                                    "AndroidIconics",
                                    "fastadapter"
                                )
                            }
                            .intent(this@AboutActivity, LibrariesActivity::class.java)
                    )
                }
            }
            cardContribute.apply {
                buttonTranslate.setOnClickListener {
                    launchUrl(R.string.url_button_about_contribute_translate)
                }
                buttonIssue.setOnClickListener {
                    launchUrl(R.string.url_button_about_contribute_issue)
                }
            }
        }
    }

    private fun launchUrl(@StringRes urlRes: Int) {
        try {
            val url = getString(urlRes)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.can_not_handle),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun composeEmail() {
        try {
            val address = getString(R.string.address_email_about_button_contact)
            val subject = getString(R.string.subject_email_about_button_contact)
            val intent = Intent(Intent.ACTION_SENDTO, "mailto:$address".toUri())
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.can_not_handle),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

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

package rocks.poopjournal.metadataremover.viewmodel

import android.app.Application
import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import com.mikepenz.aboutlibraries.LibsBuilder
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.ui.LibrariesActivity
import rocks.poopjournal.metadataremover.ui.LicenseActivity
import rocks.poopjournal.metadataremover.util.ActivityLauncher
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.applicationContext
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.singleLiveEventOf
import rocks.poopjournal.metadataremover.util.extensions.parseUri
import rocks.poopjournal.metadataremover.util.extensions.startActivity

class AboutViewModel(application: Application) : AndroidViewModel(application), ActivityLauncherViewModel, ActivityLauncher {

    override val activityLaunchInfo = singleLiveEventOf<ActivityLauncher.LaunchInfo>()

    override fun startActivity(launchInfo: ActivityLauncher.LaunchInfo) {
        activityLaunchInfo.value = launchInfo
    }

    private fun openUrl(@StringRes urlRes: Int) {
        val url = applicationContext
                .getString(urlRes)
                .parseUri()
        val intent = Intent(Intent.ACTION_VIEW).setData(url)
        if (intent.resolveActivity(applicationContext.packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun composeEmail(@StringRes addressRes: Int, @StringRes subjectRes: Int) {
        val address = applicationContext
                .getString(addressRes)
        val subject = applicationContext
                .getString(subjectRes)
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(applicationContext.packageManager) != null) {
            startActivity(intent)
        }
    }

    fun openSourceCode() = openUrl(R.string.url_button_about_source_code)

    fun openSourceCodeLicense() = startActivity(Intent(
            applicationContext,
            LicenseActivity::class.java
    ))

    fun openSourceCodeLibraries() = startActivity(
            LibsBuilder()
                    .apply {
                        autoDetect = true
                        activityTheme = R.style.Theme_App
                        excludeLibraries = arrayOf(
                                "AndroidIconics",
                                "fastadapter"
                        )
                    }
                    .intent(applicationContext, LibrariesActivity::class.java)
    )

    fun openContact() = composeEmail(
            R.string.address_email_about_button_contact,
            R.string.subject_email_about_button_contact
    )

    fun openSupport() = openUrl(R.string.url_about_button_support)

    fun openDeveloperGithub() = openUrl(R.string.url_button_about_developer_github)

    fun openDeveloperWebsite() = openUrl(R.string.url_button_about_developer_website)
}
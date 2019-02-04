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

package rocks.poopjournal.metadataremover.util.extensions

import android.content.Intent
import android.os.Bundle
import rocks.poopjournal.metadataremover.util.ActivityLauncher
import rocks.poopjournal.metadataremover.util.ActivityResultLauncher

/**
 * Launch a new activity.  You will not receive any information about when
 * the activity exits.
 *
 * @param intent The description of the activity to start.
 * @param options Additional options for how the Activity should be started.
 * May be null if there are no options.  See [android.app.ActivityOptions]
 * for how to build the Bundle supplied here; there are no supported definitions
 * for building it manually.
 *
 * @see android.content.Context.startActivity
 */
fun ActivityLauncher.startActivity(intent: Intent, options: Bundle? = null) =
        startActivity(ActivityLauncher.LaunchInfo(intent, options))

/**
 * Launch an activity for which you would like a result when it finished.
 * When this activity exits, your [android.app.Activity.onActivityResult]
 * method will be called with the given [launchInfo#requestCode].
 * Using a negative [requestCode] is the same as calling
 * [startActivity] (the activity is not launched as a sub-activity).
 *
 * @param intent The description of the activity to start.
 * @param requestCode If >= 0, this code will be returned in
 * [android.app.Activity.onActivityResult] when the activity exits.
 * @param options Additional options for how the Activity should be started.
 * May be null if there are no options.  See [android.app.ActivityOptions]
 * for how to build the Bundle supplied here; there are no supported definitions
 * for building it manually.
 *
 * @see android.app.Activity.startActivity
 */
fun ActivityResultLauncher.startActivityForResult(
        intent: Intent,
        requestCode: Int,
        options: Bundle? = null) =
        startActivityForResult(ActivityResultLauncher.LaunchInfo(intent, requestCode, options))

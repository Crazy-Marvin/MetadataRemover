/*
 * MIT License
 *
 * Copyright (c) 2017 Jan Heinrich Reimer
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

package rocks.poopjournal.metadataremover.util.extensions.android

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import java.lang.ref.WeakReference

/**
 * Calls the specified function [body] with the [shared preferences][getPreferences] as
 * its receiver returns the function's return.
 *
 * @param name Desired preferences file. If a preferences file by this uri
 *  does not exist, it will be created when you retrieve an
 *  [editor][SharedPreferences.edit] and then [commit changes][SharedPreferences.Editor.commit].
 *
 * @return The [specified function's][body] return value.
 *
 * @see getPreferences
 */
inline fun <R> Context.preferences(name: String? = null, body: SharedPreferences.() -> R): R {
    return getPreferences(name).body()
}

/**
 * Retrieve and hold the contents of the preferences file [name], returning
 * a [SharedPreferences] through which you can retrieve and modify its
 * values.  Only one instance of the [SharedPreferences] object is returned
 * to any callers for the same [name], meaning they will see each other's
 * edits as soon as they are made.
 *
 * All [SharedPreferences] objects returned by this method operate
 * in the [file creation mode][Context.MODE_PRIVATE].
 *
 * @param name Desired preferences file. If a preferences file by this uri
 *  does not exist, it will be created when you retrieve an
 *  [editor][SharedPreferences.edit] and then [commit changes][SharedPreferences.Editor.commit].
 *
 * @return The single [SharedPreferences] instance that can be used
 *  to retrieve and modify the preference values.
 */
fun Context.getPreferences(name: String? = null): SharedPreferences {
    return when (name) {
        null -> PreferenceManager.getDefaultSharedPreferences(this)
        else -> getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}

/**
 * Calls the specified function [body] with the [shared preferences][getPreferences] as
 * its receiver and returns the function's return value.
 *
 * @param name Desired preferences file. If a preferences file by this uri
 *  does not exist, it will be created when you retrieve an
 *  [editor][SharedPreferences.edit] and then [commit changes][SharedPreferences.Editor.commit].
 *
 * @return The [specified function's][body] return value.
 *
 * @see getPreferences
 */
inline fun <R> WeakReference<Context>.preferences(name: String? = null, body: SharedPreferences.() -> R): R? {
    return getPreferences(name)?.body()
}

/**
 * Retrieve and hold the contents of the preferences file [name], returning
 * a [SharedPreferences] through which you can retrieve and modify its
 * values.  Only one instance of the [SharedPreferences] object is returned
 * to any callers for the same [name], meaning they will see each other's
 * edits as soon as they are made.
 *
 * All [SharedPreferences] objects returned by this method operate
 * in the [file creation mode][Context.MODE_PRIVATE].
 *
 * @param name Desired preferences file. If a preferences file by this uri
 *  does not exist, it will be created when you retrieve an
 *  [editor][SharedPreferences.edit] and then [commit changes][SharedPreferences.Editor.commit].
 *
 * @return The single [SharedPreferences] instance that can be used
 *  to retrieve and modify the preference values.
 */
fun WeakReference<Context>.getPreferences(name: String? = null): SharedPreferences? {
    val context = get()
    return when (context) {
        null -> null
        else -> context.getPreferences(name)
    }
}

/**
 * Calls the specified function [body] with the
 * [shared preferences editor][SharedPreferences.edit] as its receiver and
 * returns the function's return value.
 *
 * @return The [specified function's][body] return value.
 *
 * @see SharedPreferences.edit
 */
inline fun <R> SharedPreferences.edit(body: SharedPreferences.Editor.() -> R): R {
    val editor = edit()
    val result = editor.body()
    editor.apply()
    return result
}


/**
 * Retrieve an [Int] value from the [preferences][SharedPreferences].
 *
 * @param key The uri of the preference to retrieve.
 * @param defValue Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [defValue].
 *  Throws [ClassCastException] if there is a preference with this uri that is not an [Int].
 *
 * @throws ClassCastException
 */
operator fun SharedPreferences.get(key: String, defValue: Int) = getInt(key, defValue)

/**
 * Retrieve a [Boolean] value from the [preferences][SharedPreferences].
 *
 * @param key The uri of the preference to retrieve.
 * @param defValue Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [defValue].
 *  Throws [ClassCastException] if there is a preference with this uri that is not a [Boolean].
 *
 * @throws ClassCastException
 */
operator fun SharedPreferences.get(key: String, defValue: Boolean) = getBoolean(key, defValue)

/**
 * Retrieve a [Float] value from the [preferences][SharedPreferences].
 *
 * @param key The uri of the preference to retrieve.
 * @param defValue Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [defValue].
 *  Throws [ClassCastException] if there is a preference with this uri that is not a [Float].
 *
 * @throws ClassCastException
 */
operator fun SharedPreferences.get(key: String, defValue: Float) = getFloat(key, defValue)

/**
 * Retrieve a [Long] value from the [preferences][SharedPreferences].
 *
 * @param key The uri of the preference to retrieve.
 * @param defValue Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [defValue].
 *  Throws [ClassCastException] if there is a preference with this uri that is not an [Long].
 *
 * @throws ClassCastException
 */
operator fun SharedPreferences.get(key: String, defValue: Long) = getLong(key, defValue)

/**
 * Retrieve a [String] value from the [preferences][SharedPreferences].
 *
 * @param key The uri of the preference to retrieve.
 * @param defValue Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [defValue].
 *  Throws [ClassCastException] if there is a preference with this uri that is not an [String].
 *
 * @throws ClassCastException
 */
operator fun SharedPreferences.get(key: String, defValue: String) = getString(key, defValue)
        ?: defValue

/**
 * Retrieve a [`Set<String>`][Set] value from the [preferences][SharedPreferences].
 *
 * @param key The uri of the preference to retrieve.
 * @param defValue Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [defValue].
 *  Throws [ClassCastException] if there is a preference with this uri that is not
 *  an [`Set<String>`][Set].
 *
 * @throws ClassCastException
 */
operator fun SharedPreferences.get(key: String, defValue: Set<String>): Set<String> = getStringSet(key, defValue)
        ?: defValue


/**
 * Set an [Int] value in the preferences [editor][SharedPreferences.Editor], to be written back
 * once [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply] are called.
 *
 * @param key The uri of the preference to modify.
 * @param value The new value for the preference.
 *
 * @return Returns a reference to the same [SharedPreferences.Editor] object,
 *  so you can chain set calls together.
 */
operator fun SharedPreferences.Editor.set(key: String, value: Int): Editor = putInt(key, value)

/**
 * Set a [Boolean] value in the preferences [editor][SharedPreferences.Editor], to be written back
 * once [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply] are called.
 *
 * @param key The uri of the preference to modify.
 * @param value The new value for the preference.
 *
 * @return Returns a reference to the same [SharedPreferences.Editor] object,
 *  so you can chain set calls together.
 */
operator fun SharedPreferences.Editor.set(key: String, value: Boolean): Editor = putBoolean(key, value)

/**
 * Set a [Float] value in the preferences [editor][SharedPreferences.Editor], to be written back
 * once [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply] are called.
 *
 * @param key The uri of the preference to modify.
 * @param value The new value for the preference.
 *
 * @return Returns a reference to the same [SharedPreferences.Editor] object,
 *  so you can chain set calls together.
 */
operator fun SharedPreferences.Editor.set(key: String, value: Float): Editor = putFloat(key, value)

/**
 * Set a [Long] value in the preferences [editor][SharedPreferences.Editor], to be written back
 * once [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply] are called.
 *
 * @param key The uri of the preference to modify.
 * @param value The new value for the preference.
 *
 * @return Returns a reference to the same [SharedPreferences.Editor] object,
 *  so you can chain set calls together.
 */
operator fun SharedPreferences.Editor.set(key: String, value: Long): Editor = putLong(key, value)

/**
 * Set a [String] value in the preferences [editor][SharedPreferences.Editor], to be written back
 * once [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply] are called.
 *
 * @param key The uri of the preference to modify.
 * @param value The new value for the preference.
 *
 * @return Returns a reference to the same [SharedPreferences.Editor] object,
 *  so you can chain set calls together.
 */
operator fun SharedPreferences.Editor.set(key: String, value: String): Editor = putString(key, value)

/**
 * Set a [`Set<String>`][Set] value in the preferences [editor][SharedPreferences.Editor],
 * to be written back once [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply]
 * are called.
 *
 * @param key The uri of the preference to modify.
 * @param value The new value for the preference.
 *
 * @return Returns a reference to the same [SharedPreferences.Editor] object,
 *  so you can chain set calls together.
 */
operator fun SharedPreferences.Editor.set(key: String, value: Set<String>): Editor = putStringSet(key, value)


/**
 * Retrieve a [MutableLiveData] wrapper around an [Int] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getIntLiveData(key: String, defValue: Int): MutableLiveData<Int> = PreferenceLiveData(this, key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [Boolean] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getBooleanLiveData(key: String, defValue: Boolean): MutableLiveData<Boolean> = PreferenceLiveData(this, key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [Float] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getFloatLiveData(key: String, defValue: Float): MutableLiveData<Float> = PreferenceLiveData(this, key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [Long] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getLongLiveData(key: String, defValue: Long): MutableLiveData<Long> = PreferenceLiveData(this, key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [String] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getStringLiveData(key: String, defValue: String): MutableLiveData<String> = PreferenceLiveData(this, key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [`Set<String>`][Set] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getStringSetLiveData(key: String, defValue: Set<String>):
        MutableLiveData<Set<String>> = PreferenceLiveData(this, key, defValue)


/**
 * Retrieve a [MutableLiveData] wrapper around an [Int] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getLiveData(key: String, defValue: Int): MutableLiveData<Int> = getIntLiveData(key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [Boolean] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getLiveData(key: String, defValue: Boolean): MutableLiveData<Boolean> = getBooleanLiveData(key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [Float] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getLiveData(key: String, defValue: Float): MutableLiveData<Float> = getFloatLiveData(key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [String] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getLiveData(key: String, defValue: Long): MutableLiveData<Long> = getLongLiveData(key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [String] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getLiveData(key: String, defValue: String): MutableLiveData<String> = getStringLiveData(key, defValue)

/**
 * Retrieve a [MutableLiveData] wrapper around a [`Set<String>`][Set] [preference][SharedPreferences]
 * that supports supports reading and writing the preference and updates
 * all [observers][PreferenceLiveData.observe] when the preference has been changed elsewhere.
 */
fun SharedPreferences.getLiveData(key: String, defValue: Set<String>): MutableLiveData<Set<String>> = getStringSetLiveData(key, defValue)


/**
 * A [MutableLiveData] wrapper around a [preference][SharedPreferences] that supports
 * reading and writing the preference and updates all [observers][observe] when the preference
 * has been changed elsewhere.
 */
private class PreferenceLiveData<T : Any>(
        private val preferences: SharedPreferences,
        private val key: String,
        private val defValue: T) : MutableLiveData<T>() {

    init {
        if (!checkValidType(defValue)) {
            throw IllegalArgumentException("Can only handle Int, Boolean, Float, Long, " +
                    "String and Set<String>.")
        }
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, updatedKey ->
        if (updatedKey == key) {
            // Fetch the current value from SharedPreferences and set.
            super.postValue(value)
        }
    }

    /**
     * Retrieve this preference's value.
     *
     * @return Returns the preference value if it exists, or [defValue].
     *  Throws [ClassCastException] if there is a preference with this uri that is not of type [T].
     *
     * @throws ClassCastException
     */
    @Suppress("UNCHECKED_CAST")
    override fun getValue(): T = when (defValue) {
        is Int -> preferences[key, defValue] as T
        is Boolean -> preferences[key, defValue] as T
        is Float -> preferences[key, defValue] as T
        is Long -> preferences[key, defValue] as T
        is String -> preferences[key, defValue] as T
        is Set<*> -> preferences[key, defValue as Set<String>] as T
        else -> throw IllegalStateException("Should never happen. The type was checked before.")
    }

    /**
     * Set an value of type [T] to the preference, to be written back asynchronously.
     *
     * @param value The new value for the preference.
     */
    override fun setValue(value: T) {
        postValue(value)
    }

    /**
     * Set an value of type [T] to the preference, to be written back asynchronously.
     *
     * @param value The new value for the preference.
     */
    override fun postValue(value: T) {
        checkValidType(value)

        unregisterListener()

        val editor = preferences.edit()
        @Suppress("UNCHECKED_CAST")
        when (value) {
            is Int -> editor[key] = value
            is Boolean -> editor[key] = value
            is Float -> editor[key] = value
            is Long -> editor[key] = value
            is String -> editor[key] = value
            is Set<*> -> editor[key] = value as Set<String>
            else -> throw IllegalStateException("Should never happen. The type was checked before.")
        }
        editor.apply()

        registerListener()
    }

    private fun checkValidType(value: T) = when (value) {
        is Int -> true
        is Boolean -> true
        is Float -> true
        is Long -> true
        is String -> true
        is Set<*> -> true
        else -> false
    }

    private fun registerListener() {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun unregisterListener() {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun onActive() {
        registerListener()
    }

    override fun onInactive() {
        unregisterListener()
    }
}
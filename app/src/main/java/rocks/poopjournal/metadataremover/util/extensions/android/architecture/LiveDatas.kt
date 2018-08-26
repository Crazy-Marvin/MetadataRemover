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

package rocks.poopjournal.metadataremover.util.extensions.android.architecture

import androidx.annotation.MainThread
import androidx.lifecycle.*
import rocks.poopjournal.metadataremover.util.SingleLiveEvent

/**
 * Adds the given observer to the observers list within the lifespan of the given
 * owner. The events are dispatched on the main thread. If [LiveData] already has data
 * set, it will be delivered to the observer.
 *
 * The observer will only receive events if the owner is in
 * [androidx.lifecycle.Lifecycle.State.STARTED]
 * or [androidx.lifecycle.Lifecycle.State.RESUMED] state (active).
 *
 * If the owner moves to the [androidx.lifecycle.Lifecycle.State.DESTROYED] state,
 * the observer will automatically be removed.
 *
 * When data changes while the [owner] is not active, it will not receive any updates.
 * If it becomes active again, it will receive the last available data automatically.
 *
 * [LiveData] keeps a strong reference to the observer and the owner as long as the
 * given [LifecycleOwner] is not destroyed. When it is destroyed, [LiveData] removes references to
 * the observer & the owner.
 *
 * If the given owner is already in [androidx.lifecycle.Lifecycle.State.DESTROYED] state,
 * [LiveData] ignores the call.
 *
 * If the given owner, observer tuple is already in the list, the call is ignored.
 * If the observer is already in the list with another owner, [LiveData] throws an
 * [IllegalArgumentException].
 *
 * @param owner The [LifecycleOwner] which controls the observer.
 * @param observer The observer that will receive the events.
 */
@MainThread
inline fun <DataType> LiveData<DataType>.observe(
        owner: LifecycleOwner,
        crossinline observer: (data: DataType?) -> Unit) {
    observe(owner, Observer<DataType> { data -> observer(data) })
}

/**
 * Adds the given observer to the observers list within the lifespan of the given
 * owner. The events are dispatched on the main thread. If [LiveData] already has data
 * set, it will be delivered to the observer.
 *
 * The observer will only receive events if the owner is in
 * [androidx.lifecycle.Lifecycle.State.STARTED]
 * or [androidx.lifecycle.Lifecycle.State.RESUMED] state (active).
 *
 * If the owner moves to the [androidx.lifecycle.Lifecycle.State.DESTROYED] state,
 * the observer will automatically be removed.
 *
 * When data changes while the [owner] is not active, it will not receive any updates.
 * If it becomes active again, it will receive the last available data automatically.
 *
 * [LiveData] keeps a strong reference to the observer and the owner as long as the
 * given [LifecycleOwner] is not destroyed. When it is destroyed, [LiveData] removes references to
 * the observer & the owner.
 *
 * If the given owner is already in [androidx.lifecycle.Lifecycle.State.DESTROYED] state,
 * [LiveData] ignores the call.
 *
 * If the given owner, observer tuple is already in the list, the call is ignored.
 * If the observer is already in the list with another owner, [LiveData] throws an
 * [IllegalArgumentException].
 *
 * @param owner The [LifecycleOwner] which controls the observer.
 * @param observer The observer that will receive the events.
 */
@MainThread
inline fun <DataType> LiveData<DataType>.observeNotNull(
        owner: LifecycleOwner,
        crossinline observer: (data: DataType) -> Unit) {
    observe(owner) { data ->
        if (data != null) {
            observer(data)
        }
    }
}

/**
 * Adds the given observer to the observers list. This call is similar to
 * [LiveData.observe] with a [LifecycleOwner], which
 * is always active. This means that the given observer will receive all events and will never
 * be automatically removed. You should manually call [LiveData.removeObserver] to stop
 * observing this [LiveData].
 * While [LiveData] has one of such observers, it will be considered
 * as active.
 *
 * If the observer was already added with an owner to this [LiveData], [LiveData] throws an
 * [IllegalArgumentException].
 *
 * @param observer The observer that will receive the events.
 */
@MainThread
inline fun <DataType> LiveData<DataType>.observeForeverNotNull(
        crossinline observer: (data: DataType) -> Unit) {
    observeForever { data ->
        if (data != null) {
            observer(data)
        }
    }
}

/**
 * Map [LiveData]s.
 */
inline fun <FromType, ToType> LiveData<FromType>.map(
        crossinline transform: (FromType) -> ToType): LiveData<ToType> {
    return Transformations.map(this) { input -> transform(input) }
}

@MainThread
fun <ValueType> liveDataOf(value: ValueType): LiveData<ValueType> {
    return object : LiveData<ValueType>() {
        init {
            this.value = value
        }
    }
}

@MainThread
fun <ValueType> mutableLiveDataOf(initialValue: ValueType? = null): MutableLiveData<ValueType> {
    val liveData = MutableLiveData<ValueType>()
    if (initialValue != null) {
        liveData.value = initialValue
    }
    return liveData
}

@MainThread
fun <ValueType> singleLiveEventOf(initialValue: ValueType? = null): SingleLiveEvent<ValueType> {
    val liveData = SingleLiveEvent<ValueType>()
    if (initialValue != null) {
        liveData.value = initialValue
    }
    return liveData
}
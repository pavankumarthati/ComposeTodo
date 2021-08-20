package com.masterbit.composetodo.common

import androidx.lifecycle.Observer

class Event<out T>(private val content: T) {

    var hasBeenHandled: Boolean = false
        private set

    fun getContentIfNotHandled(): T? {
        if (hasBeenHandled) return null
        hasBeenHandled = true
        return content
    }

    fun peekContent(): T {
        return content
    }
}

class EventObserver<T>(private val eventHandler: (T) -> Unit): Observer<Event<T>> {
    override fun onChanged(t: Event<T>?) {
        t?.getContentIfNotHandled()?.let {
            eventHandler(it)
        }
    }

}
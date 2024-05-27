package com.app.basestructuremvvmdsl.core

class EventLoading<T>(var value: T)
{

    private var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            value
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = value
}

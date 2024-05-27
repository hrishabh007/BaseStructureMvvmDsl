package com.app.basestructuremvvmdsl.di

import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Qualifier

@[Retention(AnnotationRetention.RUNTIME) Qualifier]
annotation class DefaultDispatcher

@[Retention(AnnotationRetention.RUNTIME) Qualifier Deprecated("use CoroutineDispatchers")]
annotation class IoDispatcher

@[Retention(AnnotationRetention.RUNTIME) Qualifier]
annotation class MainDispatcher

@[Retention(AnnotationRetention.RUNTIME) Qualifier]
annotation class MainImmediateDispatcher

interface CoroutineDispatchers {
    val IO: CoroutineDispatcher
    val Main: CoroutineDispatcher
    val Default: CoroutineDispatcher
}

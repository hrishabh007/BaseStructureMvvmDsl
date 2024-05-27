package com.app.basestructuremvvmdsl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
object CoroutinesModule {

    @[Singleton Provides]
    fun providesCoroutineScope(dispatchers: CoroutineDispatchers): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatchers.Main + CoroutineName("\uD83D\uDE44 Singleton Scope"))
    }

    @[Singleton Provides]
    fun providesCoroutineDispatchers(): CoroutineDispatchers = object : CoroutineDispatchers {
        override val IO: CoroutineDispatcher get() = Dispatchers.IO
        override val Main: CoroutineDispatcher get() = Dispatchers.Main
        override val Default: CoroutineDispatcher get() = Dispatchers.Default
    }

}
package com.revrank.di

import com.revrank.data.repository.LeaderboardRepositoryImpl
import com.revrank.domain.repository.LeaderboardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds repository interfaces to their implementations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(
        impl: LeaderboardRepositoryImpl
    ): LeaderboardRepository
}

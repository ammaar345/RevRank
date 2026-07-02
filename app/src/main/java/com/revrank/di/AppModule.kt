package com.revrank.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.revrank.data.local.RevRankDatabase
import com.revrank.data.revenuecat.PurchaseManager
import com.revrank.data.repository.AuthRepository
import com.revrank.data.repository.TripRepository
import com.revrank.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideRevRankDatabase(@ApplicationContext context: Context): RevRankDatabase {
        return Room.databaseBuilder(
            context,
            RevRankDatabase::class.java,
            "revrank_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTripDao(database: RevRankDatabase) = database.tripDao()

    @Provides
    fun provideUserDao(database: RevRankDatabase) = database.userDao()

    @Provides
    fun provideBadgeDao(database: RevRankDatabase) = database.badgeDao()

    @Provides
    @Singleton
    fun providePurchaseManager(): PurchaseManager = PurchaseManager()
}

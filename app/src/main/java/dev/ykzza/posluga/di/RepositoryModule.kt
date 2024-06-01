package dev.ykzza.posluga.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ykzza.posluga.data.repository.AuthRepository
import dev.ykzza.posluga.data.repository.AuthRepositoryImpl
import dev.ykzza.posluga.data.repository.UserRepository
import dev.ykzza.posluga.data.repository.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth
    ): AuthRepository {
        return AuthRepositoryImpl(auth, db)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        db: FirebaseFirestore,
        storage: StorageReference
    ): UserRepository {
        return UserRepositoryImpl(db, storage)
    }
}
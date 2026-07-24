package com.abdulaziz733.kinetron.di

import com.abdulaziz733.kinetron.data.repository.DeviceDataRepository
import com.abdulaziz733.kinetron.data.repository.DeviceDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDeviceDataRepository(
        impl: DeviceDataRepositoryImpl
    ): DeviceDataRepository
}

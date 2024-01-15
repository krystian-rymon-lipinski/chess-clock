package com.krystian.chessclock.dependency_injection

import com.krystian.chessclock.data_layer.CustomGameDataSource
import com.krystian.chessclock.data_layer.CustomGameDataSourceImpl
import com.krystian.chessclock.data_layer.CustomMatchDataSource
import com.krystian.chessclock.data_layer.CustomMatchDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataLayerModule {

    @Binds
    @ViewModelScoped
    abstract fun bindCustomMatchDataSource(impl: CustomMatchDataSourceImpl) : CustomMatchDataSource


    @Binds
    @ViewModelScoped
    abstract fun bindCustomGameDataSource(impl: CustomGameDataSourceImpl) : CustomGameDataSource
}
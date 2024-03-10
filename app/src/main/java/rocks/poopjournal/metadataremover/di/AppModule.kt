package rocks.poopjournal.metadataremover.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rocks.poopjournal.metadataremover.viewmodel.usecases.GetDescriptor
import rocks.poopjournal.metadataremover.viewmodel.usecases.GetFileUri
import rocks.poopjournal.metadataremover.viewmodel.usecases.MetadataHandler
import rocks.poopjournal.metadataremover.viewmodel.usecases.SharedImages
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDescriptor (@ApplicationContext context: Context) : GetDescriptor{
        return GetDescriptor(context = context)
    }

    @Provides
    @Singleton
    fun providesMetadataHandler (@ApplicationContext context: Context) : MetadataHandler{
        return MetadataHandler(context = context)
    }

    @Provides
    @Singleton
    fun providesSharedImages (@ApplicationContext context: Context) : SharedImages {
        return SharedImages(context = context)
    }

    @Provides
    @Singleton
    fun providesFileUri(@ApplicationContext context: Context) : GetFileUri {
        return  GetFileUri(context = context)
    }

}
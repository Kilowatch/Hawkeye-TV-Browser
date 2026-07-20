package za.kilowatch.hawkeyetvbrowser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import za.kilowatch.hawkeyetvbrowser.core.network.DoHProvider
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(doHProvider: DoHProvider): OkHttpClient {
        val bootstrapClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val builder = bootstrapClient.newBuilder()
        doHProvider.configureDns(builder, bootstrapClient)
        return builder.build()
    }
}

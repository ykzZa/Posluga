package dev.ykzza.posluga.di

import android.app.Application
import android.widget.ArrayAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ykzza.posluga.R
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("categories")
    fun provideCategories(application: Application): Array<String> {
        return application.resources.getStringArray(R.array.categories)
    }

    @Provides
    @Singleton
    @Named("subCategories")
    fun provideSubCategories(): Array<Int> {
        return arrayOf(
            R.array.tutoring_subcategory,
            R.array.building_works_subcategory,
            R.array.equipment_repair_subcategory,
            R.array.handyman_subcategory,
            R.array.cleaning_subcategory,
            R.array.other_subcategory
        )
    }

    @Provides
    @Singleton
    @Named("states")
    fun provideStates(application: Application): Array<String> {
        return application.resources.getStringArray(R.array.states)
    }

    @Provides
    @Singleton
    @Named("cities")
    fun provideCities(): Array<Int> {
        return arrayOf(
            R.array.avtonomna_respublika_krym_cities,
            R.array.vinnytska_cities,
            R.array.volynska_cities,
            R.array.dnipropetrovska_cities,
            R.array.donetska_cities,
            R.array.zhytomyrska_cities,
            R.array.zakarpatska_cities,
            R.array.zaporizka_cities,
            R.array.ivano_frankivska_cities,
            R.array.kyivska_cities,
            R.array.kirovohradska_cities,
            R.array.luhanska_cities,
            R.array.lvivska_cities,
            R.array.mykolaivska_cities,
            R.array.odeska_cities,
            R.array.poltavska_cities,
            R.array.rivnenska_cities,
            R.array.sumska_cities,
            R.array.ternopilska_cities,
            R.array.kharkivska_cities,
            R.array.khersonska_cities,
            R.array.khmelnytska_cities,
            R.array.cherkaska_cities,
            R.array.chernivetska_cities,
            R.array.chernihivska_cities
        )
    }

    @Provides
    @Singleton
    @Named("categoryAdapter")
    fun provideCategoryAdapter(application: Application): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            application,
            R.array.categories,
            android.R.layout.simple_dropdown_item_1line
        )
    }

    @Provides
    @Singleton
    @Named("statesAdapter")
    fun provideStatesAdapter(application: Application): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            application,
            R.array.states,
            android.R.layout.simple_dropdown_item_1line
        )
    }
}
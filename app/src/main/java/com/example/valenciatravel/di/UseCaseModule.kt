package com.example.valenciatravel.di

import com.example.valenciatravel.domain.repository.CategoryRatingRepository
import com.example.valenciatravel.domain.repository.FavouriteRepository
import com.example.valenciatravel.domain.repository.PreferencesRepository
import com.example.valenciatravel.domain.repository.UserRepository
import com.example.valenciatravel.domain.usecase.AddPlaceToFavouritesUseCase
import com.example.valenciatravel.domain.usecase.DeleteUserUseCase
import com.example.valenciatravel.domain.usecase.GetCurrentUserUseCase
import com.example.valenciatravel.domain.usecase.GetFavouritePlaceIdsUseCase
import com.example.valenciatravel.domain.usecase.GetFavouritePlacesUseCase
import com.example.valenciatravel.domain.usecase.GetPreferencesUseCase
import com.example.valenciatravel.domain.usecase.IsGuestModeUseCase
import com.example.valenciatravel.domain.usecase.IsPlaceFavouriteUseCase
import com.example.valenciatravel.domain.usecase.LoginAsGuestUseCase
import com.example.valenciatravel.domain.usecase.LoginUserUseCase
import com.example.valenciatravel.domain.usecase.RegisterUserUseCase
import com.example.valenciatravel.domain.usecase.RemovePlaceFromFavouritesUseCase
import com.example.valenciatravel.domain.usecase.SaveCategoryRatingUseCase
import com.example.valenciatravel.domain.usecase.UpdateCategoryWeightUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideRegisterUserUseCase(repository: UserRepository): RegisterUserUseCase {
        return RegisterUserUseCase(repository)
    }

    @Provides
    fun provideLoginUserUseCase(repository: UserRepository): LoginUserUseCase {
        return LoginUserUseCase(repository)
    }

    @Provides
    fun provideGetCurrentUserUseCase(repository: UserRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(repository)
    }

    @Provides
    fun provideDeleteUserUseCase(repository: UserRepository): DeleteUserUseCase {
        return DeleteUserUseCase(repository)
    }

    @Provides
    fun provideGetFavouritePlaceIdsUseCase(repository: FavouriteRepository): GetFavouritePlaceIdsUseCase {
        return GetFavouritePlaceIdsUseCase(repository)
    }

    @Provides
    fun provideGetFavouritePlacesUseCase(
        repository: FavouriteRepository,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ): GetFavouritePlacesUseCase {
        return GetFavouritePlacesUseCase(repository, getCurrentUserUseCase)
    }

    @Provides
    fun provideAddPlaceToFavouritesUseCase(
        repository: FavouriteRepository,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ): AddPlaceToFavouritesUseCase {
        return AddPlaceToFavouritesUseCase(repository, getCurrentUserUseCase)
    }

    @Provides
    fun provideRemovePlaceFromFavouritesUseCase(
        repository: FavouriteRepository,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ): RemovePlaceFromFavouritesUseCase {
        return RemovePlaceFromFavouritesUseCase(repository, getCurrentUserUseCase)
    }

    @Provides
    fun provideIsPlaceFavouriteUseCase(
        repository: FavouriteRepository,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ): IsPlaceFavouriteUseCase {
        return IsPlaceFavouriteUseCase(repository, getCurrentUserUseCase)
    }

    @Provides
    fun provideGetPreferencesUseCase(
        repository: PreferencesRepository,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ): GetPreferencesUseCase {
        return GetPreferencesUseCase(repository, getCurrentUserUseCase)
    }

    @Provides
    fun provideUpdateCategoryWeightUseCase(
        repository: PreferencesRepository,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ): UpdateCategoryWeightUseCase {
        return UpdateCategoryWeightUseCase(repository, getCurrentUserUseCase)
    }

    @Provides
    fun provideLoginAsGuestUseCase(repository: UserRepository): LoginAsGuestUseCase {
        return LoginAsGuestUseCase(repository)
    }

    @Provides
    fun provideIsGuestModeUseCase(repository: UserRepository): IsGuestModeUseCase {
        return IsGuestModeUseCase(repository)
    }

    @Provides
    fun provideSaveCategoryRatingUseCase(
        repository: CategoryRatingRepository,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ): SaveCategoryRatingUseCase {
        return SaveCategoryRatingUseCase(repository, getCurrentUserUseCase)
    }

}
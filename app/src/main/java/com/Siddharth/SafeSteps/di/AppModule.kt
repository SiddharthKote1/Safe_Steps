package com.Siddharth.SafeSteps.di

import com.Siddharth.SafeSteps.data.ApiService
import com.Siddharth.SafeSteps.data.RetrofitClient
import com.Siddharth.SafeSteps.repository.*
import com.Siddharth.SafeSteps.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.Siddharth.SafeSteps.PreferencesHelper

val appModule = module {
    // Network Layer
    single<ApiService> { RetrofitClient.apiService }
    single { PreferencesHelper(androidContext()) }

    // Repositories
    single { AdminRepository(get()) }
    single { AnalyticsRepository(get()) }
    single { AuthRepository(get()) }
    single { ContactRepository(get()) }
    single { ConversationRepository(get()) }
    single { EmergencyRepository(get()) }
    single { LocationRepository(get()) }
    single { NotificationRepository(get()) }
    single { PermissionRepository(get()) }
    single { ProfileRepository(get()) }
    single { ReportRepository(get()) }
    single { TtsRepository(get()) }

    // ViewModels
    viewModel { AnalyticsViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { ContactsViewModel(get()) }
    viewModel { ConversationViewModel(get()) }
    viewModel { EmergencyViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}

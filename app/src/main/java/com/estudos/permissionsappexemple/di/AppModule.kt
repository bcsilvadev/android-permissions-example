package com.estudos.permissionsappexemple.di

import android.content.Context
import com.estudos.permissionsappexemple.data.manager.CameraManager
import com.estudos.permissionsappexemple.data.repository.PermissionRepository
import com.estudos.permissionsappexemple.data.repository.PermissionRepositoryImpl
import com.estudos.permissionsappexemple.domain.usecase.CheckPermissionUseCase
import com.estudos.permissionsappexemple.domain.usecase.GetRequiredPermissionsUseCase
import com.estudos.permissionsappexemple.presentation.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Módulo de injeção de dependências usando Koin.
 * 
 * Este módulo configura todas as dependências do app usando Koin Annotations.
 * 
 * ESTRUTURA:
 * - Data Layer: PermissionRepository, CameraManager
 * - Domain Layer: UseCases
 * - Presentation Layer: ViewModels
 * 
 * COMO USAR EM OUTROS PROJETOS:
 * 1. Copie este módulo
 * 2. Ajuste os pacotes conforme sua estrutura
 * 3. Adicione novas dependências seguindo o mesmo padrão
 */
val appModule = module {
    // ========== DATA LAYER ==========
    
    // PermissionRepository: Interface e implementação
    singleOf(::PermissionRepositoryImpl) { bind<PermissionRepository>() }
    
    // CameraManager: Gerencia operações de câmera
    factory { (authority: String) ->
        CameraManager(
            context = androidContext(),
            authority = authority
        )
    }
    
    // ========== DOMAIN LAYER ==========
    
    // UseCases
    factoryOf(::CheckPermissionUseCase)
    factoryOf(::GetRequiredPermissionsUseCase)
    
    // ========== PRESENTATION LAYER ==========
    
    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
}


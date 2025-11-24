package com.estudos.permissionsappexemple

import android.app.Application
import com.estudos.permissionsappexemple.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application class do app.
 * 
 * Responsável por inicializar o Koin (sistema de injeção de dependências)
 * quando o app é iniciado.
 */
class PermissionsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inicializa o Koin com os módulos de dependências
        startKoin {
            androidContext(this@PermissionsApp)
            modules(appModule)
        }
    }
}


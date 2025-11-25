package com.estudos.permissionsappexemple.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.estudos.permissionsappexemple.presentation.ui.screen.HomeScreen
import com.estudos.permissionsappexemple.presentation.ui.theme.PermissionsAppTheme
import com.estudos.permissionsappexemple.presentation.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * MainActivity - Ponto de entrada do app.
 * 
 * Esta Activity:
 * 1. Configura o tema do app
 * 2. Inicializa a navegação com Navigation Compose
 * 3. Injeta o ViewModel usando Koin
 * 4. Define as rotas do app
 * 
 * ESTRUTURA DE NAVEGAÇÃO:
 * - Home: Tela principal com os três botões de funcionalidades
 * 
 * Para adicionar mais telas:
 * 1. Crie a tela Compose
 * 2. Adicione uma rota no enum Routes
 * 3. Adicione um composable no NavHost
 */
class MainActivity : ComponentActivity() {
    
    // Injeta o ViewModel usando Koin
    private val homeViewModel: HomeViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            PermissionsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Configuração de navegação
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Home.route
                    ) {
                        composable(Routes.Home.route) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Enum com as rotas do app.
 * 
 * Facilita a navegação e evita erros de digitação de strings.
 */
sealed class Routes(val route: String) {
    object Home : Routes("home")
}




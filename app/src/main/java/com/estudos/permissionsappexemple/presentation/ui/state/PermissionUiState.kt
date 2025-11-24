package com.estudos.permissionsappexemple.presentation.ui.state

import com.estudos.permissionsappexemple.domain.model.PermissionStatus

/**
 * Estado da UI relacionado a permissões.
 * 
 * Este sealed class representa todos os possíveis estados que a UI pode estar
 * em relação ao processo de solicitação de permissões.
 * 
 * O ViewModel emite esses estados, e a UI (Compose) reage a eles:
 * - Mostra dialogs de rationale
 * - Dispara launchers de permissão
 * - Mostra mensagens de erro
 * - Navega para configurações
 */
sealed class PermissionUiState {
    /**
     * Estado inicial - nenhuma ação de permissão em andamento
     */
    object Idle : PermissionUiState()
    
    /**
     * Verificando status da permissão
     */
    object Checking : PermissionUiState()
    
    /**
     * Permissão já concedida - pode prosseguir
     */
    object Granted : PermissionUiState()
    
    /**
     * Precisa solicitar permissão pela primeira vez
     */
    object RequestPermission : PermissionUiState()
    
    /**
     * Usuário negou permissão anteriormente - mostrar rationale
     */
    data class ShowRationale(
        val message: String,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit
    ) : PermissionUiState()
    
    /**
     * Permissão negada - mostrar mensagem de erro
     */
    data class Denied(val message: String) : PermissionUiState()
    
    /**
     * Permissão permanentemente negada - oferecer ir para Configurações
     */
    data class PermanentlyDenied(
        val message: String,
        val onOpenSettings: () -> Unit
    ) : PermissionUiState()
    
    /**
     * Permissão não necessária para esta versão do Android
     */
    object NotRequired : PermissionUiState()
}

/**
 * Converte PermissionStatus (domínio) para PermissionUiState (UI)
 */
fun PermissionStatus.toUiState(
    rationaleMessage: String = "",
    onOpenSettings: () -> Unit = {},
    onRequestPermission: () -> Unit = {}
): PermissionUiState {
    return when (this) {
        is PermissionStatus.Granted -> PermissionUiState.Granted
        is PermissionStatus.Denied -> PermissionUiState.RequestPermission
        is PermissionStatus.PermanentlyDenied -> PermissionUiState.PermanentlyDenied(
            message = rationaleMessage,
            onOpenSettings = onOpenSettings
        )
        is PermissionStatus.NotRequired -> PermissionUiState.NotRequired
    }
}


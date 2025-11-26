package com.estudos.permissionsappexemple.domain.model

/**
 * Representa o status de uma permissão no sistema Android.
 * 
 * Este modelo é usado para comunicar o estado da permissão entre as camadas,
 * sem depender de APIs específicas do Android (como Context ou Activity).
 * 
 * FLUXO:
 * - CheckPermissionUseCase retorna PermissionStatus
 * - ViewModel recebe PermissionStatus e decide qual ação tomar
 * - UI observa o estado e dispara os launchers apropriados
 */
sealed class PermissionStatus {
    /**
     * Permissão já foi concedida pelo usuário.
     * Ação: Prosseguir com a operação (abrir galeria, câmera, etc.)
     */
    object Granted : PermissionStatus()
    
    /**
     * Permissão ainda não foi solicitada ou foi negada pela primeira vez.
     * Ação: Solicitar permissão usando ActivityResultContracts
     */
    object Denied : PermissionStatus()
    
    /**
     * Permissão foi negada e o usuário marcou "Não perguntar novamente".
     * Ação: Mostrar rationale e oferecer botão para abrir Configurações
     */
    object PermanentlyDenied : PermissionStatus()
    
    /**
     * Permissão não é necessária para esta versão do Android.
     * Exemplo: READ_MEDIA_IMAGES não existe em Android < 13
     * Ação: Prosseguir sem solicitar permissão
     */
    object NotRequired : PermissionStatus()
}





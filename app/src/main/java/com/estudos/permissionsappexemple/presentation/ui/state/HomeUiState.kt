package com.estudos.permissionsappexemple.presentation.ui.state

import android.net.Uri

/**
 * Estado da UI da tela principal (HomeScreen).
 * 
 * Contém:
 * - Estados de permissão para cada feature
 * - Resultados das operações (URIs de imagens/arquivos)
 * - Estados de loading/erro
 */
data class HomeUiState(
    // Estados de permissão para cada feature
    val galleryPermissionState: PermissionUiState = PermissionUiState.Idle,
    val cameraPermissionState: PermissionUiState = PermissionUiState.Idle,
    val filePickerPermissionState: PermissionUiState = PermissionUiState.Idle,
    
    // Resultados das operações
    val selectedImageUri: Uri? = null,
    val selectedFileUri: Uri? = null,
    val cameraImageUri: Uri? = null,
    
    // Estados de operação
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Verifica se há alguma imagem selecionada para exibir
     */
    fun hasSelectedImage(): Boolean = selectedImageUri != null || cameraImageUri != null
    
    /**
     * Retorna a URI da imagem atual (prioriza câmera, depois galeria)
     */
    fun getCurrentImageUri(): Uri? = cameraImageUri ?: selectedImageUri
}


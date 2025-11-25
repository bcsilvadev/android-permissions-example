package com.estudos.permissionsappexemple.presentation.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estudos.permissionsappexemple.domain.model.PermissionStatus
import com.estudos.permissionsappexemple.domain.model.PermissionType
import com.estudos.permissionsappexemple.domain.usecase.CheckPermissionUseCase
import com.estudos.permissionsappexemple.domain.usecase.GetRequiredPermissionsUseCase
import com.estudos.permissionsappexemple.presentation.ui.state.HomeUiState
import com.estudos.permissionsappexemple.presentation.ui.state.PermissionUiState
import com.estudos.permissionsappexemple.presentation.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela principal (HomeScreen).
 * 
 * RESPONSABILIDADES:
 * 1. Gerenciar o estado da UI (HomeUiState)
 * 2. Orquestrar a lógica de permissões através de UseCases
 * 3. Emitir estados que a UI deve observar e reagir
 * 
 * IMPORTANTE: Este ViewModel NÃO tem referência a Context, Activity ou Fragment.
 * Toda a interação com o sistema Android (solicitar permissões, abrir câmera, etc.)
 * é feita pela UI (Compose) através de launchers de Activity Result.
 * 
 * FLUXO DE PERMISSÕES:
 * 1. Usuário clica em botão (ex: "Selecionar foto")
 * 2. ViewModel verifica permissão via CheckPermissionUseCase
 * 3. ViewModel emite PermissionUiState indicando o que fazer
 * 4. UI observa o estado e:
 *    - Se Granted: prossegue com a operação
 *    - Se RequestPermission: dispara launcher de permissão
 *    - Se ShowRationale: mostra dialog explicativo
 *    - Se PermanentlyDenied: mostra opção de ir para Configurações
 * 5. UI recebe resultado do launcher e notifica ViewModel
 * 6. ViewModel atualiza estado e emite novo PermissionUiState
 */
class HomeViewModel(
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    /**
     * Processa a intenção do usuário de selecionar uma foto da galeria.
     * 
     * Este método:
     * 1. Verifica se a permissão está concedida
     * 2. Emite o estado apropriado para a UI reagir
     * 
     * NOTA: O estado ShowRationale não é emitido aqui porque ele só aparece
     * após o usuário negar a permissão pela primeira vez. O rationale é
     * detectado no método onPermissionResult() quando shouldShowRationale é true.
     */
    fun onSelectGalleryImage() {
        viewModelScope.launch {
            _uiState.update { it.copy(galleryPermissionState = PermissionUiState.Checking) }
            
            val status = checkPermissionUseCase(PermissionType.GALLERY)
            
            _uiState.update { state ->
                state.copy(
                    galleryPermissionState = status.toUiState(
                        rationaleMessage = "Precisamos de acesso à galeria para selecionar imagens.",
                        onOpenSettings = { onOpenSettings(PermissionType.GALLERY) },
                        onRequestPermission = { /* Será tratado pela UI */ }
                    )
                )
            }
        }
    }
    
    /**
     * Processa a intenção do usuário de selecionar um arquivo.
     * 
     * Para FILE_PICKER, geralmente não é necessário solicitar permissões
     * (SAF gerencia isso automaticamente).
     */
    fun onSelectFile() {
        viewModelScope.launch {
            val status = checkPermissionUseCase(PermissionType.FILE_PICKER)
            
            _uiState.update { state ->
                state.copy(
                    filePickerPermissionState = status.toUiState()
                )
            }
        }
    }
    
    /**
     * Processa a intenção do usuário de tirar uma foto com a câmera.
     */
    fun onTakePhoto() {
        viewModelScope.launch {
            _uiState.update { it.copy(cameraPermissionState = PermissionUiState.Checking) }
            
            val status = checkPermissionUseCase(PermissionType.CAMERA)
            
            _uiState.update { state ->
                state.copy(
                    cameraPermissionState = status.toUiState(
                        rationaleMessage = "Precisamos de acesso à câmera para tirar fotos.",
                        onOpenSettings = { onOpenSettings(PermissionType.CAMERA) },
                        onRequestPermission = { /* Será tratado pela UI */ }
                    )
                )
            }
        }
    }
    
    /**
     * Notifica o ViewModel sobre o resultado de uma solicitação de permissão.
     * 
     * Este método é chamado pela UI após o usuário responder à solicitação de permissão.
     * 
     * @param permissionType Tipo da permissão que foi solicitada
     * @param granted true se a permissão foi concedida, false caso contrário
     * @param shouldShowRationale true se o sistema recomenda mostrar rationale
     */
    fun onPermissionResult(
        permissionType: PermissionType,
        granted: Boolean,
        shouldShowRationale: Boolean
    ) {
        viewModelScope.launch {
            val rationaleMessage = when (permissionType) {
                PermissionType.GALLERY -> "Precisamos de acesso à galeria para selecionar imagens. Por favor, permita o acesso nas configurações de permissão."
                PermissionType.CAMERA -> "Precisamos de acesso à câmera para tirar fotos. Por favor, permita o acesso nas configurações de permissão."
                PermissionType.FILE_PICKER -> ""
            }
            
            val uiState = if (granted) {
                // Permissão concedida
                PermissionUiState.Granted
            } else if (shouldShowRationale) {
                // Usuário negou, mas ainda pode ser convencido - mostrar rationale
                PermissionUiState.ShowRationale(
                    message = rationaleMessage,
                    onConfirm = {
                        // Quando usuário confirma o rationale, solicita permissão novamente
                        requestPermissionAgain(permissionType)
                    },
                    onDismiss = {
                        // Quando usuário cancela, reseta o estado
                        resetPermissionState(permissionType)
                    }
                )
            } else {
                // Usuário negou permanentemente (marcou "Não perguntar novamente")
                PermissionUiState.PermanentlyDenied(
                    message = rationaleMessage,
                    onOpenSettings = { onOpenSettings(permissionType) }
                )
            }
            
            _uiState.update { state ->
                when (permissionType) {
                    PermissionType.GALLERY -> state.copy(galleryPermissionState = uiState)
                    PermissionType.CAMERA -> state.copy(cameraPermissionState = uiState)
                    PermissionType.FILE_PICKER -> state.copy(filePickerPermissionState = uiState)
                }
            }
        }
    }
    
    /**
     * Solicita permissão novamente após o usuário ver o rationale e confirmar.
     * 
     * Este método é chamado quando o usuário confirma o dialog de rationale,
     * indicando que entendeu a necessidade da permissão e quer tentar novamente.
     * 
     * @param permissionType Tipo de permissão a solicitar novamente
     */
    private fun requestPermissionAgain(permissionType: PermissionType) {
        viewModelScope.launch {
            // Emite estado RequestPermission para que a UI solicite a permissão novamente
            _uiState.update { state ->
                val requestState = PermissionUiState.RequestPermission
                when (permissionType) {
                    PermissionType.GALLERY -> state.copy(galleryPermissionState = requestState)
                    PermissionType.CAMERA -> state.copy(cameraPermissionState = requestState)
                    PermissionType.FILE_PICKER -> state.copy(filePickerPermissionState = requestState)
                }
            }
        }
    }
    
    /**
     * Notifica o ViewModel sobre o resultado de uma operação (seleção de imagem, arquivo, etc.).
     * 
     * @param uri URI do arquivo/imagem selecionado
     * @param source Origem da seleção (gallery, camera, file)
     */
    fun onOperationResult(uri: Uri?, source: OperationSource) {
        _uiState.update { state ->
            when (source) {
                OperationSource.GALLERY -> state.copy(
                    selectedImageUri = uri,
                    galleryPermissionState = PermissionUiState.Granted
                )
                OperationSource.CAMERA -> state.copy(
                    cameraImageUri = uri,
                    cameraPermissionState = PermissionUiState.Granted
                )
                OperationSource.FILE_PICKER -> state.copy(
                    selectedFileUri = uri,
                    filePickerPermissionState = PermissionUiState.Granted
                )
            }
        }
    }
    
    /**
     * Notifica o ViewModel que o usuário quer abrir as Configurações do app.
     * 
     * Emite um estado que a UI observa para abrir as configurações.
     * 
     * @param permissionType Tipo de permissão que precisa ser habilitada
     */
    fun onOpenSettings(permissionType: PermissionType) {
        _uiState.update { it.copy(shouldOpenSettings = permissionType) }
    }
    
    /**
     * Notifica o ViewModel que as configurações foram abertas.
     * Limpa o estado e prepara para verificar novamente quando o usuário voltar.
     */
    fun onSettingsOpened() {
        _uiState.update { it.copy(shouldOpenSettings = null) }
    }
    
    /**
     * Verifica novamente o status de uma permissão após o usuário voltar das configurações.
     * 
     * Este método deve ser chamado quando o usuário retorna das configurações do app,
     * para verificar se a permissão foi habilitada manualmente.
     * 
     * @param permissionType Tipo de permissão a verificar
     */
    fun recheckPermission(permissionType: PermissionType) {
        viewModelScope.launch {
            val status = checkPermissionUseCase(permissionType)
            
            val rationaleMessage = when (permissionType) {
                PermissionType.GALLERY -> "Precisamos de acesso à galeria para selecionar imagens."
                PermissionType.CAMERA -> "Precisamos de acesso à câmera para tirar fotos."
                PermissionType.FILE_PICKER -> ""
            }
            
            val uiState = status.toUiState(
                rationaleMessage = rationaleMessage,
                onOpenSettings = { onOpenSettings(permissionType) }
            )
            
            _uiState.update { state ->
                when (permissionType) {
                    PermissionType.GALLERY -> state.copy(galleryPermissionState = uiState)
                    PermissionType.CAMERA -> state.copy(cameraPermissionState = uiState)
                    PermissionType.FILE_PICKER -> state.copy(filePickerPermissionState = uiState)
                }
            }
        }
    }
    
    /**
     * Limpa o estado de erro.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Reseta o estado de permissão para um tipo específico.
     * Útil para tentar novamente após erro.
     */
    fun resetPermissionState(permissionType: PermissionType) {
        _uiState.update { state ->
            val idleState = PermissionUiState.Idle
            when (permissionType) {
                PermissionType.GALLERY -> state.copy(galleryPermissionState = idleState)
                PermissionType.CAMERA -> state.copy(cameraPermissionState = idleState)
                PermissionType.FILE_PICKER -> state.copy(filePickerPermissionState = idleState)
            }
        }
    }
    
    /**
     * Retorna as permissões necessárias para um tipo específico.
     * Usado pela UI para saber quais permissões solicitar.
     */
    suspend fun getRequiredPermissions(permissionType: PermissionType): List<String> {
        return getRequiredPermissionsUseCase(permissionType)
    }
}

/**
 * Enum para identificar a origem de uma operação.
 */
enum class OperationSource {
    GALLERY,
    CAMERA,
    FILE_PICKER
}



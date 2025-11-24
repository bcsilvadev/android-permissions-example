package com.estudos.permissionsappexemple.presentation.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.estudos.permissionsappexemple.data.manager.CameraManager
import com.estudos.permissionsappexemple.domain.model.PermissionType
import com.estudos.permissionsappexemple.presentation.ui.state.PermissionUiState
import com.estudos.permissionsappexemple.presentation.viewmodel.HomeViewModel
import com.estudos.permissionsappexemple.presentation.viewmodel.OperationSource
import kotlinx.coroutines.launch

/**
 * Tela principal do app com três funcionalidades:
 * 1. Selecionar foto da galeria
 * 2. Selecionar arquivo do dispositivo
 * 3. Tirar foto com a câmera
 * 
 * Esta tela demonstra o fluxo completo de permissões:
 * - Verificação inicial
 * - Solicitação em runtime
 * - Tratamento de resultados
 * - Rationale e navegação para configurações
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // CameraManager para criar URIs de fotos
    val cameraManager = remember {
        CameraManager(
            context = context,
            authority = "${context.packageName}.fileprovider"
        )
    }
    
    // ========== LAUNCHERS DE PERMISSÃO ==========
    
    /**
     * Launcher para solicitar permissão de galeria.
     * 
     * Este launcher é disparado quando o ViewModel emite PermissionUiState.RequestPermission
     * para o tipo GALLERY.
     */
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Verifica se deve mostrar rationale
        val activity = context as? Activity
        val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        ) ?: false
        
        // Notifica o ViewModel sobre o resultado
        viewModel.onPermissionResult(
            permissionType = PermissionType.GALLERY,
            granted = isGranted,
            shouldShowRationale = shouldShowRationale
        )
    }
    
    /**
     * Launcher para solicitar permissão de câmera.
     */
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val activity = context as? Activity
        val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(
            Manifest.permission.CAMERA
        ) ?: false
        
        viewModel.onPermissionResult(
            permissionType = PermissionType.CAMERA,
            granted = isGranted,
            shouldShowRationale = shouldShowRationale
        )
    }
    
    // ========== LAUNCHERS DE ACTIVITY RESULT ==========
    
    /**
     * Launcher para selecionar imagem da galeria.
     * 
     * Usa PickVisualMedia (Photo Picker) que é a forma moderna recomendada.
     * Em Android 13+, pode não requerer permissões explícitas em alguns casos.
     */
    val galleryImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.onOperationResult(uri, OperationSource.GALLERY)
    }
    
    /**
     * Launcher para selecionar arquivo usando Storage Access Framework (SAF).
     * 
     * SAF não requer permissões explícitas - o sistema gerencia isso automaticamente.
     */
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        viewModel.onOperationResult(uri, OperationSource.FILE_PICKER)
    }
    
    /**
     * Launcher para tirar foto com a câmera.
     * 
     * Requer uma URI pré-criada onde a foto será salva.
     */
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            viewModel.onOperationResult(cameraImageUri, OperationSource.CAMERA)
        }
    }
    
    // ========== REAÇÃO AOS ESTADOS DE PERMISSÃO ==========
    
    /**
     * Observa o estado de permissão de galeria e reage apropriadamente.
     */
    LaunchedEffect(uiState.galleryPermissionState) {
        when (val state = uiState.galleryPermissionState) {
            is PermissionUiState.RequestPermission -> {
                // ViewModel sinalizou que precisa solicitar permissão
                scope.launch {
                    val permissions = viewModel.getRequiredPermissions(PermissionType.GALLERY)
                    if (permissions.isNotEmpty()) {
                        galleryPermissionLauncher.launch(permissions.first())
                    } else {
                        // Sem permissões necessárias, prossegue direto
                        galleryImagePicker.launch(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    }
                }
            }
            is PermissionUiState.Granted -> {
                // Permissão concedida, abre o seletor de imagens
                galleryImagePicker.launch(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            }
            is PermissionUiState.NotRequired -> {
                // Permissão não necessária (ex: Photo Picker em Android 13+)
                galleryImagePicker.launch(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            }
            else -> { /* Outros estados são tratados na UI */ }
        }
    }
    
    /**
     * Observa o estado de permissão de câmera e reage apropriadamente.
     */
    LaunchedEffect(uiState.cameraPermissionState) {
        when (val state = uiState.cameraPermissionState) {
            is PermissionUiState.RequestPermission -> {
                scope.launch {
                    val permissions = viewModel.getRequiredPermissions(PermissionType.CAMERA)
                    if (permissions.isNotEmpty()) {
                        cameraPermissionLauncher.launch(permissions.first())
                    }
                }
            }
            is PermissionUiState.Granted -> {
                // Permissão concedida, cria URI e abre câmera
                val uri = cameraManager.createImageUri()
                if (uri != null) {
                    cameraImageUri = uri
                    takePictureLauncher.launch(uri)
                }
            }
            else -> { /* Outros estados são tratados na UI */ }
        }
    }
    
    /**
     * Observa o estado de permissão de file picker.
     */
    LaunchedEffect(uiState.filePickerPermissionState) {
        when (val state = uiState.filePickerPermissionState) {
            is PermissionUiState.Granted,
            is PermissionUiState.NotRequired -> {
                // Abre o seletor de arquivos (SAF não requer permissões)
                filePicker.launch(arrayOf("*/*"))
            }
            else -> { /* Outros estados são tratados na UI */ }
        }
    }
    
    // ========== UI ==========
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App de Permissões") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título e descrição
            Text(
                text = "Demonstração de Permissões",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Este app demonstra o fluxo completo de solicitação e tratamento de permissões em runtime.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider()
            
            // Botões de ação
            ActionButton(
                text = "Selecionar foto da galeria",
                onClick = { viewModel.onSelectGalleryImage() },
                enabled = uiState.galleryPermissionState !is PermissionUiState.Checking
            )
            
            ActionButton(
                text = "Selecionar arquivo do dispositivo",
                onClick = { viewModel.onSelectFile() },
                enabled = uiState.filePickerPermissionState !is PermissionUiState.Checking
            )
            
            ActionButton(
                text = "Tirar foto com a câmera",
                onClick = { viewModel.onTakePhoto() },
                enabled = uiState.cameraPermissionState !is PermissionUiState.Checking
            )
            
            Divider()
            
            // Exibição de resultados
            if (uiState.hasSelectedImage()) {
                val imageUri = uiState.getCurrentImageUri()
                if (imageUri != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Imagem selecionada:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Imagem selecionada",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
            
            if (uiState.selectedFileUri != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Arquivo selecionado:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.selectedFileUri.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Dialogs de rationale e erro
            uiState.galleryPermissionState.ShowRationaleDialog(
                onDismiss = { viewModel.resetPermissionState(PermissionType.GALLERY) }
            )
            
            uiState.cameraPermissionState.ShowRationaleDialog(
                onDismiss = { viewModel.resetPermissionState(PermissionType.CAMERA) }
            )
            
            uiState.galleryPermissionState.ShowPermanentlyDeniedDialog(
                onOpenSettings = {
                    openAppSettings(context)
                    viewModel.resetPermissionState(PermissionType.GALLERY)
                },
                onDismiss = { viewModel.resetPermissionState(PermissionType.GALLERY) }
            )
            
            uiState.cameraPermissionState.ShowPermanentlyDeniedDialog(
                onOpenSettings = {
                    openAppSettings(context)
                    viewModel.resetPermissionState(PermissionType.CAMERA)
                },
                onDismiss = { viewModel.resetPermissionState(PermissionType.CAMERA) }
            )
            
            // Mensagens de erro
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Text("✕")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Botão de ação reutilizável.
 */
@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(text = text)
    }
}

/**
 * Extension function para mostrar dialog de rationale.
 */
@Composable
private fun PermissionUiState.ShowRationaleDialog(
    onDismiss: () -> Unit
) {
    if (this is PermissionUiState.ShowRationale) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Permissão necessária") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Entendi")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Extension function para mostrar dialog de permissão permanentemente negada.
 */
@Composable
private fun PermissionUiState.ShowPermanentlyDeniedDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    if (this is PermissionUiState.PermanentlyDenied) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Permissão bloqueada") },
            text = {
                Column {
                    Text(message)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Por favor, habilite a permissão nas configurações do app.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onOpenSettings) {
                    Text("Abrir Configurações")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Abre as configurações do app.
 */
private fun openAppSettings(context: android.content.Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}


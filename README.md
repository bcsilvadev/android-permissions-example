# 📱 App de Exemplo - Permissões Android

Este projeto é um **exemplo completo e didático** de como implementar o gerenciamento de permissões em runtime no Android, seguindo as melhores práticas e recomendações atuais (Android 13+). O projeto demonstra uma arquitetura MVVM limpa com separação de responsabilidades e tratamento adequado de todos os estados de permissão.

---

## 🎯 Objetivo do Projeto

Este app foi criado para servir como **referência educacional** sobre:

- ✅ Solicitar permissões em runtime de forma correta e moderna
- ✅ Tratar todos os estados de permissão (concedida, negada, permanentemente negada)
- ✅ Usar APIs modernas (Photo Picker, Storage Access Framework)
- ✅ Implementar arquitetura MVVM com camadas bem definidas
- ✅ Separar responsabilidades entre ViewModel e UI
- ✅ Usar injeção de dependências (Koin) de forma adequada

---

## 🏗️ Arquitetura do Projeto

O projeto segue a arquitetura **MVVM (Model-View-ViewModel)** com três camadas principais:

```
┌─────────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                         │
│  ┌──────────────────────────────────────────────────┐   │
│  │  UI (Compose)                                    │   │
│  │  - HomeScreen.kt                                 │   │
│  │  - Observa estados do ViewModel                 │   │
│  │  - Dispara launchers de permissão               │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  ViewModels                                       │   │
│  │  - HomeViewModel.kt                              │   │
│  │  - Orquestra lógica de negócio                   │   │
│  │  - Emite estados para UI                         │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  UI States                                        │   │
│  │  - PermissionUiState.kt                          │   │
│  │  - HomeUiState.kt                                │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                        ↓ (chama UseCases)
┌─────────────────────────────────────────────────────────┐
│              DOMAIN LAYER                                │
│  ┌──────────────────────────────────────────────────┐   │
│  │  UseCases                                        │   │
│  │  - CheckPermissionUseCase.kt                    │   │
│  │  - GetRequiredPermissionsUseCase.kt              │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Models (Entidades de Domínio)                   │   │
│  │  - PermissionStatus.kt                           │   │
│  │  - PermissionType.kt                               │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                        ↓ (consulta Repository)
┌─────────────────────────────────────────────────────────┐
│              DATA LAYER                                  │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Repository                                       │   │
│  │  - PermissionRepository.kt (interface)           │   │
│  │  - PermissionRepositoryImpl.kt (implementação)   │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Managers                                         │   │
│  │  - CameraManager.kt                              │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                        ↓ (acessa sistema Android)
                    Sistema Android
```

### Princípios da Arquitetura

1. **Separação de Responsabilidades**: Cada camada tem uma responsabilidade clara
2. **Inversão de Dependências**: Camadas superiores dependem de abstrações (interfaces)
3. **ViewModel sem Context**: ViewModel não conhece detalhes de Android (Context, Activity)
4. **UI Reativa**: UI observa estados e reage apropriadamente
5. **Testabilidade**: Estrutura facilita testes unitários

---

## 📦 Tecnologias e Dependências

### Core
- **Kotlin 2.0.21**: Linguagem de programação moderna
- **Android Gradle Plugin 8.12.3**: Build system
- **KSP 2.0.21-1.0.28**: Kotlin Symbol Processing (para Koin)

### UI
- **Jetpack Compose BOM 2024.12.01**: Framework de UI declarativa
- **Material 3**: Design system moderno
- **Navigation Compose 2.8.4**: Navegação entre telas
- **Compose Compiler 2.2.21**: Compilador do Compose

### Arquitetura
- **Lifecycle 2.8.7**: Gerenciamento de ciclo de vida
- **Koin 3.5.6**: Injeção de dependências
- **Koin Annotations 1.3.1**: Anotações para DI automática

### Utilitários
- **Activity Compose 1.9.3**: Integração Activity + Compose
- **Coil 2.7.0**: Carregamento de imagens assíncrono

---

## 📁 Estrutura Detalhada do Projeto

```
app/src/main/java/com/estudos/permissionsappexemple/
│
├── 📂 data/                          # Camada de Dados
│   ├── 📂 manager/
│   │   └── CameraManager.kt          # Gerencia operações de câmera e FileProvider
│   └── 📂 repository/
│       └── PermissionRepository.kt   # Interface e implementação do repositório
│
├── 📂 domain/                         # Camada de Domínio (Lógica de Negócio)
│   ├── 📂 model/
│   │   ├── PermissionStatus.kt       # Estados possíveis de uma permissão
│   │   └── PermissionType.kt         # Tipos de permissão (GALLERY, CAMERA, etc.)
│   └── 📂 usecase/
│       ├── CheckPermissionUseCase.kt  # Verifica status de permissão
│       └── GetRequiredPermissionsUseCase.kt  # Obtém permissões necessárias
│
├── 📂 presentation/                  # Camada de Apresentação
│   ├── MainActivity.kt               # Activity principal do app
│   └── 📂 ui/
│       ├── 📂 screen/
│       │   └── HomeScreen.kt         # Tela principal (Compose)
│       ├── 📂 state/
│       │   ├── PermissionUiState.kt  # Estados de UI para permissões
│       │   └── HomeUiState.kt        # Estado completo da tela principal
│       ├── 📂 theme/
│       │   └── Theme.kt              # Configuração de tema Material3
│       └── 📂 viewmodel/
│           └── HomeViewModel.kt       # ViewModel da tela principal
│
├── 📂 di/                            # Injeção de Dependências
│   └── AppModule.kt                  # Módulo Koin com todas as dependências
│
└── PermissionsApp.kt                 # Application class (inicializa Koin)
```

---

## 🔍 Detalhamento de Cada Componente

### 1. 📂 Domain Layer (Camada de Domínio)

#### `domain/model/PermissionStatus.kt`

**O que é**: Sealed class que representa todos os estados possíveis de uma permissão.

**Estados possíveis**:
- `Granted`: Permissão já foi concedida pelo usuário
- `Denied`: Permissão ainda não foi solicitada ou foi negada pela primeira vez
- `PermanentlyDenied`: Usuário marcou "Não perguntar novamente"
- `NotRequired`: Permissão não é necessária para esta versão do Android

**Por que existe**: Encapsula o conceito de status de permissão sem depender de APIs Android específicas. Permite que a lógica de negócio trabalhe com conceitos de domínio, não com detalhes técnicos.

**Como usar**:
```kotlin
when (permissionStatus) {
    is PermissionStatus.Granted -> { /* Prosseguir */ }
    is PermissionStatus.Denied -> { /* Solicitar */ }
    // ...
}
```

---

#### `domain/model/PermissionType.kt`

**O que é**: Enum que define os tipos de permissões que o app precisa gerenciar.

**Tipos**:
- `GALLERY`: Acesso a imagens da galeria
- `CAMERA`: Acesso à câmera
- `FILE_PICKER`: Seleção de arquivos (geralmente não requer permissão)

**Por que existe**: Abstrai diferentes permissões Android (que variam por versão) em conceitos de domínio. Por exemplo, `GALLERY` pode ser `READ_MEDIA_IMAGES` (Android 13+) ou `READ_EXTERNAL_STORAGE` (Android 12-), mas o domínio só conhece "GALLERY".

**Como usar**:
```kotlin
checkPermissionUseCase(PermissionType.GALLERY)
```

---

#### `domain/usecase/CheckPermissionUseCase.kt`

**O que é**: UseCase que encapsula a lógica de verificação de permissão.

**Responsabilidades**:
- Recebe um `PermissionType`
- Consulta o `PermissionRepository`
- Retorna um `PermissionStatus`

**Por que existe**: Segue o padrão Clean Architecture. Encapsula uma regra de negócio específica (verificar permissão) de forma reutilizável e testável.

**Fluxo**:
```
ViewModel → CheckPermissionUseCase → PermissionRepository → Sistema Android
```

**Como usar**:
```kotlin
val status = checkPermissionUseCase(PermissionType.CAMERA)
```

---

#### `domain/usecase/GetRequiredPermissionsUseCase.kt`

**O que é**: UseCase que retorna as strings de permissão Android necessárias para um tipo.

**Responsabilidades**:
- Recebe um `PermissionType`
- Retorna lista de strings de permissão (ex: `["android.permission.CAMERA"]`)

**Por que existe**: A UI precisa das strings de permissão para passar aos launchers. Este UseCase centraliza a lógica de mapeamento.

**Como usar**:
```kotlin
val permissions = getRequiredPermissionsUseCase(PermissionType.GALLERY)
// Retorna: ["android.permission.READ_MEDIA_IMAGES"] (Android 13+)
```

---

### 2. 📂 Data Layer (Camada de Dados)

#### `data/repository/PermissionRepository.kt`

**O que é**: Interface e implementação do repositório de permissões.

**Interface (`PermissionRepository`)**:
- Define o contrato sem expor detalhes de implementação
- Permite que camadas superiores dependam de abstração

**Implementação (`PermissionRepositoryImpl`)**:
- Usa `ContextCompat.checkSelfPermission()` para verificar permissões
- Adapta permissões conforme a versão do Android:
  - Android 13+: `READ_MEDIA_IMAGES` para galeria
  - Android 12-: `READ_EXTERNAL_STORAGE` para galeria
- Retorna `PermissionStatus` baseado no resultado

**Métodos principais**:
1. `checkPermissionStatus()`: Verifica se permissão está concedida
2. `getRequiredPermissions()`: Retorna strings de permissão necessárias
3. `isPermissionPermanentlyDenied()`: Verifica se foi negada permanentemente

**Por que existe**: Encapsula toda a lógica de acesso ao sistema Android. As camadas superiores não precisam saber sobre `Context`, `PackageManager`, ou versões do Android.

**Exemplo de uso interno**:
```kotlin
// No repositório
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    listOf(Manifest.permission.READ_MEDIA_IMAGES)
} else {
    listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}
```

---

#### `data/manager/CameraManager.kt`

**O que é**: Classe que gerencia operações relacionadas à câmera.

**Responsabilidades**:
- Criar URIs seguras usando `FileProvider` para salvar fotos
- Gerenciar arquivos temporários de fotos
- Limpar fotos antigas (opcional)

**Método principal**: `createImageUri()`
- Cria um arquivo temporário com nome único (timestamp)
- Usa `FileProvider.getUriForFile()` para criar URI segura
- Retorna `Uri` que pode ser compartilhada com outros apps (como a câmera)

**Por que existe**: Centraliza a lógica de criação de URIs para fotos. O `FileProvider` é necessário porque apps não podem compartilhar `file://` URIs diretamente por questões de segurança.

**Como usar**:
```kotlin
val cameraManager = CameraManager(context, authority)
val imageUri = cameraManager.createImageUri()
takePictureLauncher.launch(imageUri)
```

---

### 3. 📂 Presentation Layer (Camada de Apresentação)

#### `presentation/ui/viewmodel/HomeViewModel.kt`

**O que é**: ViewModel que gerencia o estado e a lógica da tela principal.

**Responsabilidades**:
1. Gerenciar `HomeUiState` (estado completo da UI)
2. Orquestrar verificação de permissões através de UseCases
3. Emitir `PermissionUiState` para cada funcionalidade
4. Processar resultados de permissões e operações

**Características importantes**:
- ❌ **NÃO tem referência a `Context` ou `Activity`**
- ✅ Trabalha apenas com modelos de domínio
- ✅ Emite estados que a UI observa
- ✅ Recebe notificações da UI sobre resultados

**Métodos principais**:
- `onSelectGalleryImage()`: Inicia fluxo de seleção de foto
- `onSelectFile()`: Inicia fluxo de seleção de arquivo
- `onTakePhoto()`: Inicia fluxo de tirar foto
- `onPermissionResult()`: Processa resultado de solicitação de permissão
- `onOperationResult()`: Processa resultado de operação (URI selecionado)
- `onOpenSettings(permissionType)`: Emite estado para abrir configurações do app
- `onSettingsOpened()`: Limpa estado após abrir configurações
- `recheckPermission(permissionType)`: Verifica novamente status da permissão após usuário voltar das configurações

**Fluxo típico**:
```
1. Usuário clica → ViewModel.onSelectGalleryImage()
2. ViewModel verifica permissão → checkPermissionUseCase()
3. ViewModel emite estado → PermissionUiState.RequestPermission
4. UI observa estado → Dispara launcher
5. Usuário responde → UI chama ViewModel.onPermissionResult()
6. ViewModel atualiza estado → UI reage novamente
```

**Como usar na UI**:
```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
// Observar uiState.galleryPermissionState, etc.
```

---

#### `presentation/ui/state/PermissionUiState.kt`

**O que é**: Sealed class que representa todos os estados de UI relacionados a permissões.

**Estados possíveis**:
- `Idle`: Estado inicial, nenhuma ação
- `Checking`: Verificando status da permissão
- `Granted`: Permissão concedida, pode prosseguir
- `RequestPermission`: Precisa solicitar permissão
- `ShowRationale`: Mostrar explicação ao usuário
- `Denied`: Permissão negada, mostrar erro
- `PermanentlyDenied`: Bloqueada, oferecer ir para Configurações
- `NotRequired`: Permissão não necessária

**Por que existe**: Separa estados de domínio (`PermissionStatus`) de estados de UI (`PermissionUiState`). A UI pode ter estados adicionais como `ShowRationale` que não existem no domínio.

**Função de extensão**: `toUiState()`
- Converte `PermissionStatus` (domínio) para `PermissionUiState` (UI)
- Adiciona callbacks necessários (onOpenSettings, etc.)

**Como usar**:
```kotlin
when (val state = uiState.galleryPermissionState) {
    is PermissionUiState.RequestPermission -> { /* Solicitar */ }
    is PermissionUiState.ShowRationale -> { /* Mostrar dialog */ }
    // ...
}
```

---

#### `presentation/ui/state/HomeUiState.kt`

**O que é**: Data class que representa o estado completo da tela principal.

**Propriedades**:
- Estados de permissão para cada feature (gallery, camera, filePicker)
- URIs de resultados (selectedImageUri, selectedFileUri, cameraImageUri)
- Estados de operação (isLoading, errorMessage)
- `shouldOpenSettings`: Controla quando abrir as configurações do app (tipo de permissão que precisa ser habilitada)

**Funções auxiliares**:
- `hasSelectedImage()`: Verifica se há imagem para exibir
- `getCurrentImageUri()`: Retorna URI da imagem atual (prioriza câmera)

**Por que existe**: Centraliza todo o estado da tela em um único lugar. Facilita observação e atualização.

**Como usar**:
```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
if (uiState.hasSelectedImage()) {
    // Exibir imagem
}
```

---

#### `presentation/ui/screen/HomeScreen.kt`

**O que é**: Tela principal do app construída com Jetpack Compose.

**Responsabilidades**:
1. Renderizar a UI (botões, imagens, dialogs)
2. Configurar launchers de permissão e Activity Result
3. Observar estados do ViewModel
4. Reagir a mudanças de estado e disparar ações apropriadas

**Launchers configurados**:

1. **Launchers de Permissão**:
   ```kotlin
   val galleryPermissionLauncher = rememberLauncherForActivityResult(
       contract = ActivityResultContracts.RequestPermission()
   ) { isGranted ->
       // Processa resultado
   }
   ```

2. **Launchers de Activity Result**:
   - `galleryImagePicker`: Photo Picker para selecionar imagem
   - `filePicker`: SAF para selecionar arquivo
   - `takePictureLauncher`: Câmera para tirar foto

**Reação a Estados** (usando `LaunchedEffect`):
```kotlin
LaunchedEffect(uiState.galleryPermissionState) {
    when (val state = uiState.galleryPermissionState) {
        is PermissionUiState.RequestPermission -> {
            // Dispara launcher
            galleryPermissionLauncher.launch(permission)
        }
        is PermissionUiState.Granted -> {
            // Abre seletor
            galleryImagePicker.launch(...)
        }
        // ...
    }
}
```

**Componentes da UI**:
- Três botões principais (Galeria, Arquivo, Câmera)
- Cards para exibir resultados (imagens, URIs)
- Dialogs para rationale e permissão bloqueada
- Mensagens de erro

**Funcionalidade de Abertura de Configurações**:
- Observa o estado `shouldOpenSettings` do ViewModel
- Abre automaticamente as configurações do app quando necessário
- Verifica automaticamente o status da permissão quando o usuário volta das configurações
- Atualiza o estado da UI se a permissão foi habilitada manualmente

**Por que existe**: Esta é a única camada que interage diretamente com o sistema Android (launchers, Activity, Context). O ViewModel permanece puro e testável.

---

#### `presentation/MainActivity.kt`

**O que é**: Activity principal do app.

**Responsabilidades**:
1. Configurar o tema do app
2. Inicializar Navigation Compose
3. Injetar ViewModel usando Koin
4. Definir rotas do app

**Estrutura**:
```kotlin
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setContent {
            PermissionsAppTheme {
                NavHost(...) {
                    composable(Routes.Home.route) {
                        HomeScreen(viewModel = homeViewModel)
                    }
                }
            }
        }
    }
}
```

**Rotas**: Enum `Routes` define as rotas do app (atualmente apenas `Home`).

---

#### `presentation/ui/theme/Theme.kt`

**O que é**: Configuração do tema Material 3 do app.

**Características**:
- Suporta modo claro e escuro
- Dynamic colors (Android 12+)
- Configura status bar corretamente

---

### 4. 📂 DI (Injeção de Dependências)

#### `di/AppModule.kt`

**O que é**: Módulo Koin que configura todas as dependências do app.

**Estrutura**:
```kotlin
val appModule = module {
    // Data Layer
    singleOf(::PermissionRepositoryImpl) { bind<PermissionRepository>() }
    factory { (authority: String) -> CameraManager(...) }
    
    // Domain Layer
    factoryOf(::CheckPermissionUseCase)
    factoryOf(::GetRequiredPermissionsUseCase)
    
    // Presentation Layer
    viewModel { HomeViewModel(get(), get()) }
}
```

**Tipos de escopo**:
- `single`: Instância única (singleton) - usado para Repository
- `factory`: Nova instância a cada injeção - usado para UseCases
- `viewModel`: Escopo de ViewModel - usado para ViewModels

**Por que existe**: Centraliza configuração de dependências. Facilita testes (pode criar módulos de teste) e manutenção.

---

#### `PermissionsApp.kt`

**O que é**: Application class do app.

**Responsabilidade**: Inicializar o Koin quando o app é iniciado.

**Código**:
```kotlin
class PermissionsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PermissionsApp)
            modules(appModule)
        }
    }
}
```

---

## 🔄 Fluxo Completo de Permissão (Passo a Passo)

### Exemplo: Selecionar Foto da Galeria

#### 1. Usuário Interage com a UI
```
Usuário clica no botão "Selecionar foto da galeria"
↓
HomeScreen chama: viewModel.onSelectGalleryImage()
```

#### 2. ViewModel Processa a Intenção
```kotlin
// HomeViewModel.kt
fun onSelectGalleryImage() {
    viewModelScope.launch {
        // Atualiza estado para "verificando"
        _uiState.update { 
            it.copy(galleryPermissionState = PermissionUiState.Checking) 
        }
        
        // Verifica permissão através do UseCase
        val status = checkPermissionUseCase(PermissionType.GALLERY)
        
        // Converte status de domínio para estado de UI
        _uiState.update { state ->
            state.copy(
                galleryPermissionState = status.toUiState(...)
            )
        }
    }
}
```

#### 3. UseCase Consulta o Repositório
```kotlin
// CheckPermissionUseCase.kt
suspend operator fun invoke(permissionType: PermissionType): PermissionStatus {
    return permissionRepository.checkPermissionStatus(permissionType)
}
```

#### 4. Repositório Verifica no Sistema Android
```kotlin
// PermissionRepositoryImpl.kt
override fun checkPermissionStatus(permissionType: PermissionType): PermissionStatus {
    val permissions = getRequiredPermissions(permissionType)
    
    // Verifica se todas as permissões foram concedidas
    val allGranted = permissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == 
            PackageManager.PERMISSION_GRANTED
    }
    
    return if (allGranted) {
        PermissionStatus.Granted
    } else {
        PermissionStatus.Denied
    }
}
```

#### 5. UI Observa Mudança de Estado
```kotlin
// HomeScreen.kt
LaunchedEffect(uiState.galleryPermissionState) {
    when (val state = uiState.galleryPermissionState) {
        is PermissionUiState.RequestPermission -> {
            // ViewModel sinalizou que precisa solicitar permissão
            scope.launch {
                val permissions = viewModel.getRequiredPermissions(PermissionType.GALLERY)
                galleryPermissionLauncher.launch(permissions.first())
            }
        }
        is PermissionUiState.Granted -> {
            // Permissão já concedida, abre seletor direto
            galleryImagePicker.launch(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        }
        // ...
    }
}
```

#### 6. Sistema Android Solicita Permissão
```
Dialog do sistema aparece: "Permitir que PermissionsAppExemple acesse fotos?"
Usuário escolhe: Permitir / Negar
```

#### 7. UI Recebe Resultado e Notifica ViewModel
```kotlin
// HomeScreen.kt
val galleryPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    val activity = context as? Activity
    val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(...) ?: false
    
    // Notifica ViewModel sobre o resultado
    viewModel.onPermissionResult(
        permissionType = PermissionType.GALLERY,
        granted = isGranted,
        shouldShowRationale = shouldShowRationale
    )
}
```

#### 8. ViewModel Processa Resultado
```kotlin
// HomeViewModel.kt
fun onPermissionResult(
    permissionType: PermissionType,
    granted: Boolean,
    shouldShowRationale: Boolean
) {
    val newStatus = if (granted) {
        PermissionStatus.Granted
    } else if (shouldShowRationale) {
        PermissionStatus.Denied  // Ainda pode ser convencido
    } else {
        PermissionStatus.PermanentlyDenied  // Bloqueada
    }
    
    // Atualiza estado
    _uiState.update { ... }
}
```

#### 9. UI Reage ao Novo Estado
- Se `Granted`: Abre seletor de imagens
- Se `Denied`: Pode mostrar rationale e tentar novamente
- Se `PermanentlyDenied`: Mostra dialog oferecendo ir para Configurações

#### 9.1. Caso: Permissão Permanentemente Negada (Abertura de Configurações)
```
Se PermanentlyDenied:
  ↓
Dialog aparece com botão "Abrir Configurações"
  ↓
Usuário clica → viewModel.onOpenSettings(PermissionType.GALLERY)
  ↓
ViewModel emite: shouldOpenSettings = GALLERY
  ↓
LaunchedEffect detecta e abre configurações automaticamente
  ↓
Usuário habilita permissão nas configurações
  ↓
Usuário volta para o app
  ↓
LaunchedEffect detecta mudança e chama viewModel.recheckPermission()
  ↓
ViewModel verifica status novamente
  ↓
Se concedida: Estado atualizado para Granted, funcionalidade disponível
```

#### 10. Usuário Seleciona Imagem
```
Photo Picker abre
Usuário seleciona imagem
↓
galleryImagePicker retorna Uri
↓
UI chama: viewModel.onOperationResult(uri, OperationSource.GALLERY)
↓
ViewModel atualiza: selectedImageUri = uri
↓
UI exibe imagem na tela
```

---

## 🚀 Funcionalidades do App

### 1. 📸 Selecionar Foto da Galeria

**Como funciona**:
1. Usa **Photo Picker** (Android 13+) quando disponível
2. Solicita `READ_MEDIA_IMAGES` (Android 13+) ou `READ_EXTERNAL_STORAGE` (Android 12-)
3. Exibe a imagem selecionada usando Coil

**Permissões necessárias**:
- Android 13+: `READ_MEDIA_IMAGES`
- Android 12-: `READ_EXTERNAL_STORAGE`

**Vantagens do Photo Picker**:
- Interface moderna e consistente
- Pode dispensar permissões em alguns casos
- Melhor experiência do usuário

---

### 2. 📁 Selecionar Arquivo do Dispositivo

**Como funciona**:
1. Usa **Storage Access Framework (SAF)**
2. Não requer permissões explícitas (sistema gerencia)
3. Exibe o URI do arquivo selecionado

**Por que não precisa de permissão**:
- SAF é um sistema do Android que gerencia acesso a arquivos
- O usuário escolhe explicitamente qual arquivo compartilhar
- Não é necessário acesso amplo ao storage

**Uso**:
```kotlin
val filePicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
) { uri: Uri? ->
    // Processa arquivo selecionado
}
filePicker.launch(arrayOf("*/*"))  // Todos os tipos de arquivo
```

---

### 3. 📷 Tirar Foto com a Câmera

**Como funciona**:
1. Solicita permissão `CAMERA`
2. Cria URI segura usando `FileProvider` através do `CameraManager`
3. Abre câmera com `ActivityResultContracts.TakePicture`
4. Exibe a foto tirada

**Por que usar FileProvider**:
- Apps não podem compartilhar `file://` URIs diretamente
- `FileProvider` cria URIs seguras (`content://`) que podem ser compartilhadas
- Necessário para passar URI para o app de câmera

**Fluxo**:
```kotlin
// 1. Criar URI
val imageUri = cameraManager.createImageUri()

// 2. Abrir câmera
takePictureLauncher.launch(imageUri)

// 3. Câmera salva foto no URI
// 4. Launcher retorna sucesso
// 5. Exibir foto usando a URI
```

---

## 📝 Configurações Necessárias

### AndroidManifest.xml

**Permissões declaradas**:
```xml
<!-- Câmera -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Galeria (Android 13+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Galeria (Android 12 e abaixo) -->
<uses-permission 
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

**FileProvider configurado**:
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_provider_paths" />
</provider>
```

### res/xml/file_provider_paths.xml

Define os caminhos onde o FileProvider pode criar arquivos:
```xml
<paths>
    <external-files-path name="my_images" path="Pictures" />
    <cache-path name="my_cache" path="." />
</paths>
```

---

## 🎓 Conceitos Importantes Explicados

### 1. Photo Picker (Android 13+)

**O que é**: API moderna do Android para selecionar mídia (fotos, vídeos).

**Vantagens**:
- Interface consistente em todos os apps
- Pode dispensar permissões em alguns casos
- Melhor privacidade (usuário escolhe explicitamente)

**Como usar**:
```kotlin
val picker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
) { uri: Uri? ->
    // Processa URI selecionada
}
picker.launch(ActivityResultContracts.PickVisualMedia.ImageOnly)
```

---

### 2. Storage Access Framework (SAF)

**O que é**: Sistema do Android para acessar documentos e arquivos de forma segura.

**Características**:
- Não requer permissões explícitas
- Usuário escolhe explicitamente quais arquivos compartilhar
- Funciona com qualquer provedor de storage (local, nuvem, etc.)

**Como usar**:
```kotlin
val filePicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
) { uri: Uri? ->
    // Processa arquivo
}
filePicker.launch(arrayOf("*/*"))  // Todos os tipos
```

---

### 3. FileProvider

**O que é**: Provider do Android que cria URIs seguras para compartilhar arquivos entre apps.

**Por que é necessário**:
- Apps não podem compartilhar `file://` URIs diretamente (segurança)
- `FileProvider` cria URIs `content://` que podem ser compartilhadas
- Necessário para passar arquivos para outros apps (ex: câmera)

**Configuração**:
1. Declarar no `AndroidManifest.xml`
2. Criar `res/xml/file_provider_paths.xml`
3. Usar `FileProvider.getUriForFile()` para criar URIs

---

### 4. Rationale (Explicação)

**O que é**: Explicação mostrada ao usuário sobre por que uma permissão é necessária.

**Quando mostrar**:
- Usuário negou permissão anteriormente
- Sistema recomenda mostrar (`shouldShowRequestPermissionRationale()` retorna `true`)

**Como implementar**:
```kotlin
if (shouldShowRationale) {
    // Mostrar dialog explicativo
    AlertDialog(
        title = { Text("Permissão necessária") },
        text = { Text("Precisamos desta permissão para...") },
        // ...
    )
}
```

---

### 5. Permissão Permanentemente Negada

**O que é**: Estado quando usuário marcou "Não perguntar novamente".

**Como detectar**:
- `shouldShowRequestPermissionRationale()` retorna `false`
- E permissão não está concedida

**O que fazer**:
- Mostrar dialog explicando que precisa habilitar nas Configurações
- Oferecer botão para abrir Configurações do app
- Usar `Settings.ACTION_APPLICATION_DETAILS_SETTINGS`

**Código básico**:
```kotlin
val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.fromParts("package", context.packageName, null)
}
context.startActivity(intent)
```

---

### 6. Abertura Automática de Configurações

**O que é**: Funcionalidade implementada no app para abrir automaticamente as configurações do app quando uma permissão é permanentemente negada, e verificar automaticamente se foi habilitada quando o usuário volta.

**Como funciona**:

1. **ViewModel emite estado**:
   ```kotlin
   fun onOpenSettings(permissionType: PermissionType) {
       _uiState.update { it.copy(shouldOpenSettings = permissionType) }
   }
   ```

2. **UI observa e abre configurações**:
   ```kotlin
   LaunchedEffect(uiState.shouldOpenSettings) {
       uiState.shouldOpenSettings?.let { permissionType ->
           viewModel.onSettingsOpened()
           openAppSettings(context)
       }
   }
   ```

3. **Verificação automática ao voltar**:
   ```kotlin
   LaunchedEffect(uiState.shouldOpenSettings, permissionTypeToRecheck) {
       if (uiState.shouldOpenSettings == null && permissionTypeToRecheck != null) {
           delay(300)
           viewModel.recheckPermission(permissionTypeToRecheck)
       }
   }
   ```

**Fluxo completo**:
```
1. Usuário nega permissão permanentemente
   ↓
2. Dialog aparece: "Permissão bloqueada"
   ↓
3. Usuário clica em "Abrir Configurações"
   ↓
4. ViewModel.onOpenSettings(PermissionType.GALLERY)
   ↓
5. HomeUiState.shouldOpenSettings = GALLERY
   ↓
6. LaunchedEffect detecta e abre configurações automaticamente
   ↓
7. Usuário habilita permissão manualmente nas configurações
   ↓
8. Usuário volta para o app
   ↓
9. LaunchedEffect detecta que shouldOpenSettings voltou para null
   ↓
10. ViewModel.recheckPermission() verifica novamente o status
   ↓
11. Se concedida, estado é atualizado e funcionalidade pode ser usada
```

**Vantagens**:
- ✅ Abre configurações automaticamente
- ✅ Detecta quando usuário volta das configurações
- ✅ Verifica automaticamente se permissão foi habilitada
- ✅ Atualiza estado da UI automaticamente
- ✅ Funciona para todos os tipos de permissão

**Métodos do ViewModel relacionados**:
- `onOpenSettings(permissionType)`: Emite estado para abrir configurações
- `onSettingsOpened()`: Limpa estado após abrir
- `recheckPermission(permissionType)`: Verifica novamente status da permissão

---

## 🛠️ Como Reutilizar em Outros Projetos

### 1. Copiar Componentes de Permissão

**Arquivos para copiar**:
```
domain/model/
  ├── PermissionStatus.kt
  └── PermissionType.kt

domain/usecase/
  ├── CheckPermissionUseCase.kt
  └── GetRequiredPermissionsUseCase.kt

data/repository/
  └── PermissionRepository.kt

presentation/ui/state/
  └── PermissionUiState.kt
```

**Como usar**:
```kotlin
// No seu ViewModel
class MyViewModel(
    private val checkPermissionUseCase: CheckPermissionUseCase
) : ViewModel() {
    fun onSomeAction() {
        viewModelScope.launch {
            val status = checkPermissionUseCase(PermissionType.CAMERA)
            // Tratar status...
        }
    }
}
```

---

### 2. Padrão de Launchers

**Template reutilizável**:
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    val context = LocalContext.current
    
    // Launcher de permissão
    val permissionLauncher = rememberLauncherForActivityResult(
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
    
    // Observar estado e reagir
    LaunchedEffect(viewModel.permissionState) {
        when (viewModel.permissionState) {
            is PermissionUiState.RequestPermission -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
            // ...
        }
    }
}
```

---

### 3. Adicionar Novos Tipos de Permissão

**Passo 1**: Adicionar ao enum `PermissionType`
```kotlin
enum class PermissionType {
    // ... existentes
    LOCATION,  // Novo tipo
}
```

**Passo 2**: Atualizar `PermissionRepository.getRequiredPermissions()`
```kotlin
PermissionType.LOCATION -> {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
```

**Passo 3**: Adicionar permissão no `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

---

### 4. Implementar Abertura de Configurações

**Para adicionar a funcionalidade de abrir configurações automaticamente:**

**Passo 1**: Adicionar estado no seu `UiState`
```kotlin
data class MyUiState(
    // ... outros estados
    val shouldOpenSettings: PermissionType? = null
)
```

**Passo 2**: Adicionar métodos no ViewModel
```kotlin
class MyViewModel : ViewModel() {
    fun onOpenSettings(permissionType: PermissionType) {
        _uiState.update { it.copy(shouldOpenSettings = permissionType) }
    }
    
    fun onSettingsOpened() {
        _uiState.update { it.copy(shouldOpenSettings = null) }
    }
    
    fun recheckPermission(permissionType: PermissionType) {
        viewModelScope.launch {
            val status = checkPermissionUseCase(permissionType)
            // Atualizar estado baseado no novo status
        }
    }
}
```

**Passo 3**: Implementar na UI (Compose)
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var permissionTypeToRecheck by remember { mutableStateOf<PermissionType?>(null) }
    
    // Observa e abre configurações
    LaunchedEffect(uiState.shouldOpenSettings) {
        uiState.shouldOpenSettings?.let { permissionType ->
            permissionTypeToRecheck = permissionType
            viewModel.onSettingsOpened()
            openAppSettings(context)
        }
    }
    
    // Verifica quando usuário volta
    LaunchedEffect(uiState.shouldOpenSettings, permissionTypeToRecheck) {
        if (uiState.shouldOpenSettings == null && permissionTypeToRecheck != null) {
            delay(300)
            viewModel.recheckPermission(permissionTypeToRecheck)
            permissionTypeToRecheck = null
        }
    }
}

// Função auxiliar
private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
```

**Passo 4**: Usar no dialog de permissão bloqueada
```kotlin
if (permissionState is PermissionUiState.PermanentlyDenied) {
    AlertDialog(
        // ...
        confirmButton = {
            TextButton(onClick = {
                viewModel.onOpenSettings(PermissionType.CAMERA)
            }) {
                Text("Abrir Configurações")
            }
        }
    )
}
```

---

## 🐛 Troubleshooting

### Erro: "FileProvider não encontrado"

**Causa**: FileProvider não está configurado corretamente.

**Solução**:
1. Verifique se `file_provider_paths.xml` existe em `res/xml/`
2. Confirme que o `authority` no `AndroidManifest.xml` está correto:
   ```xml
   android:authorities="${applicationId}.fileprovider"
   ```
3. Verifique se o provider está dentro da tag `<application>`

---

### Permissão não é solicitada

**Causa**: Launcher não está sendo disparado ou permissão não está declarada.

**Solução**:
1. Verifique se a permissão está no `AndroidManifest.xml`
2. Confirme que o launcher está sendo chamado (adicione logs)
3. Verifique se o estado do ViewModel está correto
4. Veja logs do Android: `adb logcat | grep -i permission`

---

### Imagem não é exibida

**Causa**: URI incorreta ou problema de permissão de leitura.

**Solução**:
1. Verifique se a URI está correta (log o valor)
2. Confirme que o Coil está nas dependências
3. Verifique permissões de leitura do arquivo
4. Teste com uma URI de internet primeiro para verificar se o Coil funciona

---

### ViewModel não recebe resultado

**Causa**: Launcher não está notificando o ViewModel corretamente.

**Solução**:
1. Verifique se `viewModel.onPermissionResult()` está sendo chamado
2. Confirme que os parâmetros estão corretos
3. Adicione logs para rastrear o fluxo
4. Verifique se o ViewModel está sendo injetado corretamente (Koin)

---

## 📚 Referências e Documentação

### Documentação Oficial
- [Android Permissions Guide](https://developer.android.com/training/permissions)
- [Activity Result APIs](https://developer.android.com/training/basics/intents/result)
- [Photo Picker](https://developer.android.com/training/data-storage/shared/photopicker)
- [Storage Access Framework](https://developer.android.com/guide/topics/providers/document-provider)
- [FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider)

### Arquitetura
- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [MVVM Pattern](https://developer.android.com/topic/architecture/ui-layer)

### Bibliotecas
- [Koin Documentation](https://insert-koin.io/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

---

## 📄 Licença

Este projeto é um exemplo educacional. Sinta-se livre para usar, modificar e distribuir conforme necessário.

---

## 🎯 Próximos Passos

1. **Execute o projeto** e teste cada funcionalidade
2. **Leia os comentários** no código para entender detalhes
3. **Experimente modificar** os fluxos para aprender
4. **Adicione novos tipos de permissão** seguindo o padrão
5. **Reutilize os componentes** em seus próprios projetos

---

**Desenvolvido com foco em didática e boas práticas de Android moderno. 🚀**

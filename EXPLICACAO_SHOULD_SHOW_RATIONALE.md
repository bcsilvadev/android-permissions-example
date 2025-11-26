# 📚 Explicação: `shouldShowRequestPermissionRationale()`

## 🎯 O que é?

`shouldShowRequestPermissionRationale()` é um método do Android que indica se você **deve mostrar uma explicação** ao usuário sobre por que uma permissão é necessária, **antes de solicitar a permissão novamente**.

---

## ⏰ Quando é chamado?

### 1. **Após o usuário responder à solicitação de permissão**

O método é chamado **dentro do callback do launcher de permissão**, logo após o usuário responder (permitir ou negar).

**No nosso projeto, está aqui:**

```kotlin
val galleryPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    // ⬇️ AQUI: Chamado DEPOIS que o usuário respondeu
    val activity = context as? Activity
    val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(
        Manifest.permission.READ_MEDIA_IMAGES
    ) ?: false
    
    // Passa o resultado para o ViewModel
    viewModel.onPermissionResult(
        permissionType = PermissionType.GALLERY,
        granted = isGranted,
        shouldShowRationale = shouldShowRationale  // ⬅️ Passa o resultado
    )
}
```

### 2. **Momento exato no fluxo:**

```
1. App solicita permissão → permissionLauncher.launch(permission)
   ↓
2. Sistema mostra dialog: "Permitir que App acesse fotos?"
   ↓
3. Usuário escolhe: Permitir OU Negar
   ↓
4. Callback do launcher é executado: { isGranted -> ... }
   ↓
5. ⭐ AQUI: shouldShowRequestPermissionRationale() é chamado
   ↓
6. Baseado no resultado, decide o próximo passo
```

---

## 🔍 O que o método retorna?

O método retorna um **Boolean** que indica:

| Retorno | Significado | Quando acontece |
|---------|------------|------------------|
| `true` | **Deve mostrar rationale** | Usuário negou a permissão anteriormente, mas ainda pode ser convencido |
| `false` | **Não deve mostrar rationale** | Primeira vez negando OU usuário marcou "Não perguntar novamente" |

---

## 📊 Tabela de Comportamento

### Cenário 1: Primeira vez solicitando permissão
```
Usuário nunca viu a solicitação antes
↓
shouldShowRequestPermissionRationale() = false
↓
Ação: Solicitar permissão normalmente (sem rationale)
```

### Cenário 2: Usuário negou uma vez
```
Usuário já negou a permissão anteriormente
↓
shouldShowRequestPermissionRationale() = true
↓
Ação: Mostrar dialog explicativo (rationale) antes de solicitar novamente
```

### Cenário 3: Usuário marcou "Não perguntar novamente"
```
Usuário negou e marcou a checkbox "Não perguntar novamente"
↓
shouldShowRequestPermissionRationale() = false
↓
Ação: Não pode mais solicitar via dialog. Deve abrir Configurações.
```

### Cenário 4: Permissão já foi concedida
```
Usuário já concedeu a permissão
↓
shouldShowRequestPermissionRationale() = false (não importa, já tem permissão)
↓
Ação: Prosseguir com a funcionalidade
```

---

## 💻 Como está implementado no nosso projeto?

### 1. **No Launcher (HomeScreen.kt)**

```kotlin
val galleryPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    // ⬇️ Chamado APÓS o usuário responder
    val activity = context as? Activity
    val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    ) ?: false
    
    // Passa para o ViewModel
    viewModel.onPermissionResult(
        permissionType = PermissionType.GALLERY,
        granted = isGranted,
        shouldShowRationale = shouldShowRationale  // ⬅️ Resultado do método
    )
}
```

### 2. **No ViewModel (HomeViewModel.kt)**

```kotlin
fun onPermissionResult(
    permissionType: PermissionType,
    granted: Boolean,
    shouldShowRationale: Boolean  // ⬅️ Recebe o resultado
) {
    if (granted) {
        // Permissão concedida - prosseguir
        PermissionUiState.Granted
    } else if (shouldShowRationale) {
        // ⭐ Usuário negou, mas ainda pode ser convencido
        // Mostrar dialog de rationale
        PermissionUiState.ShowRationale(
            message = "Precisamos de acesso à galeria...",
            onConfirm = { requestPermissionAgain(permissionType) },
            onDismiss = { resetPermissionState(permissionType) }
        )
    } else {
        // ⭐ Usuário negou permanentemente
        // Oferecer abrir Configurações
        PermissionUiState.PermanentlyDenied(
            message = "Permissão bloqueada...",
            onOpenSettings = { onOpenSettings(permissionType) }
        )
    }
}
```

---

## 🔄 Fluxo Completo no Projeto

### Exemplo: Usuário nega permissão pela primeira vez

```
1. Usuário clica em "Selecionar foto da galeria"
   ↓
2. ViewModel verifica: permissão não concedida
   ↓
3. ViewModel emite: RequestPermission
   ↓
4. UI dispara: galleryPermissionLauncher.launch(permission)
   ↓
5. Sistema mostra dialog: "Permitir acesso a fotos?"
   ↓
6. Usuário escolhe: "Negar"
   ↓
7. Callback do launcher é executado: { isGranted = false -> ... }
   ↓
8. ⭐ shouldShowRequestPermissionRationale() é chamado
   Resultado: false (primeira vez negando)
   ↓
9. ViewModel recebe: shouldShowRationale = false
   ↓
10. ViewModel emite: PermanentlyDenied (mesmo sendo primeira vez)
    (Na prática, poderia ser apenas Denied, mas o sistema trata como permanente)
```

### Exemplo: Usuário nega pela segunda vez

```
1. Usuário clica novamente em "Selecionar foto da galeria"
   ↓
2. ViewModel verifica: permissão não concedida
   ↓
3. ViewModel emite: RequestPermission
   ↓
4. UI dispara: galleryPermissionLauncher.launch(permission)
   ↓
5. Sistema mostra dialog: "Permitir acesso a fotos?"
   ↓
6. Usuário escolhe: "Negar" (novamente)
   ↓
7. Callback do launcher é executado: { isGranted = false -> ... }
   ↓
8. ⭐ shouldShowRequestPermissionRationale() é chamado
   Resultado: true (já negou antes, mas não marcou "não perguntar")
   ↓
9. ViewModel recebe: shouldShowRationale = true
   ↓
10. ViewModel emite: ShowRationale
    ↓
11. UI mostra dialog: "Permissão necessária - Precisamos de acesso..."
    ↓
12. Se usuário clica "Entendi":
    → onConfirm() → requestPermissionAgain()
    → Solicita permissão novamente
```

### Exemplo: Usuário marca "Não perguntar novamente"

```
1. Usuário nega permissão várias vezes
   ↓
2. Sistema oferece checkbox: "Não perguntar novamente"
   ↓
3. Usuário marca a checkbox e nega
   ↓
4. Callback do launcher: { isGranted = false -> ... }
   ↓
5. ⭐ shouldShowRequestPermissionRationale() é chamado
   Resultado: false (marcou "não perguntar")
   ↓
6. ViewModel recebe: shouldShowRationale = false
   ↓
7. ViewModel emite: PermanentlyDenied
   ↓
8. UI mostra dialog: "Permissão bloqueada - Abrir Configurações"
```

---

## ⚠️ Pontos Importantes

### 1. **Só funciona dentro de uma Activity**

```kotlin
// ✅ Correto
val activity = context as? Activity
val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(...)

// ❌ Errado (não funciona com Context genérico)
val shouldShowRationale = context.shouldShowRequestPermissionRationale(...)
```

### 2. **Deve ser chamado DEPOIS da resposta do usuário**

```kotlin
// ✅ Correto - dentro do callback
permissionLauncher.launch(permission)
// ... usuário responde ...
{ isGranted ->
    val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(...)
}

// ❌ Errado - antes de solicitar
val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(...)
permissionLauncher.launch(permission)
```

### 3. **Comportamento pode variar por versão do Android**

- **Android 6.0-10**: Comportamento mais previsível
- **Android 11+**: Pode retornar `false` na primeira negação em alguns casos
- **Android 13+**: Comportamento mais consistente

---

## 📝 Resumo

| Quando é chamado? | Onde no código? | O que retorna? |
|-------------------|-----------------|----------------|
| **Após usuário responder** à solicitação de permissão | Dentro do callback do `permissionLauncher` | `true` = mostrar rationale<br>`false` = não mostrar |
| **Antes de decidir** o próximo passo | No método `onPermissionResult()` do ViewModel | Usado para decidir entre `ShowRationale` ou `PermanentlyDenied` |

---

## 🎓 Conclusão

`shouldShowRequestPermissionRationale()` é chamado **automaticamente pelo sistema Android** dentro do callback do launcher de permissão, **após o usuário responder** à solicitação. Ele indica se você deve mostrar uma explicação (rationale) antes de solicitar a permissão novamente.

**No nosso projeto:**
- É chamado nas linhas 75 e 98 de `HomeScreen.kt`
- O resultado é passado para o ViewModel
- O ViewModel usa para decidir entre mostrar rationale ou oferecer abrir Configurações



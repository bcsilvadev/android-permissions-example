package com.estudos.permissionsappexemple.domain.model

/**
 * Define os tipos de permissões que o app precisa gerenciar.
 * 
 * Cada tipo pode ter diferentes permissões dependendo da versão do Android:
 * - Android 13+ (API 33+): Usa permissões granulares (READ_MEDIA_IMAGES)
 * - Android 12 e abaixo: Usa permissões mais amplas (READ_EXTERNAL_STORAGE)
 */
enum class PermissionType {
    /**
     * Permissão para acessar imagens da galeria.
     * - Android 13+: READ_MEDIA_IMAGES
     * - Android 12-: READ_EXTERNAL_STORAGE
     * 
     * NOTA: Com Photo Picker (Android 13+), pode não ser necessário
     * solicitar permissão em alguns casos.
     */
    GALLERY,
    
    /**
     * Permissão para tirar fotos com a câmera.
     * - Todas as versões: CAMERA
     */
    CAMERA,
    
    /**
     * Para seleção de arquivos via SAF (Storage Access Framework),
     * geralmente não é necessário solicitar permissões explícitas.
     * O sistema gerencia isso automaticamente.
     */
    FILE_PICKER
}





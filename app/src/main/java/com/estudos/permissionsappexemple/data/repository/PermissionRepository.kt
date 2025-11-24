package com.estudos.permissionsappexemple.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.estudos.permissionsappexemple.domain.model.PermissionStatus
import com.estudos.permissionsappexemple.domain.model.PermissionType

/**
 * Interface do repositório de permissões.
 * 
 * Define o contrato para verificar status de permissões sem expor
 * detalhes de implementação Android (Context, PackageManager, etc.)
 * para as camadas superiores.
 */
interface PermissionRepository {
    /**
     * Verifica o status atual de uma permissão específica.
     * 
     * @param permissionType Tipo da permissão (GALLERY, CAMERA, etc.)
     * @return PermissionStatus indicando se está concedida, negada, etc.
     */
    fun checkPermissionStatus(permissionType: PermissionType): PermissionStatus
    
    /**
     * Verifica se uma permissão específica foi negada permanentemente
     * (usuário marcou "Não perguntar novamente").
     * 
     * NOTA: Esta verificação geralmente requer acesso a Activity,
     * então pode retornar false se não for possível determinar.
     * A verificação definitiva acontece no resultado do request.
     */
    fun isPermissionPermanentlyDenied(permissionType: PermissionType): Boolean
    
    /**
     * Retorna as permissões Android (strings) necessárias para um PermissionType.
     * 
     * Isso é necessário porque diferentes versões do Android usam
     * diferentes permissões para a mesma funcionalidade.
     * 
     * Exemplo:
     * - GALLERY em Android 13+ retorna ["android.permission.READ_MEDIA_IMAGES"]
     * - GALLERY em Android 12- retorna ["android.permission.READ_EXTERNAL_STORAGE"]
     */
    fun getRequiredPermissions(permissionType: PermissionType): List<String>
}

/**
 * Implementação concreta do PermissionRepository.
 * 
 * Esta classe encapsula toda a lógica de verificação de permissões,
 * incluindo a detecção da versão do Android e seleção das permissões corretas.
 */
class PermissionRepositoryImpl(
    private val context: Context
) : PermissionRepository {
    
    override fun checkPermissionStatus(permissionType: PermissionType): PermissionStatus {
        val permissions = getRequiredPermissions(permissionType)
        
        // Se não há permissões necessárias (ex: FILE_PICKER em Android moderno)
        if (permissions.isEmpty()) {
            return PermissionStatus.NotRequired
        }
        
        // Verifica se todas as permissões necessárias foram concedidas
        val allGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        
        return if (allGranted) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied
        }
    }
    
    override fun isPermissionPermanentlyDenied(permissionType: PermissionType): Boolean {
        // Esta verificação geralmente requer Activity.shouldShowRequestPermissionRationale(),
        // que não temos acesso aqui. Retornamos false e deixamos a verificação
        // definitiva acontecer no resultado do request na UI.
        return false
    }
    
    override fun getRequiredPermissions(permissionType: PermissionType): List<String> {
        return when (permissionType) {
            PermissionType.CAMERA -> {
                // Permissão de câmera é a mesma em todas as versões
                listOf(android.Manifest.permission.CAMERA)
            }
            
            PermissionType.GALLERY -> {
                // Android 13+ (API 33+) usa permissões granulares
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(android.Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    // Android 12 e abaixo usa READ_EXTERNAL_STORAGE
                    listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            
            PermissionType.FILE_PICKER -> {
                // Storage Access Framework (SAF) não requer permissões explícitas
                // O sistema gerencia isso automaticamente
                emptyList()
            }
        }
    }
}



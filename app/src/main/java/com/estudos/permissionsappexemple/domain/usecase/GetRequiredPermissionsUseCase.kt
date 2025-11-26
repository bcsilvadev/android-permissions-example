package com.estudos.permissionsappexemple.domain.usecase

import com.estudos.permissionsappexemple.domain.model.PermissionType
import com.estudos.permissionsappexemple.data.repository.PermissionRepository

/**
 * UseCase para obter as permissões Android (strings) necessárias para um tipo.
 * 
 * Este UseCase é usado pela UI para saber quais permissões solicitar
 * quando usar ActivityResultContracts.RequestPermission ou RequestMultiplePermissions.
 * 
 * Exemplo de uso:
 * - UI precisa solicitar permissão
 * - ViewModel chama este UseCase para obter as strings de permissão
 * - UI usa essas strings no launcher de permissão
 */
class GetRequiredPermissionsUseCase(
    private val permissionRepository: PermissionRepository
) {
    /**
     * Retorna as permissões Android necessárias para um PermissionType.
     * 
     * @param permissionType Tipo da permissão
     * @return Lista de strings de permissão (ex: ["android.permission.CAMERA"])
     */
    suspend operator fun invoke(permissionType: PermissionType): List<String> {
        return permissionRepository.getRequiredPermissions(permissionType)
    }
}





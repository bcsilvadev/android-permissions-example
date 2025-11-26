package com.estudos.permissionsappexemple.domain.usecase

import com.estudos.permissionsappexemple.domain.model.PermissionStatus
import com.estudos.permissionsappexemple.domain.model.PermissionType
import com.estudos.permissionsappexemple.data.repository.PermissionRepository

/**
 * UseCase para verificar o status de uma permissão.
 * 
 * Este UseCase encapsula a lógica de negócio de verificação de permissões.
 * Ele não conhece detalhes de implementação Android, apenas trabalha com
 * os modelos de domínio (PermissionType, PermissionStatus).
 * 
 * FLUXO:
 * 1. ViewModel chama este UseCase quando usuário tenta usar uma feature
 * 2. UseCase consulta PermissionRepository
 * 3. Retorna PermissionStatus
 * 4. ViewModel decide qual ação tomar baseado no status
 */
class CheckPermissionUseCase(
    private val permissionRepository: PermissionRepository
) {
    /**
     * Executa a verificação de permissão.
     * 
     * @param permissionType Tipo da permissão a verificar
     * @return Status atual da permissão
     */
    suspend operator fun invoke(permissionType: PermissionType): PermissionStatus {
        return permissionRepository.checkPermissionStatus(permissionType)
    }
}





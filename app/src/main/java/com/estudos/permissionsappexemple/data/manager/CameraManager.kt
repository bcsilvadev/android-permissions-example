package com.estudos.permissionsappexemple.data.manager

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gerencia operações relacionadas à câmera.
 * 
 * Responsável por:
 * - Criar URIs seguras para salvar fotos (usando FileProvider)
 * - Gerenciar arquivos temporários de fotos
 */
class CameraManager(
    private val context: Context,
    private val authority: String
) {
    
    /**
     * Cria uma URI segura para salvar uma foto tirada pela câmera.
     * 
     * Usa FileProvider para criar uma URI que pode ser compartilhada
     * com outros apps (como a câmera) de forma segura.
     * 
     * @return Uri onde a foto será salva, ou null se houver erro
     */
    fun createImageUri(): Uri? {
        return try {
            // Cria um arquivo temporário com nome único baseado em timestamp
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_${timeStamp}_"
            
            // Obtém o diretório de imagens externas do app
            val storageDir = context.getExternalFilesDir("Pictures")
            if (storageDir == null || !storageDir.exists()) {
                storageDir?.mkdirs()
            }
            
            // Cria o arquivo
            val imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            )
            
            // Cria a URI usando FileProvider (seguro para compartilhar com outros apps)
            FileProvider.getUriForFile(
                context,
                authority,
                imageFile
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Limpa arquivos temporários de fotos antigas (opcional, para gerenciar espaço)
     */
    fun cleanOldImages(maxAgeDays: Int = 7) {
        try {
            val storageDir = context.getExternalFilesDir("Pictures")
            if (storageDir?.exists() == true) {
                val files = storageDir.listFiles()
                val maxAge = System.currentTimeMillis() - (maxAgeDays * 24 * 60 * 60 * 1000L)
                
                files?.forEach { file ->
                    if (file.lastModified() < maxAge) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            // Ignora erros de limpeza
        }
    }
}





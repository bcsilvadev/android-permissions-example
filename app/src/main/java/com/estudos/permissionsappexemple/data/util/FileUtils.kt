package com.estudos.permissionsappexemple.data.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.File

/**
 * Utilitários para trabalhar com arquivos e URIs.
 * 
 * Fornece funções para extrair informações de arquivos a partir de URIs,
 * incluindo nome, tamanho, etc.
 */
object FileUtils {
    
    /**
     * Extrai o nome do arquivo de uma URI.
     * 
     * Esta função funciona com diferentes tipos de URI:
     * - content:// (Storage Access Framework, MediaStore, etc.)
     * - file:// (arquivos locais)
     * 
     * @param context Context necessário para consultar ContentResolver
     * @param uri URI do arquivo
     * @return Nome do arquivo ou null se não conseguir extrair
     */
    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        
        // Se a URI é do tipo content://
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )
            
            cursor?.use {
                if (it.moveToFirst()) {
                    // Tenta obter o nome usando OpenableColumns.DISPLAY_NAME
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = it.getString(nameIndex)
                    }
                }
            }
            
            // Se não conseguiu pelo método acima, tenta outros métodos
            if (result == null) {
                result = uri.path?.let { path ->
                    val cut = path.lastIndexOf('/')
                    if (cut != -1) {
                        path.substring(cut + 1)
                    } else {
                        null
                    }
                }
            }
        }
        
        // Se a URI é do tipo file://
        if (result == null && uri.scheme == "file") {
            result = uri.path?.let { path ->
                File(path).name
            }
        }
        
        // Última tentativa: extrair do path da URI
        if (result == null) {
            result = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) {
                    path.substring(cut + 1)
                } else {
                    path
                }
            }
        }
        
        return result
    }
    
    /**
     * Extrai o tamanho do arquivo de uma URI.
     * 
     * @param context Context necessário para consultar ContentResolver
     * @param uri URI do arquivo
     * @return Tamanho do arquivo em bytes, ou null se não conseguir obter
     */
    fun getFileSize(context: Context, uri: Uri): Long? {
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        val size = it.getLong(sizeIndex)
                        if (size > 0) {
                            return size
                        }
                    }
                }
            }
        }
        
        // Tenta obter tamanho do arquivo diretamente
        if (uri.scheme == "file") {
            uri.path?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    return file.length()
                }
            }
        }
        
        return null
    }
    
    /**
     * Formata o tamanho do arquivo em uma string legível.
     * 
     * @param size Tamanho em bytes
     * @return String formatada (ex: "1.5 MB", "500 KB")
     */
    fun formatFileSize(size: Long): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size bytes"
        }
    }
}


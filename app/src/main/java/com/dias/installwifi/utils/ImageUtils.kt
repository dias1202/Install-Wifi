package com.dias.installwifi.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

fun uriToFile(uri: Uri, context: Context): File? {
    // Simple implementation, you may want to improve this for production
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
    cursor?.moveToFirst()
    val columnIndex = cursor?.getColumnIndex(filePathColumn[0]) ?: -1
    val filePath = if (columnIndex != -1) cursor?.getString(columnIndex) else null
    cursor?.close()
    return filePath?.let { File(it) }
}
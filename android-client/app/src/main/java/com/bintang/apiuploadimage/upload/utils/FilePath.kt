package com.bintang.apiuploadimage.upload.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.DocumentsProvider
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object FilePath {
    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {
        var uri = uri
        val needToCheckUri = Build.VERSION.SDK_INT >= 19
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                return Environment.getExternalStorageDirectory()
                        .toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                when (type) {
                    "image" -> uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                        split[1]
                )
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(
                    MediaStore.Images.Media.DATA
            )
            try {
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                        .use { cursor ->
                            if (cursor != null && cursor.moveToFirst()) {
                                val columnIndex =
                                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                                return cursor.getString(columnIndex)
                            }
                        }
            } catch (e: Exception) {
                Log.e("on getPath", "Exception", e)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}

//object FilePath {
//    //private constructor to enforce Singleton pattern
//    /** TAG for log messages.  */
//    val TAG = "FileUtils"
//    val DEBUG = false // Set to true to enable logging
//
//    val MIME_TYPE_AUDIO = "audio/*"
//    val MIME_TYPE_TEXT = "text/*"
//    val MIME_TYPE_IMAGE = "image/*"
//    val MIME_TYPE_VIDEO = "video/*"
//    val MIME_TYPE_APP = "application/*"
//
//    val HIDDEN_PREFIX = "."
//
//    /**
//     * Gets the extension of a file name, like ".png" or ".jpg".
//     *
//     * @param uri
//     * @return Extension including the dot("."); "" if there is no extension;
//     * null if uri was null.
//     */
//    fun getExtension(uri: String?): String? {
//        if (uri == null) {
//            return null
//        }
//
//        val dot = uri.lastIndexOf(".")
//        return if (dot >= 0) {
//            uri.substring(dot)
//        } else {
//            // No extension.
//            ""
//        }
//    }
//
//    /**
//     * @return Whether the URI is a local one.
//     */
//    fun isLocal(url: String?): Boolean {
//        return if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
//            true
//        } else false
//    }
//
//    /**
//     * @return True if Uri is a MediaStore Uri.
//     * @author paulburke
//     */
//    fun isMediaUri(uri: Uri): Boolean {
//        return "media".equals(uri.authority!!, ignoreCase = true)
//    }
//
//    /**
//     * Convert File into Uri.
//     *
//     * @param file
//     * @return uri
//     */
//    fun getUri(file: File?): Uri? {
//        return if (file != null) {
//            Uri.fromFile(file)
//        } else null
//    }
//
//    /**
//     * Returns the path only (without file name).
//     *
//     * @param file
//     * @return
//     */
//    fun getPathWithoutFilename(file: File?): File? {
//        if (file != null) {
//            if (file.isDirectory()) {
//                // no file to be split off. Return everything
//                return file
//            } else {
//                val filename = file.getName()
//                val filepath = file.getAbsolutePath()
//
//                // Construct path without file name.
//                var pathwithoutname = filepath.substring(
//                    0,
//                    filepath.length - filename.length
//                )
//                if (pathwithoutname.endsWith("/")) {
//                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
//                }
//                return File(pathwithoutname)
//            }
//        }
//        return null
//    }
//
//    /**
//     * @return The MIME type for the given file.
//     */
//    fun getMimeType(file: File): String? {
//
//        val extension = getExtension(file.getName())
//
//        return if (extension!!.length > 0) MimeTypeMap.getSingleton()
//            .getMimeTypeFromExtension(extension.substring(1)) else "application/octet-stream"
//
//    }
//
//    /**
//     * @return The MIME type for the give Uri.
//     */
//    fun getMimeType(context: Context, uri: Uri): String? {
//        val file = File(getPath(context, uri))
//        return getMimeType(file)
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is [LocalStorageProvider].
//     * @author paulburke
//     */
//    fun isLocalStorageDocument(uri: Uri): Boolean {
//        return LocalStorageProvider.AUTHORITY.equals(uri.authority)
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is ExternalStorageProvider.
//     * @author paulburke
//     */
//    fun isExternalStorageDocument(uri: Uri): Boolean {
//        return "com.android.externalstorage.documents" == uri.authority
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is DownloadsProvider.
//     * @author paulburke
//     */
//    fun isDownloadsDocument(uri: Uri): Boolean {
//        return "com.android.providers.downloads.documents" == uri.authority
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is MediaProvider.
//     * @author paulburke
//     */
//    fun isMediaDocument(uri: Uri): Boolean {
//        return "com.android.providers.media.documents" == uri.authority
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is Google Photos.
//     */
//    fun isGooglePhotosUri(uri: Uri): Boolean {
//        return "com.google.android.apps.photos.content" == uri.authority
//    }
//
//    /**
//     * Get the value of the data column for this Uri. This is useful for
//     * MediaStore Uris, and other file-based ContentProviders.
//     *
//     * @param context The context.
//     * @param uri The Uri to query.
//     * @param selection (Optional) Filter used in the query.
//     * @param selectionArgs (Optional) Selection arguments used in the query.
//     * @return The value of the _data column, which is typically a file path.
//     * @author paulburke
//     */
//    fun getDataColumn(
//        context: Context, uri: Uri?, selection: String?,
//        selectionArgs: Array<String>?
//    ): String? {
//
//        var cursor: Cursor? = null
//        val column = "_data"
//        val projection = arrayOf(column)
//
//        try {
//            cursor =
//                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                if (DEBUG)
//                    DatabaseUtils.dumpCursor(cursor)
//
//                val column_index = cursor.getColumnIndexOrThrow(column)
//                return cursor.getString(column_index)
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close()
//        }
//        return null
//    }
//
//    /**
//     * Get a file path from a Uri. This will get the the path for Storage Access
//     * Framework Documents, as well as the _data field for the MediaStore and
//     * other file-based ContentProviders.<br></br>
//     * <br></br>
//     * Callers should check whether the path is local before assuming it
//     * represents a local file.
//     *
//     * @param context The context.
//     * @param uri The Uri to query.
//     * @see .isLocal
//     * @see .getFile
//     * @author paulburke
//     */
//    fun getPath(context: Context, uri: Uri): String? {
//
//        if (DEBUG)
//            Log.d(
//                "$TAG File -",
//                "Authority: " + uri.authority +
//                        ", Fragment: " + uri.fragment +
//                        ", Port: " + uri.port +
//                        ", Query: " + uri.query +
//                        ", Scheme: " + uri.scheme +
//                        ", Host: " + uri.host +
//                        ", Segments: " + uri.pathSegments.toString()
//            )
//
//        val isKitKat = Build.VERSION.SDK_INT <= Build.VERSION_CODES.M
//
//        // DocumentProvider
//        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            // LocalStorageProvider
//            if (isLocalStorageDocument(uri)) {
//                // The path is the id
//                return DocumentsContract.getDocumentId(uri)
//            } else if (isExternalStorageDocument(uri)) {
//                val docId = DocumentsContract.getDocumentId(uri)
//                val split =
//                    docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                val type = split[0]
//
//                if ("primary".equals(type, ignoreCase = true)) {
//                    return (Environment.getExternalStorageDirectory()).toString() + "/" + split[1]
//                }
//
//                // TODO handle non-primary volumes
//            } else if (isDownloadsDocument(uri)) {
//
//                val id = DocumentsContract.getDocumentId(uri)
//                val contentUri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
//                )
//
//                return getDataColumn(context, contentUri, null, null)
//
//            } else if (isMediaDocument(uri)) {
//                val docId = DocumentsContract.getDocumentId(uri)
//                val split =
//                    docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                val type = split[0]
//
//                var contentUri: Uri? = null
//                if ("image" == type) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                } else if ("video" == type) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                } else if ("audio" == type) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//                }
//
//                val selection = "_id=?"
//                val selectionArgs = arrayOf(split[1])
//
//                return getDataColumn(context, contentUri, selection, selectionArgs)
//            }// MediaProvider
//            // DownloadsProvider
//            // ExternalStorageProvider
//        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
//
//            Log.d("urity", uri.authority)
//            if (isGoogleDriveUri(uri)) {
//
//                return getDriveFilePath(uri, context)
//
//            } else if (isDownloadsDocument(uri)) {
//
//                val fileName = getFilePath(context, uri)
//                if (fileName != null) {
//                    return Environment.getExternalStorageDirectory()
//                        .toString() + "/Download/" + fileName
//                }
//
//                var id = DocumentsContract.getDocumentId(uri)
//                if (id.startsWith("raw:")) {
//                    id = id.replaceFirst("raw:".toRegex(), "")
//                    val file = File(id)
//                    if (file.exists()) return id
//                }
//
//                val contentUri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"),
//                    java.lang.Long.valueOf(id)
//                )
//                return getDataColumn1(context, contentUri, null, null)
//
//            } else if (isExternalStorageDocument(uri)) {
//
//                val docId = DocumentsContract.getDocumentId(uri)
//                val split = docId.split(":").toTypedArray()
//                val type = split[0]
//
//                // This is for checking Main Memory
//                // This is for checking Main Memory
//                return if ("primary".equals(type, ignoreCase = true)) {
//                    if (split.size > 1) {
//                        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
//                    } else {
//                        Environment.getExternalStorageDirectory().toString() + "/"
//                    }
//                    // This is for checking SD Card
//                } else {
//                    "storage" + "/" + docId.replace(":", "/")
//                }
//
//
////                val docId = DocumentsContract.getDocumentId(uri)
////                val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
////                val type = split[0]
////
////                if ("primary".equals(type, ignoreCase = true)) {
////                    return (Environment.getExternalStorageDirectory()).toString() + "/" + split[1]
////                }
//
//                // TODO handle non-primary volumes
//            }
//
//            // Return the remote address
//            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
//                context,
//                uri,
//                null,
//                null
//            )
//
//        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
//            return uri.path
//        }// File
//        // MediaStore (and general)
//
//        return null
//    }
//
//    fun getDataColumn1(
//        context: Context, uri: Uri?, selection: String?,
//        selectionArgs: Array<String?>?
//    ): String? {
//        var cursor: Cursor? = null
//        val column = "_data"
//        val projection = arrayOf(
//            column
//        )
//        try {
//            cursor = context.contentResolver.query(
//                uri!!, projection, selection, selectionArgs,
//                null
//            )
//            if (cursor != null && cursor.moveToFirst()) {
//                val index = cursor.getColumnIndexOrThrow(column)
//                return cursor.getString(index)
//            }
//        } finally {
//            cursor?.close()
//        }
//        return null
//    }
//
//    fun getFilePath(context: Context, uri: Uri?): String? {
//        var cursor: Cursor? = null
//        val projection = arrayOf(
//            MediaStore.MediaColumns.DISPLAY_NAME
//        )
//        try {
//            cursor = context.contentResolver.query(
//                uri!!, projection, null, null,
//                null
//            )
//            if (cursor != null && cursor.moveToFirst()) {
//                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
//                return cursor.getString(index)
//            }
//        } finally {
//            cursor?.close()
//        }
//        return null
//    }
//
//    private fun isGoogleDriveUri(uri: Uri): Boolean {
//        //content://com.google.android.apps.docs.storage/document/
//        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
//    }
//
//    fun getDriveFilePath(uri: Uri, context: Context): String? {
//        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
//        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//        val sizeIndex = returnCursor?.getColumnIndex(OpenableColumns.SIZE)
//        returnCursor?.moveToFirst()
//        val name = returnCursor?.getString(nameIndex ?: 0)
//        val size = java.lang.Long.toString(returnCursor?.getLong(sizeIndex ?: 0) ?: 0)
//        val file = File(context.cacheDir, name)
//        try {
//            val inputStream = context.contentResolver.openInputStream(uri)
//            val outputStream = FileOutputStream(file)
//            var read = 0
//            val maxBufferSize = 1 * 1024 * 1024
//            val bytesAvailable = inputStream?.available()
//            val bufferSize = Math.min(bytesAvailable ?: 0, maxBufferSize)
//            val buffers = ByteArray(bufferSize)
//            while (inputStream?.read(buffers).also { read = it ?: 0 } != -1) {
//                outputStream.write(buffers, 0, read)
//            }
//            Log.e("File Size", "Size " + file.length())
//            inputStream?.close()
//            outputStream.close()
//            Log.e("File Path", "Path " + file.path)
//            Log.e("File Size", "Size " + file.length())
//        } catch (e: Exception) {
//            Log.e("Exception", e.message)
//        }
//        return file.path
//    }
//
//
//    /**
//     * Convert Uri into File, if possible.
//     *
//     * @return file A local file that the Uri was pointing to, or null if the
//     * Uri is unsupported or pointed to a remote resource.
//     * @see .getPath
//     * @author paulburke
//     */
//    fun getFile(context: Context, uri: Uri?): File? {
//        if (uri != null) {
//            val path = getPath(context, uri)
//            if (path != null && isLocal(path)) {
//                return File(path)
//            }
//        }
//        return null
//    }
//
//    /**
//     * Get the file size in a human-readable string.
//     *
//     * @param size
//     * @return
//     * @author paulburke
//     */
//    fun getReadableFileSize(size: Int): String {
//        val BYTES_IN_KILOBYTES = 1024
//        val dec = DecimalFormat("###.#")
//        val KILOBYTES = " KB"
//        val MEGABYTES = " MB"
//        val GIGABYTES = " GB"
//        var fileSize = 0f
//        var suffix = KILOBYTES
//
//        if (size > BYTES_IN_KILOBYTES) {
//            fileSize = (size / BYTES_IN_KILOBYTES).toFloat()
//            if (fileSize > BYTES_IN_KILOBYTES) {
//                fileSize = fileSize / BYTES_IN_KILOBYTES
//                if (fileSize > BYTES_IN_KILOBYTES) {
//                    fileSize = fileSize / BYTES_IN_KILOBYTES
//                    suffix = GIGABYTES
//                } else {
//                    suffix = MEGABYTES
//                }
//            }
//        }
//        return (dec.format(fileSize) + suffix).toString()
//    }
//
//    var sComparator: Comparator<File> = object : Comparator<File> {
//        override fun compare(f1: File, f2: File): Int {
//            // Sort alphabetically by lower case, which is much cleaner
//            return f1.getName().toLowerCase().compareTo(
//                f2.getName().toLowerCase()
//            )
//        }
//    }
//
//    /**
//     * File (not directories) filter.
//     *
//     * @author paulburke
//     */
//    var sFileFilter: FileFilter = object : FileFilter {
//        override fun accept(file: File): Boolean {
//            val fileName = file.getName()
//            // Return files only (not directories) and skip hidden files
//            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX)
//        }
//    }
//
//    /**
//     * Folder (directories) filter.
//     *
//     * @author paulburke
//     */
//    var sDirFilter: FileFilter = object : FileFilter {
//        override fun accept(file: File): Boolean {
//            val fileName = file.getName()
//            // Return directories only and skip hidden directories
//            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX)
//        }
//    }
//
//    /**
//     * Get the Intent for selecting content to be used in an Intent Chooser.
//     *
//     * @return The intent for opening a file with Intent.createChooser()
//     * @author paulburke
//     */
//    fun createGetContentIntent(): Intent {
//        // Implicitly allow the user to select a particular kind of data
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        // The MIME data type filter
//        intent.type = "*/*"
//        // Only return URIs that can be opened with ContentResolver
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        return intent
//    }
//
//}

@RequiresApi(Build.VERSION_CODES.KITKAT)
class LocalStorageProvider : DocumentsProvider() {

    @Throws(FileNotFoundException::class)
    override fun queryRoots(projection: Array<String>?): Cursor {
        // Create a cursor with either the requested fields, or the default
        // projection if "projection" is null.
        val result = MatrixCursor(projection ?: DEFAULT_ROOT_PROJECTION)
        // Add Home directory
        val homeDir = Environment.getExternalStorageDirectory()
        val row = result.newRow()
        // These columns are required
        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, homeDir.absolutePath)
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, homeDir.absolutePath)
        row.add(DocumentsContract.Root.COLUMN_TITLE, "")
        row.add(
                DocumentsContract.Root.COLUMN_FLAGS,
                DocumentsContract.Root.FLAG_LOCAL_ONLY or DocumentsContract.Root.FLAG_SUPPORTS_CREATE
        )
        //row.add(DocumentsContract.Root.COLUMN_ICON, R.drawable.ic_provider)
        // These columns are optional
        row.add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, homeDir.freeSpace)
        // Root.COLUMN_MIME_TYPE is another optional column and useful if you
        // have multiple roots with different
        // types of mime types (roots that don't match the requested mime type
        // are automatically hidden)
        return result
    }

    @Throws(FileNotFoundException::class)
    override fun createDocument(
            parentDocumentId: String, mimeType: String,
            displayName: String
    ): String? {
        val newFile = File(parentDocumentId, displayName)
        try {
            newFile.createNewFile()
            return newFile.absolutePath
        } catch (e: IOException) {
            Log.e(LocalStorageProvider::class.java.simpleName, "Error creating new file $newFile")
        }

        return null
    }

    @Throws(FileNotFoundException::class)
    override fun openDocumentThumbnail(
            documentId: String, sizeHint: Point,
            signal: CancellationSignal
    ): AssetFileDescriptor? {
        // Assume documentId points to an image file. Build a thumbnail no
        // larger than twice the sizeHint
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(documentId, options)
        val targetHeight = 2 * sizeHint.y
        val targetWidth = 2 * sizeHint.x
        val height = options.outHeight
        val width = options.outWidth
        options.inSampleSize = 1
        if (height > targetHeight || width > targetWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / options.inSampleSize > targetHeight || halfWidth / options.inSampleSize > targetWidth) {
                options.inSampleSize *= 2
            }
        }
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(documentId, options)
        // Write out the thumbnail to a temporary file
        var tempFile: File? = null
        var out: FileOutputStream? = null
        try {
            tempFile = File.createTempFile("thumbnail", null, context!!.cacheDir)
            out = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: IOException) {
            Log.e(LocalStorageProvider::class.java.simpleName, "Error writing thumbnail", e)
            return null
        } finally {
            if (out != null)
                try {
                    out!!.close()
                } catch (e: IOException) {
                    Log.e(LocalStorageProvider::class.java.simpleName, "Error closing thumbnail", e)
                }

        }
        // It appears the Storage Framework UI caches these results quite
        // aggressively so there is little reason to
        // write your own caching layer beyond what you need to return a single
        // AssetFileDescriptor
        return AssetFileDescriptor(
                ParcelFileDescriptor.open(
                        tempFile,
                        ParcelFileDescriptor.MODE_READ_ONLY
                ), 0,
                AssetFileDescriptor.UNKNOWN_LENGTH
        )
    }

    override fun openDocument(
            documentId: String?,
            mode: String?,
            signal: CancellationSignal?
    ): ParcelFileDescriptor {
        val file = File(documentId)
        val isWrite = mode?.indexOf('w') != -1
        return if (isWrite) {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        } else {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        }
    }

    @Throws(FileNotFoundException::class)
    override fun queryChildDocuments(
            parentDocumentId: String, projection: Array<String>?,
            sortOrder: String
    ): Cursor {
        // Create a cursor with either the requested fields, or the default
        // projection if "projection" is null.
        val result = MatrixCursor(projection ?: DEFAULT_DOCUMENT_PROJECTION)
        val parent = File(parentDocumentId)
        for (file in parent.listFiles()!!) {
            // Don't show hidden files/folders
            if (!file.name.startsWith(".")) {
                // Adds the file's display name, MIME type, size, and so on.
                includeFile(result, file)
            }
        }
        return result
    }

    @Throws(FileNotFoundException::class)
    override fun queryDocument(documentId: String, projection: Array<String>?): Cursor {
        // Create a cursor with either the requested fields, or the default
        // projection if "projection" is null.
        val result = MatrixCursor(projection ?: DEFAULT_DOCUMENT_PROJECTION)
        includeFile(result, File(documentId))
        return result
    }

    @Throws(FileNotFoundException::class)
    private fun includeFile(result: MatrixCursor, file: File) {
        val row = result.newRow()
        // These columns are required
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, file.absolutePath)
        row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, file.name)
        val mimeType = getDocumentType(file.absolutePath)
        row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, mimeType)
        var flags = if (file.canWrite())
            DocumentsContract.Document.FLAG_SUPPORTS_DELETE or DocumentsContract.Document.FLAG_SUPPORTS_WRITE
        else
            0
        // We only show thumbnails for image files - expect a call to
        // openDocumentThumbnail for each file that has
        // this flag set
        if (mimeType.startsWith("image/"))
            flags = flags or DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL
        row.add(DocumentsContract.Document.COLUMN_FLAGS, flags)
        // COLUMN_SIZE is required, but can be null
        row.add(DocumentsContract.Document.COLUMN_SIZE, file.length())
        // These columns are optional
        row.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, file.lastModified())
        // Document.COLUMN_ICON can be a resource id identifying a custom icon.
        // The system provides default icons
        // based on mime type
        // Document.COLUMN_SUMMARY is optional additional information about the
        // file
    }

    @Throws(FileNotFoundException::class)
    override fun getDocumentType(documentId: String): String {
        val file = File(documentId)
        if (file.isDirectory)
            return DocumentsContract.Document.MIME_TYPE_DIR
        // From FileProvider.getType(Uri)
        val lastDot = file.name.lastIndexOf('.')
        if (lastDot >= 0) {
            val extension = file.name.substring(lastDot + 1)
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            if (mime != null) {
                return mime
            }
        }
        return "application/octet-stream"
    }

    @Throws(FileNotFoundException::class)
    override fun deleteDocument(documentId: String) {
        File(documentId).delete()
    }


    override fun onCreate(): Boolean {
        return true
    }

    companion object {

        val AUTHORITY = "com.bintang.apiuploadimage"

        /**
         * Default root projection: everything but Root.COLUMN_MIME_TYPES
         */
        private val DEFAULT_ROOT_PROJECTION = arrayOf<String>(
                DocumentsContract.Root.COLUMN_ROOT_ID,
                DocumentsContract.Root.COLUMN_FLAGS,
                DocumentsContract.Root.COLUMN_TITLE,
                DocumentsContract.Root.COLUMN_DOCUMENT_ID,
                DocumentsContract.Root.COLUMN_ICON,
                DocumentsContract.Root.COLUMN_AVAILABLE_BYTES
        )

        /**
         * Default document projection: everything but Document.COLUMN_ICON and
         * Document.COLUMN_SUMMARY
         */
        private val DEFAULT_DOCUMENT_PROJECTION = arrayOf<String>(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_FLAGS,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
        )
    }
}

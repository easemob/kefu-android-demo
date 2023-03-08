package com.hyphenate.helpdesk.videokit.uitls;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GetRealFilePathFromUri {


    /**
     * 根据Uri获取文件绝对路径，解决Android4.4以上版本Uri转换 兼容Android 10
     *
     * @param context Context
     * @param imageUri Uri
     * // Uri分为三个部分   域名://主机名/路径/id
     *     // content://media/extenral/images/media/17766
     *     // content://media/external/audio/media/1447156
     *     // content://media/external/video/media/1450711
     *
     *     // content://com.android.providers.media.documents/document/document%3A507
     *     // content://com.android.providers.media.documents/document/image%3A1563134
     *
     *     // content://com.android.providers.media.documents/document/image:2706
     *     // content://com.android.providers.media.documents/document/image%3A14122
     *     // content://com.android.externalstorage.documents/document/0EE0-F326%3A_hwclone%2Fcom.tencent.mm%2Ftencent%2FTMAssistantSDK%2FDownload%2Fcom.tencent.mm%2F52f5a2d59f00496cca08b78c0d63a59b
     *     // content://com.android.externalstorage.documents/document/0EE0-F326%3Agallery%20pages.pdf
     *     // content://cn.wps.moffice_eng.fileprovider/external/Android/data/com.easemob.helpdeskdemo/1411220310107978%23kefuchannelapp77561/0q9qeltre5x5mjt/file/%E4%B8%AA%E7%A8%8E%E4%B8%93%E9%A1%B9%E6%89%A3%E9%99%A4%E6%94%BF%E7%AD%96%E8%AF%A6%E8%A7%A3.pptx
     *     // file://com.xxxx.xxxxx ---- 7.0 有限制
     */
    public static String getFileAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return null;
        }
        int code = Build.VERSION.SDK_INT;
        int kitkat = Build.VERSION_CODES.KITKAT;

        //4.4以下的版本
        if (code < kitkat) {
            return getRealFilePath(context, imageUri);
        }

        //大于4.4，小于10

        if (code < 29 && DocumentsContract.isDocumentUri(context, imageUri)) {

            // OpenableColumns
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    contentUri = MediaStore.Files.getContentUri("external");

                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        if (code >= 29) {
            // MediaStore (and general)  大于等于10
            return uriToFileApiQ(context, imageUri);
        } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri)) {
                return imageUri.getLastPathSegment();
            }
            // 7.0
            if (Build.VERSION.SDK_INT >= 24) {
                return getFilePathFromUri(context, imageUri); //content 类型
            } else {
                return getDataColumn(context, imageUri, null, null);
            }
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    private static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] projection = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * Android 10 以上适配 另一种写法
     *
     * @param context Context
     * @param uri     Uri
     * @return String
     */
    @SuppressLint("Range")
    @SuppressWarnings("unused")
    private static String getFileFromContentUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                return filePath;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return "";
    }

    /**
     * Android 10 以上适配
     *
     * @param context Context
     * @param uri     Uri
     * @return String
     */
    private static String uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        //android10以上转换
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            file = new File(uri.getPath());
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null){
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    try {
                        InputStream is = contentResolver.openInputStream(uri);
                        File cache = new File(context.getFilesDir(), displayName);
                        if (cache.exists()){
                            //noinspection ResultOfMethodCallIgnored
                            cache.delete();
                        }
                        FileOutputStream fos = new FileOutputStream(cache);
                        assert is != null;
                        copy(is, fos);
                        file = cache;
                        fos.close();
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        cursor.close();
                    }
                }
                cursor.close();
            }
        }

        return file != null ? file.getAbsolutePath() : "";
    }

    private static void copy(InputStream inputStream, FileOutputStream outputStream) throws IOException {
        int read;
        // 1M
        int m = 1;
        int maxBufferSize = m * 1024 * 1024;
        int bytesAvailable = inputStream.available();
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        final byte[] buffers = new byte[bufferSize];
        while ((read = inputStream.read(buffers)) != -1) {
            outputStream.write(buffers, 0, read);
        }
    }

    private static String getFilePathFromUri(Context context, Uri uri) {
        String realFilePath = getRealFilePath(context, uri); //防止获取不到真实的地址，因此这里需要进行判断
        if (!TextUtils.isEmpty(realFilePath)) {
            return realFilePath;
        }
        File filesDir = context.getApplicationContext().getFilesDir();
        String fileName = getFileName(context, uri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(filesDir + File.separator + fileName);
            copyFile(context, uri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    @SuppressLint("Range")
    private static String getFileName(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String fileName = null;
        String path = uri.getPath();
        if (path != null) {
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                fileName = path.substring(cut + 1);
            }
        } else {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                    cursor.close();
                }
            }catch (Exception e){
                e.printStackTrace();
                if (cursor != null){
                    cursor.close();
                }
            }
        }
        return fileName;
    }

    private static void copyFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) {
                return;
            }
            if (dstFile.exists()){
                //noinspection ResultOfMethodCallIgnored
                dstFile.delete();
            }

            OutputStream outputStream = new FileOutputStream(dstFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void copyStream(InputStream input, OutputStream output) {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int n;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
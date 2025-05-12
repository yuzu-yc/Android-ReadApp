package com.example.read;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;

import com.example.read.nativeBook.BookDatabaseHelper;
import com.example.read.nativeBook.FileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImportBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList = new ArrayList<>();
    private ImageButton imsearchBackBtn; // 返回按钮

    // ActivityResultLauncher用于获取文件选择器的返回结果
    @SuppressLint("NotifyDataSetChanged")
    private final ActivityResultLauncher<Intent> openFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData(); // 获取选中的文件URI
                    if (uri != null) {
                        // 获取选中文件的路径
                        String fileNameWithoutExtension = getFileNameWithoutExtension(uri);
                        fileList.add(new File(fileNameWithoutExtension));  // 添加不带扩展名的文件名
                        fileAdapter.notifyDataSetChanged();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_books);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，则请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            // 已经有权限，可以执行访问文件的操作
            openFilePicker();
        }
        // 初始化RecyclerView
        recyclerView = findViewById(R.id.epubFileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取文件列表（这里使用Intent让用户选择文件）
        openFilePicker();

        // 初始化控件
        imsearchBackBtn = findViewById(R.id.imsearchBackBtn);
        // 设置返回按钮点击事件
        imsearchBackBtn.setOnClickListener(view -> finish());  // 结束当前 Activity，返回上一页面

        // 设置适配器
        fileAdapter = new FileAdapter(fileList, (file, position) -> {
            // 点击文件后，执行导入数据库的操作
            importBookToDatabase(file);
        });

        recyclerView.setAdapter(fileAdapter);
    }

    // 通过文件选择器让用户选择epub文件
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/epub+zip");  // 只允许选择epub文件
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);  // 不允许选择多个文件
        openFileLauncher.launch(intent);  // 启动文件选择器
    }


    // 导入EPUB到数据库的逻辑（这里暂时为占位方法）
    private void importBookToDatabase(File epubFile) {
        // 获取文件名（不带扩展名）
        String fileNameWithoutExtension = getFileNameWithoutExtension(Uri.fromFile(epubFile));

        // 获取文件路径
        String filePath = epubFile.getAbsolutePath();

        // 将书籍信息存入数据库
        BookDatabaseHelper dbHelper = new BookDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookDatabaseHelper.COLUMN_TITLE, fileNameWithoutExtension);  // 使用常量 COLUMN_TITLE
        values.put(BookDatabaseHelper.COLUMN_FILE_PATH, filePath);  // 使用常量 COLUMN_FILE_PATH


        // 插入数据
        long newRowId = db.insert("books", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "成功导入书籍: " + epubFile.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "导入失败", Toast.LENGTH_SHORT).show();
        }

        // 关闭数据库连接yey
        db.close();
        // 这里可以实现解析和导入书籍到数据库的具体逻辑
        Toast.makeText(this, "导入书籍: " + epubFile.getName(), Toast.LENGTH_SHORT).show();
    }

    // 获取文件的实际路径（适配Scoped Storage）
    private String getFileNameWithoutExtension(Uri uri) {
        String fileName = null;
        String path = getRealPathFromURI(uri);  // 获取文件的真实路径
        if (path != null) {
            File file = new File(path);
            fileName = file.getName();
            // 去掉文件扩展名
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = fileName.substring(0, dotIndex);  // 去掉扩展名
            }
        }
        return fileName;
    }

    // 获取文件的真实路径
    private String getRealPathFromURI(Uri uri) {
        String path = null;

        // 判断 URI 类型
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型 URI
            String[] projection = {DocumentsContract.Document.COLUMN_DISPLAY_NAME};
            try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME);
                    if (columnIndex != -1) {
                        path = cursor.getString(columnIndex); // 获取文件名
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 file 类型 URI，直接返回路径
            path = uri.getPath();
        }

        // 如果路径为空，尝试获取文件路径的绝对路径（适配 Android 12 及以上）
        if (path == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path = getPathFromUriForAndroidQAndAbove(uri);
        }

        return path;
    }

    // 适配 Android 10 及以上的 Scoped Storage
    @SuppressLint("Range")
    private String getPathFromUriForAndroidQAndAbove(Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 使用 DocumentsContract 处理文档 URI
            String docId = DocumentsContract.getDocumentId(uri);
            if (Objects.equals(uri.getAuthority(), "com.android.providers.media.documents")) {
                String id = docId.split(":")[1];
                Uri contentUri = MediaStore.Files.getContentUri("external");
                String selection = MediaStore.Files.FileColumns._ID + "=?";
                String[] selectionArgs = new String[]{id};
                Cursor cursor = getContentResolver().query(contentUri, null, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    cursor.close();
                }
            }
        }
        return path;
    }

}

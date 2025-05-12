package com.example.read.nativeBook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// 数据库帮助类，用于创建和管理书籍数据表，获取书籍的名字和文件地址
public class BookDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "books.db";
    private static final int BOOK_DATABASE_VERSION = 1;

    // 书籍表名
    public static final String TABLE_BOOKS = "books";

    // 必要字段
    public static final String COLUMN_ID = "id";           // 自增的ID
    public static final String COLUMN_TITLE = "title";     // 书名
    public static final String COLUMN_FILE_PATH = "file_path"; // 文件路径

    public BookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, BOOK_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建书籍信息表，只保留必要的字段
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"  // 自动增长的ID
        //删除了记录之后，id 不会回收这些删除的值）。
                + COLUMN_TITLE + " TEXT,"                              // 书名
                + COLUMN_FILE_PATH + " TEXT"                           // 文件路径
                + ")";
        db.execSQL(CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    // 插入书籍信息到数据库
    public void insertBook(String title, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);           // 书名
        values.put(COLUMN_FILE_PATH, filePath);   // 书籍文件路径
        db.insert(TABLE_BOOKS, null, values);      // 插入数据库
        db.close();
    }

    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // 查询表中的所有记录
        Cursor cursor = db.query(TABLE_BOOKS, null, null, null, null, null, null);

        if (cursor != null) {
            // 确保游标能够正确移动到第一行
            if (cursor.moveToFirst()) {
                do {
                    // 获取书名和文件路径的列索引，先检查列是否存在
                    int titleColumnIndex = cursor.getColumnIndex(COLUMN_TITLE);
                    int filePathColumnIndex = cursor.getColumnIndex(COLUMN_FILE_PATH);

                    // 如果列存在则读取数据
                    if (titleColumnIndex != -1 && filePathColumnIndex != -1) {
                        String title = cursor.getString(titleColumnIndex);
                        String filePath = cursor.getString(filePathColumnIndex);
                        // 添加到书籍列表
                        bookList.add(new Book(title, filePath));
                    } else {
                        // 如果某个列不存在，可以选择打印日志或处理错误
                        Log.e("getAllBooks", "某些列不存在: titleIndex=" + titleColumnIndex + ", filePathIndex=" + filePathColumnIndex);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();  // 关闭游标
        }
        db.close();  // 关闭数据库连接
        return bookList;
    }

}

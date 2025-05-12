package com.example.read.networkBook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.read.BookFetcher.BookItem;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    // 数据库名
    private static final String BASENAME = "novallist";
    // 书籍表的名称
    private static final String BOOK_TABLE = "booklist";
    // 所有书籍表的名称
    private static final String ALL_BOOK_TABLE = "all_booklist";

    // 书籍表的字段
    public static final String TITLE = "title";  // 书名
    public static final String IMG = "img";      // 书籍封面图片链接

    // 构造函数，创建数据库时调用
    public DataBase(@Nullable Context context) {
        super(context, BASENAME, null, 2);
    }

    // 创建数据库表（书籍表和所有书籍表）
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建书籍表 SQL 语句
        String createBookTableSQL = "CREATE TABLE IF NOT EXISTS " + BOOK_TABLE + " (" +
                TITLE + " TEXT NOT NULL, " + IMG + " TEXT NOT NULL, " +
                "PRIMARY KEY(" + TITLE + "));";

        // 创建所有书籍表 SQL 语句
        String createAllBookTableSQL = "CREATE TABLE IF NOT EXISTS " + ALL_BOOK_TABLE + " (" +
                TITLE + " TEXT NOT NULL, " + IMG + " TEXT NOT NULL, " +
                "PRIMARY KEY(" + TITLE + "));";

        // 执行 SQL 语句
        sqLiteDatabase.execSQL(createBookTableSQL);
        sqLiteDatabase.execSQL(createAllBookTableSQL);
    }

    // 数据库升级时调用，处理版本更新的逻辑
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // 升级逻辑，简单清空旧表重新创建
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ALL_BOOK_TABLE);

        onCreate(sqLiteDatabase);
    }

    // 添加书籍
    public boolean addBook(BookItem bookItem) {
        // 创建 ContentValues，用于存储书籍信息
        ContentValues cv = new ContentValues();
        cv.put(TITLE, bookItem.getTitle());  // 书名
        cv.put(IMG, bookItem.getImgUrl());   // 封面图片链接

        // 获取可写的数据库实例
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean isInserted = false;

        try {
            // 执行插入操作，返回插入的行数（-1表示失败）
            long insert = sqLiteDatabase.insert(BOOK_TABLE, null, cv);
            isInserted = insert != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭数据库
            sqLiteDatabase.close();
        }

        // 返回操作是否成功
        return isInserted;
    }


    public boolean addAllBook(BookItem bookItem) {
        // 创建 ContentValues，用于存储书籍信息
        ContentValues cv = new ContentValues();
        cv.put(TITLE, bookItem.getTitle());  // 书名
        cv.put(IMG, bookItem.getImgUrl());   // 封面图片链接

        // 获取可写的数据库实例
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean isInserted = false;

        try {
            // 执行插入操作，返回插入的行数（-1表示失败）
            long insert = sqLiteDatabase.insert(ALL_BOOK_TABLE, null, cv);
            isInserted = insert != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭数据库
            sqLiteDatabase.close();
        }

        // 返回操作是否成功
        return isInserted;
    }

    // 删除书籍
    public boolean deleteBook(BookItem bookItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean isDeleted = false;

        try {
            // 执行删除操作，返回删除的行数
            int num = sqLiteDatabase.delete(BOOK_TABLE, TITLE + "=?", new String[]{bookItem.getTitle()});
            isDeleted = num != 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭数据库
            sqLiteDatabase.close();
        }

        return isDeleted;
    }

    // 判断书籍是否已经存在
    public boolean isExist(BookItem bookItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            // 执行查询操作，检查是否已经有该书籍
            cursor = sqLiteDatabase.query(BOOK_TABLE, null, TITLE + "=?",
                    new String[]{bookItem.getTitle()}, null, null, null);

            // 如果查询到记录，表示该书籍已存在
            exists = cursor.getCount() != 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭游标和数据库
            if (cursor != null) cursor.close();
            sqLiteDatabase.close();
        }

        return exists;
    }

    // 获取所有书籍
    public List<BookItem> getBooks() {
        List<BookItem> books = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            // 执行查询操作，获取所有书籍
            cursor = sqLiteDatabase.query(BOOK_TABLE, null,
                    null, null, null, null, null);

            // 如果查询到了数据
            if (cursor != null) {
                // 遍历结果
                while (cursor.moveToNext()) {
                    // 获取书名和封面链接
                    String title = cursor.getString(cursor.getColumnIndex(TITLE));
                    String img = cursor.getString(cursor.getColumnIndex(IMG));

                    // 将书籍信息存入列表
                    books.add(new BookItem(title, img));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭游标和数据库
            if (cursor != null) cursor.close();
            sqLiteDatabase.close();
        }

        return books;
    }
    // 获取所有书籍，包括booklist和all_booklist表的数据
    public List<BookItem> getAllBooks() {
        List<BookItem> books = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = null;

        try {

            // 关闭游标并重新查询all_booklist表
            if (cursor != null) cursor.close();
            cursor = sqLiteDatabase.query(ALL_BOOK_TABLE, null,
                    null, null, null, null, null);

            // 遍历all_booklist表的结果
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(TITLE));
                    String img = cursor.getString(cursor.getColumnIndex(IMG));

                    // 将书籍信息存入列表
                    books.add(new BookItem(title, img));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭游标和数据库
            if (cursor != null) cursor.close();
            sqLiteDatabase.close();
        }

        return books;
    }
    // 在 DataBase 类中添加按书名查询的方法
    public List<BookItem> getBooksByTitle(String title) {
        List<BookItem> books = new ArrayList<>();
        Log.d("DatabaseTest", "Querying for: " + title);  // 打印查询条件
        try (SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(); Cursor cursor = sqLiteDatabase.query(ALL_BOOK_TABLE, null,
                TITLE + " LIKE ?", new String[]{"%" + title + "%"}, null, null, null)) {
            // 执行查询操作，按书名模糊查询书籍

            // 遍历结果
            while (cursor != null && cursor.moveToNext()) {
                String bookTitle = cursor.getString(cursor.getColumnIndex(TITLE));
                String imgUrl = cursor.getString(cursor.getColumnIndex(IMG));

                // 将书籍信息存入列表
                books.add(new BookItem(bookTitle, imgUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭游标和数据库

        return books;
    }

}


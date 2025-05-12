package com.example.read.nativeBook;

public class Book {
    private String title;   // 书籍标题
    private int imageResId; // 书籍封面图片资源 ID
    private String filePath; // 书籍文件路径，用于存储本地文件路径


    ;

    // 构造函数，保留原有的封面图片和标题，同时新增书籍文件路径和格式
    public Book(int imageResId, String title, String filePath) {
        this.imageResId = imageResId;
        this.title = title;
        this.filePath = filePath;

    }
    public Book(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;

    }

//暂时为recycle的页面展示
    public Book(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }


    // 获取封面图片资源 ID
    public int getImageResId() {
        return imageResId;
    }

    // 获取书籍标题
    public String getTitle() {
        return title;
    }

    // 获取书籍文件路径
    public String getFilePath() {
        return filePath;
    }
}




package com.example.read;

import android.app.Application;
import java.util.List;

public class BookInfoState extends Application {
    // 存储章节链接的列表
    private List<String> chapters;
    // 当前阅读章节的索引，初始为0（表示第一个章节）
    private int chapter_id = 0;
    // 存储章节标题的列表
    private List<String> titles;
    private String bookTitle;  // 当前书籍的标题
    // 单例实例，确保全局只有一个 BookInfoState 对象
    private static BookInfoState instance;
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化单例实例
        instance = this;
    }

    // 获取单例实例
    public static BookInfoState getInstance() {
        return instance;
    }
    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    // 获取当前章节的索引
    public int getChapter_id() {
        return chapter_id;
    }

    // 设置当前章节的索引
    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    // 获取章节链接列表
    public List<String> getChapters() {
        return chapters;
    }

    // 设置章节链接列表
    public void setChapters(List<String> chapters) {
        this.chapters = chapters;
    }

    // 增加章节索引，确保不超出章节列表的范围
    public void addID() {
        if (this.chapter_id != chapters.size()) {
            this.chapter_id++; // 章节索引增加
        }
    }

    // 减少章节索引，确保索引不小于0
    public void desID() {
        if (this.chapter_id > 0) {
            this.chapter_id--; // 章节索引减少
        }
    }

    // 获取章节标题列表
    public List<String> getTitles() {
        return titles;
    }

    // 设置章节标题列表
    public void setTitles(List<String> titles) {
        this.titles = titles;
    }
}

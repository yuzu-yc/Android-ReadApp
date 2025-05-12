package com.example.read.BookFetcher;

import java.io.Serializable;

public class BookItem implements Serializable {

    private String title;    // 书名
    private String imgUrl;   // 封面图像 URL

    // 构造函数
    public BookItem(String title, String imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }

    // 获取书名
    public String getTitle() {
        return title;
    }

    // 设置书名
    public void setTitle(String title) {
        this.title = title;
    }

    // 获取封面图像 URL
    public String getImgUrl() {
        return imgUrl;
    }

    // 设置封面图像 URL
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}

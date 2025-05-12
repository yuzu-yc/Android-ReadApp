package com.example.read.BookFetcher;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.read.networkBook.DataBase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookFetcher {

    private Context context;
    private Handler mainHandler;
    private int type;
    private OnBooksFetchedListener listener;
    private DataBase database;  // 新增数据库实例

    public BookFetcher(Context context, int type, OnBooksFetchedListener listener) {
        this.context = context;
        this.type = type;
        this.listener = listener;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.database = new DataBase(context);
    }

    // 定义回调接口
    public interface OnBooksFetchedListener {
        void onBooksFetched(List<BookItem> books, List<String> urls, List<String> titles);
        void onError(String message);
    }

    // 加载书籍数据的方法
    public void fetchBooks() {
        new Thread(() -> {
            try {
                Document document = Jsoup.connect("http://www.xbiqugu.net/paihangbang/").timeout(10000).
                        userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (HTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36").
                        get();
                Element node = selectNodeByType(document);
                if (node != null) {
                    List<BookItem> books = new ArrayList<>();
                    List<String> urls = new ArrayList<>();
                    List<String> titles = new ArrayList<>();
                    parseBookData(node, books, urls, titles);
                    insertBooksToDatabase(books);
                    // 更新 UI
                    mainHandler.post(() -> listener.onBooksFetched(books, urls, titles));
                } else {
                    // 失败时，回调错误信息
                    showError("数据加载失败，请稍后再试。");
                }
            } catch (IOException e) {
                showError("网络连接失败，请检查网络连接。");
            }
        }).start();
    }


    // 根据类型选择合适的节点
    private Element selectNodeByType(Document document) {
        switch (type) {
            case 1://玄幻奇幻
                return document.selectFirst("div[class=box b1]").selectFirst("ul");
            case 2://修真仙侠
                return document.selectFirst("div[class=box b2]").selectFirst("ul");
            case 3://都市青春
                return document.selectFirst("div[class=box b3]").selectFirst("ul");
            case 4://历史穿越
                return document.selectFirst("div[class=box b4]").select("ul").get(0);
            case 5://网游竞技
                return document.selectFirst("div[class=box b1]").select("ul").get(1);
            case 6://科幻灵异
                return document.selectFirst("div[class=box b2]").select("ul").get(1);
            case 0://推荐
                return document.selectFirst("div[class=box b3]").select("ul").get(1);
            default:
                return null;
        }
    }

    // 解析书籍数据
    private void parseBookData(Element node, List<BookItem> books, List<String> urls, List<String> titles) {
        Elements el = node.select("li");
        for (int i = 0; i < el.size() - 1; i++) {
            Element l = el.get(i);
            if (l.className().equals("ltitle")) {
                continue;
            }
            String title = l.select("a").text();
            String url = l.select("a").attr("abs:href");
            String imgUrl = getCover(url);
            BookItem book = new BookItem(title, imgUrl);
            books.add(book);
            urls.add(url);
            titles.add(title);
        }
    }
    // 使用现有的数据库方法插入书籍数据
    private void insertBooksToDatabase(List<BookItem> books) {
        for (BookItem book : books) {
            // 调用现有的 addBook 方法插入书籍
            database.addAllBook(book); // 使用现有的 addBook 方法
        }
    }

    // 获取封面图片 URL
    private String getCover(String url) {
        String last = url.replace("http://www.xbiqugu.net/", "");
        String s = last.split("/")[1];
        return "http://www.xbiqugu.net/files/article/image/" + last + s + "s.jpg";
    }

    // 错误处理回调
    private void showError(final String message) {
        mainHandler.post(() -> listener.onError(message));
    }
}

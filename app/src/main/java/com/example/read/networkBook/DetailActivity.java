package com.example.read.networkBook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.read.BookFetcher.BookItem;
import com.example.read.BookInfoState;
import com.example.read.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView chapterList;
    List<String> chapterLink = new ArrayList<>();
    List<String> chapters = new ArrayList<>();
    FrameLayout progressContainer; // 用于显示进度条的容器
    BookInfoState app;
    ImageView cover, back;
    TextView info;
    Button showBtn, closeBtn, readBtn, addBtn;
    RelativeLayout list;

    String title; // 小说名
    String coverLink; // 小说封面
    DataBase sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        app = (BookInfoState) getApplicationContext();

        sql = new DataBase(DetailActivity.this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");

        back = findViewById(R.id.dback);
        back.setOnClickListener(view -> finish());

        chapterList = findViewById(R.id.chapters);
        info = findViewById(R.id.info);
        cover = findViewById(R.id.cover);
        showBtn = findViewById(R.id.showChapter);
        closeBtn = findViewById(R.id.close);
        list = findViewById(R.id.list);
        readBtn = findViewById(R.id.read);
        addBtn = findViewById(R.id.addList);
        progressContainer = findViewById(R.id.progress_container); // 获取ProgressBar的容器

        updateAddButtonState(); // 更新书架按钮状态

        addBtn.setOnClickListener(view -> {
            BookItem book = new BookItem(title, coverLink);
            if (!sql.isExist(book)) {
                sql.addBook(book);
                Toast.makeText(DetailActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                updateAddButtonState(); // 更新按钮状态
            } else {
                sql.deleteBook(book);
                updateAddButtonState(); // 更新按钮状态
                Toast.makeText(DetailActivity.this, "删除成功", Toast.LENGTH_LONG).show();
            }
        });

        chapterList.setOnItemClickListener(this);
        loadChapter(url);
        showBtn.setOnClickListener(view -> list.setVisibility(View.VISIBLE));
        closeBtn.setOnClickListener(view -> list.setVisibility(View.GONE));
        readBtn.setOnClickListener(view -> {
            Intent intent1 = new Intent(DetailActivity.this, PageActivity.class);
            intent1.putExtra("link", chapterLink.get(0));
            intent1.putExtra("chaptersTitle", chapters.get(0));
            intent1.putExtra("bookTitle", title); // 传递书名
            startActivity(intent1);
        });
    }

    // 更新书架按钮状态
    private void updateAddButtonState() {
        if (sql.isExist(new BookItem(title, coverLink))) {
            addBtn.setText("拿出书架");
            addBtn.setTextColor(Color.RED);  // 设置按钮字体颜色为红色
        } else {
            addBtn.setText("加入书架");
            addBtn.setTextColor(Color.WHITE);  // 设置按钮字体颜色为白色
        }
    }

    public void loadChapter(String url) {
        showProgressBar(); // 显示ProgressBar

        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (HTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .get();
                // 获取章节内容
                Elements link = doc.select("#list>dl>dd>a");//列表下的列表内容的链接
                Elements title = doc.select("#list>dl>dd>a");
                coverLink = doc.select("#fmimg>img").attr("abs:src"); // 获取封面图片的URL
                String intro = doc.select("#intro>p").get(1).text();//书籍简介
                // 获取章节链接和标题
                for (Element l : link) {
                    chapterLink.add(l.attr("abs:href"));
                }
                for (Element s : title) {
                    chapters.add(s.text());
                }
                // 在主线程更新UI
                runOnUiThread(() -> {
                    ChapterAdapter adapter = new ChapterAdapter(DetailActivity.this, R.layout.item, chapters);
                    chapterList.setAdapter(adapter);
                    // 使用Glide加载封面图片
                    Glide.with(getCurrentContext())
                            .load(coverLink)
                            .error(R.drawable.bookcover) // 默认封面图片
                            .into(cover);
                    info.setText(intro);
                    hideProgressBar(); // 隐藏ProgressBar
                });

            } catch (IOException e) {
                e.printStackTrace(); // 打印异常，便于调试
                runOnUiThread(() -> {
                    hideProgressBar(); // 隐藏ProgressBar
                    Toast.makeText(DetailActivity.this, "加载失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

        app.setChapters(chapterLink);
        app.setChapter_id(0);
        app.setTitles(chapters);
    }

    // 显示进度条
    private void showProgressBar() {
        runOnUiThread(() -> progressContainer.setVisibility(View.VISIBLE));
    }

    // 隐藏进度条
    private void hideProgressBar() {
        runOnUiThread(() -> progressContainer.setVisibility(View.GONE));
    }

    public Context getCurrentContext() {
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(this, PageActivity.class);
        intent.putExtra("link1", chapterLink.get(position));
        intent.putExtra("chaptersTitle", chapters.get(position));
        app.setChapter_id(position);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保在活动销毁时关闭ProgressBar
        hideProgressBar();
    }
}

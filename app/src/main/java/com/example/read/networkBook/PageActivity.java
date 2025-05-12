package com.example.read.networkBook;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.read.BookInfoState;
import com.example.read.Progress.ReadProgressUtil;
import com.example.read.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageActivity extends AppCompatActivity {
    ViewPager2 container;  // 用于显示章节内容的ViewPager2控件
    String chaptersTitle;  // 当前章节标题
    String bookTitle; // 当前书名
    MyPageAdapter myPageAdapter, temp;  // 自定义适配器
    BookInfoState app;  // 书籍信息管理类的实例
    boolean flag = true, isPre = false;  // 控制页码的标志位

    // 进度条相关控件
    FrameLayout progressContainer;
    ProgressBar progressBar;

    int lastPage = 0; // 默认页码为0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // 启用全屏显示
        setContentView(R.layout.activity_page);  // 设置页面布局

        // 设置系统UI的边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取Intent数据
        Intent intent = getIntent();
        String passedLink = intent.getStringExtra("link1"); // 章节列表点击入
        String passedTitle = intent.getStringExtra("chaptersTitle");
        bookTitle = intent.getStringExtra("bookTitle");

        // 初始化进度条控件
        progressContainer = findViewById(R.id.progress_container2);
        progressBar = findViewById(R.id.progressBar2);

        container = findViewById(R.id.container);  // 初始化ViewPager2控件
        app = BookInfoState.getInstance();  // 获取书籍信息实例

        // 判断是从章节列表点击进入还是恢复阅读进度
        if (passedLink != null && passedTitle != null) {
            // 从章节列表点击进入
            chaptersTitle = passedTitle;
            getContent(passedLink, true);  // 传递true表示是新章节
        } else if (ReadProgressUtil.hasProgress(this, bookTitle)) {
            // 恢复阅读进度
            int[] progress = ReadProgressUtil.getProgress(this, bookTitle);
            int lastChapter = progress[0];
            lastPage = progress[1];

            app.setChapter_id(lastChapter);
            chaptersTitle = app.getTitles().get(lastChapter);
            getContent(app.getChapters().get(lastChapter), false);  // 传递false表示恢复进度

            // 在数据加载完成后设置当前页面
            container.post(() -> container.setCurrentItem(lastPage));
        } else {
            // 默认从第一章开始
            app.setChapter_id(0);
            chaptersTitle = app.getTitles().get(0);
            getContent(app.getChapters().get(0), true);  // 默认进入新章节

            container.post(() -> container.setCurrentItem(0));
        }

        // 注册页面切换回调
        container.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                flag = false;  // 取消初始标志
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                // 当滚动到最后一页时，加载下一章
                if (myPageAdapter.getItemCount() == position + 1) {
                    app.addID();  // 进入下一章
                    chaptersTitle = app.getTitles().get(app.getChapter_id());
                    getContent(app.getChapters().get(app.getChapter_id()), true);  // 传递true表示新章节
                }
                // 如果是第一页且不是初次加载，加载上一章
                if (position == 0 && !flag) {
                    isPre = true;
                    app.desID();  // 进入上一章
                    chaptersTitle = app.getTitles().get(app.getChapter_id());
                    getContent(app.getChapters().get(app.getChapter_id()), false);  // 传递false表示恢复进度
                }
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 保存当前的章节和页面
        int currentChapter = app.getChapter_id();
        int currentPage = container.getCurrentItem();
        // 保存进度
        ReadProgressUtil.saveProgress(this, bookTitle, currentChapter, currentPage);
    }

    public void getContent(String url, boolean isNewChapter) {
        progressContainer.setVisibility(View.VISIBLE);  // 显示加载进度条
        new Thread(() -> {
            try {
                Document document = Jsoup.connect(url).get();  // 获取网页内容
                List<TextNode> doc = document.select("#content").textNodes();  // 获取内容部分的文本节点
                StringBuilder contentBuilder = new StringBuilder();
                for (TextNode textNode : doc) {
                    contentBuilder.append(textNode.text().trim());
                }

                // 清理并格式化文本内容
                String finalContent = contentBuilder.toString()
                        .replace("&nbsp;&nbsp;&nbsp;&nbsp;", "\u3000\u3000")
                        .replace("\n", "")
                        .replaceAll("([。！？])", "$1\n")
                        .trim();

                // 获取屏幕尺寸，并动态调整每页显示的字符数
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels;
                int screenWidth = displayMetrics.widthPixels;

                // 使用屏幕的高度和宽度计算每页可以显示的字符数
                int charsPerPage = (screenHeight * screenWidth) / 6600; // 这个值根据实际情况调整
                List<String> splitContent = stringToStringArray(finalContent, charsPerPage);
                runOnUiThread(() -> {
                    initViewPager(container, splitContent, isNewChapter);  // 根据章节是否是新章节决定初始页
                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                runOnUiThread(() -> {
                    progressContainer.setVisibility(View.GONE);  // 隐藏加载进度条
                });
            }
        }).start();
    }

    private void initViewPager(ViewPager2 viewPager2, List<String> content, boolean isNewChapter) {
        // 初始化ViewPager2的数据源
        List<PagerFragment> list = new ArrayList<>();
        list.add(new PagerFragment("", 0, 0));  // 添加上一章（空白页）
        for (int i = 0; i < content.size(); i++) {
            if (i == 0) {
                list.add(new PagerFragment(content.get(i), chaptersTitle, i, content.size()));  // 第一页加入标题
            } else {
                list.add(new PagerFragment(content.get(i), i, content.size()));
            }
        }
        list.add(new PagerFragment("", 0, 0));  // 添加下一章（空白页）

        // 设置适配器
        myPageAdapter = new MyPageAdapter(getSupportFragmentManager(), getLifecycle());
        myPageAdapter.setFragmentList(list);

        // 设置ViewPager2适配器
        viewPager2.setAdapter(myPageAdapter);
// 根据标志决定初始页码(不能并列)
        if (isPre) {
            container.setCurrentItem(myPageAdapter.getItemCount() - 2);  // 跳转到上一章
            isPre = false;
        }
        // 根据是否是新章节来决定初始页
        if (isNewChapter) {
            viewPager2.setCurrentItem(1);  // 新章节从第一页开始
        } else {
            viewPager2.setCurrentItem(lastPage);  // 恢复阅读进度
        }
    }

    public List<String> stringToStringArray(String src, int length) {
        if (null == src || src.isEmpty()) {
            return null;
        }
        if (length <= 0) {
            return null;
        }
        List<String> str = new ArrayList<>();
        String[] split = src.split("");
        StringBuilder temp = new StringBuilder();
        for (String s : split) {
            temp.append(s);
            if (temp.length() == length) {
                str.add(temp.toString());
                temp = new StringBuilder();
            }
        }
        if (!temp.toString().isEmpty()) {
            str.add(temp.toString());
        }
        return str;
    }
}

package com.example.read;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.read.BookFetcher.BookItem;
import com.example.read.networkBook.DataBase;
import com.example.read.BookFetcher.FBookAdapter;
import com.example.read.networkBook.DetailActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText; // 搜索框
    private RecyclerView recyclerView; // RecyclerView 显示搜索结果
    private FBookAdapter bookAdapter; // 适配器
    private DataBase dataBase; // 数据库实例
    private Button searchButton; // 搜索按钮
    private List<BookItem> AllbookList;         // 书籍列表
private ImageButton backb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);

        // 初始化视图组件
        searchEditText = findViewById(R.id.searchEditText);  // 搜索框
        recyclerView = findViewById(R.id.bookRankingsRecyclerView);  // RecyclerView
        searchButton = findViewById(R.id.searchButton); // 搜索按钮
        backb=findViewById(R.id.searchBackBtn);
        // 设置返回按钮点击事件
        backb.setOnClickListener(v -> {
          finish();
        });
        // 设置 RecyclerView 布局和适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new FBookAdapter(new ArrayList<BookItem>());


        // 初始化数据库实例
        dataBase = new DataBase(this);
        AllbookList = dataBase.getAllBooks();

        bookAdapter.setListener((view1, position) -> {
            // 获取点击的书籍
            BookItem bookItem = AllbookList.get(position);

            // 创建 Intent，传递书籍数据
            Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
            String imageUrl = bookItem.getImgUrl();
            String pageUrl = getHtml(imageUrl);//()
            intent.putExtra("title", bookItem.getTitle()); // 传递 // 传递整个 BookItem
            intent.putExtra("url",pageUrl); // 传递 // 传递整个 BookItem
            // 启动 DetailActivity
            startActivity(intent);
        });

        recyclerView.setAdapter(bookAdapter);
        // 设置搜索按钮点击事件
        searchButton.setOnClickListener(v -> {
            // 获取输入框中的内容并进行搜索
            String query = searchEditText.getText().toString();
            searchBooks(query);
        });

        // 设置 EditText 文本变化监听器，实现实时搜索
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                searchBooks(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
    private void searchBooks(String query) {
        List<BookItem> booksToShow;
        if (query.isEmpty()) {
            // 如果没有输入任何内容，显示所有书籍
            booksToShow = dataBase.getAllBooks();
        } else {
            // 搜索包含书名的书籍
            booksToShow = dataBase.getBooksByTitle(query); // 需要在数据库中实现此方法
        }
        // 更新书籍列表
        if (booksToShow.isEmpty()) {
            Toast.makeText(this, "没有找到匹配的书籍", Toast.LENGTH_SHORT).show();
        }
        // 更新 AllbookList 和适配器
        AllbookList.clear();  // 清空之前的数据
        AllbookList.addAll(booksToShow);  // 添加新数据
        bookAdapter.updateBookList(booksToShow); // 更新适配器显示搜索结果
    }
    public String getHtml(String imageUrl) {
        String id = imageUrl.split("/\\d+s.jpg")[0];
        return id.replace("http://www.xbiqugu.net/files/article/image/","http://www.xbiqugu.net/");
    }
}
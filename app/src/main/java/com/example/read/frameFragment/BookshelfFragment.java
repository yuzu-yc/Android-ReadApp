package com.example.read.frameFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.read.BookFetcher.BookItem;
import com.example.read.networkBook.DataBase;
import com.example.read.networkBook.DetailActivity;
import com.example.read.BookFetcher.FBookAdapter;
import com.example.read.ImportBooksActivity;
import com.example.read.R;
import com.example.read.SearchActivity;

import java.util.List;

public class BookshelfFragment extends Fragment {

    private DataBase db;  // 数据库帮助类
    private List<BookItem> bookList;         // 书籍列表

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);


        // 初始化数据库帮助类
        db = new DataBase(getContext());
        // 获取数据库中的书籍
        bookList = db.getBooks();
//        // 设置 RecyclerView
//        RecyclerView recentReadingRecyclerView = view.findViewById(R.id.recentReadingRecyclerView);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
//        recentReadingRecyclerView.setLayoutManager(gridLayoutManager);
        // 设置 RecyclerView
        RecyclerView recentReadingRecyclerView = view.findViewById(R.id.recentReadingRecyclerView);
// 使用 LinearLayoutManager 代替 GridLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recentReadingRecyclerView.setLayoutManager(linearLayoutManager);

        // 将书籍数据传递给适配器
        FBookAdapter fbookAdapter = new FBookAdapter(bookList);
        fbookAdapter.setListener((view1, position) -> {
            // 获取点击的书籍
            BookItem bookItem = bookList.get(position);

            // 创建 Intent，传递书籍数据
            Intent intent = new Intent(getContext(), DetailActivity.class);
            String imageUrl = bookItem.getImgUrl();
            String pageUrl = getHtml(imageUrl);//()
            intent.putExtra("title", bookItem.getTitle()); // 传递 // 传递整个 BookItem
            intent.putExtra("url",pageUrl); // 传递 // 传递整个 BookItem
            // 启动 DetailActivity
            startActivity(intent);
        });
        recentReadingRecyclerView.setAdapter(fbookAdapter);



        // 设置搜索按钮的点击事件
        ImageButton searchButton = view.findViewById(R.id.Search);
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        // 设置菜单按钮的点击事件
        ImageButton btnMore = view.findViewById(R.id.btnMore);
        btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_more_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.action_import_books) {
                    // 跳转到导入页面
                    Intent intent = new Intent(getActivity(), ImportBooksActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (itemId == R.id.action_browsing_history) {
                    // 跳转到导入页面
//                    Intent intent = new Intent(getActivity(), ReadingActivity.class);
//                    startActivity(intent);
                    return true;
                }

                return false;
            });

            popupMenu.show();
        });

        return view;

    }

    public String getHtml(String imageUrl) {
        String id = imageUrl.split("/\\d+s.jpg")[0];
        return id.replace("http://www.xbiqugu.net/files/article/image/",
                "http://www.xbiqugu.net/");
    }
//    // 启动 FolioReader 阅读书籍
//    private void startReading(String filePath) {
//        Intent intent = new Intent(getActivity(), ReadingActivity.class);
//        intent.putExtra("book_path", filePath);  // 传递书籍文件路径
//        startActivity(intent);
//    }
}

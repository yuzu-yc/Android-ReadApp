package com.example.read.frameFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.read.BookFetcher.BookFetcher;
import com.example.read.BookFetcher.BookItem;
import com.example.read.networkBook.DetailActivity;
import com.example.read.BookFetcher.FBookAdapter;
import com.example.read.R;

import java.util.ArrayList;
import java.util.List;


public class BsTabFragment extends Fragment {

    private int position;
    private RecyclerView mRecyclerView;
    private FBookAdapter mBookAdapter;
    private List<BookItem> mBookItems = new ArrayList<>();  // 用来存储书籍数据
    private FrameLayout progressContainer;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取传递的参数
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }

        // Inflate fragment layout
        View view = inflater.inflate(R.layout.fragment_bs_tab, container, false);

        // 初始化进度条容器
        progressContainer = view.findViewById(R.id.progress_container1);

        // 设置RecyclerView
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBookAdapter = new FBookAdapter(mBookItems);
        mRecyclerView.setAdapter(mBookAdapter);

        // 设置RecyclerView的点击事件
        mBookAdapter.setListener((view1, position) -> {
            // 根据点击的position获取对应的bookItem
            BookItem bookItem = mBookItems.get(position);
            Intent intent = new Intent(BsTabFragment.this.requireContext(), DetailActivity.class);
            String imageUrl = bookItem.getImgUrl();
            String pageUrl = getHtml(imageUrl);
            intent.putExtra("title", bookItem.getTitle());
            intent.putExtra("url", pageUrl);
            BsTabFragment.this.startActivity(intent);
        });

        // 通过position获取对应的书籍数据
        fetchBooksData(position);

        // 根据位置设置背景颜色
        setBackgroundColor(view);

        return view;
    }

    private void setBackgroundColor(View view) {
        switch (position) {
            case 0:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1));
                break;
            case 1:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1));
                break;
            case 2:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1));
                break;
            case 3:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1));
                break;
            case 4:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1));
                break;
            case 5:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1));
                break;
            case 6:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1));
                break;
        }
    }

    public void fetchBooksData(int position) {
        // 显示进度条
        progressContainer.setVisibility(View.VISIBLE);

        // 创建 BookFetcher 实例并进行数据加载
        BookFetcher bookFetcher = new BookFetcher(requireContext(), position, new BookFetcher.OnBooksFetchedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onBooksFetched(List<BookItem> books, List<String> urls, List<String> titles) {
                // 获取到书籍数据后，更新RecyclerView的数据源并刷新显示
                mBookItems.clear();
                mBookItems.addAll(books);
                mBookAdapter.notifyDataSetChanged();

                // 隐藏进度条
                progressContainer.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                // 出现错误时，显示错误信息
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                // 隐藏进度条
                progressContainer.setVisibility(View.GONE);
            }
        });

        // 开始抓取书籍数据
        bookFetcher.fetchBooks();
    }

    public String getHtml(String imageUrl) {
        String id = imageUrl.split("/\\d+s.jpg")[0];
        return id.replace("http://www.xbiqugu.net/files/article/image/", "http://www.xbiqugu.net/");
    }
}

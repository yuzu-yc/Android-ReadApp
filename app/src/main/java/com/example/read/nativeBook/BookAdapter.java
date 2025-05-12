package com.example.read.nativeBook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.read.OnItemClickListener;
import com.example.read.R;

import java.util.List;

// 适配器类，继承 RecyclerView.Adapter
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final List<Book> bookList;

    // 构造函数，接收书籍列表数据
    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    //定义单条目点击接口对象
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载书籍的单项布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // 获取当前书籍
        Book book = bookList.get(position);

        // 绑定数据到视图
        holder.imageView.setImageResource(book.getImageResId());
        holder.textView.setText(book.getTitle());

        holder.itemView.setOnClickListener(view -> {
            // 使用 holder.getAdapterPosition() 获取点击项的适配器位置
            //通过使用 holder.getAdapterPosition()，即使数据集发生变化，你依然能获取到更新后的正确位置。
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(view, clickedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        // 返回书籍列表的大小
        return bookList.size();
    }

    // ViewHolder 内部类，用于缓存视图
    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            // 初始化视图组件
            imageView = itemView.findViewById(R.id.bookImage);
            textView = itemView.findViewById(R.id.bookTitle);
        }
    }
}
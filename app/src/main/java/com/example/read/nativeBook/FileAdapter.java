package com.example.read.nativeBook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.read.R;

import java.io.File;
import java.util.List;

// 定义适配器类，用于展示文件列表
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<File> fileList;
    private OnItemClickListener listener;

    // 构造方法，初始化文件列表和点击监听器
    public FileAdapter(List<File> fileList, OnItemClickListener listener) {
        this.fileList = fileList;
        this.listener = listener;
    }

    // 创建ViewHolder，用于绑定每个项的视图
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    // 绑定数据到ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File file = fileList.get(position);
        String fileNameWithoutExtension = file.getName();  // 获取文件名（去掉扩展名的部分）
        holder.fileNameTextView.setText(fileNameWithoutExtension);  // 显示文件名
        holder.itemView.setOnClickListener(v -> listener.onItemClick(file, position));
    }

    // 返回文件列表的大小
    @Override
    public int getItemCount() {
        return fileList.size();
    }

    // 定义点击事件的接口
    public interface OnItemClickListener {
        void onItemClick(File file, int position);  // 文件点击时的回调
    }

    // ViewHolder类，用于存储每一项的视图组件
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;  // 文件名TextView

        public ViewHolder(View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);  // 绑定文件名TextView
        }
    }
}

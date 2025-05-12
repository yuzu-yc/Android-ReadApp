package com.example.read.Progress;
import android.content.Context;
import android.content.SharedPreferences;
public class ReadProgressUtil {
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("ReadProgress", Context.MODE_PRIVATE);
    }
    // 保存书籍的进度
    public static void saveProgress(Context context, String bookTitle, int chapter, int page) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        // 拼接章节和页面信息，如 "2_10" 表示第2章第10页
        String progress = chapter + "_" + page;
        editor.putString(bookTitle, progress);  // 用书名作为键，保存拼接的进度
        editor.apply();
    }
    // 获取书籍的进度
    public static int[] getProgress(Context context, String bookTitle) {
        SharedPreferences preferences = getPreferences(context);
        String progress = preferences.getString(bookTitle, null);  // 获取书籍的进度字符串
        if (progress != null) {
            // 解析拼接的进度字符串
            String[] parts = progress.split("_");
            int chapter = Integer.parseInt(parts[0]);  // 获取章节号
            int page = Integer.parseInt(parts[1]);     // 获取页面号
            return new int[]{chapter, page};  // 返回章节和页面
        }
        // 如果没有记录进度，则返回默认值（从第1章第1页开始）
        return new int[]{0, 0};
    }
    // 检查书籍是否已有进度记录
    public static boolean hasProgress(Context context, String bookTitle) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.contains(bookTitle);  // 判断是否有该书籍的记录
    }
}

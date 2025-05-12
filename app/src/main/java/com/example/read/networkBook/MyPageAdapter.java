package com.example.read.networkBook;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * MyPgaerAdapter 是一个继承自 FragmentStateAdapter 的适配器类，
 * 主要用于为 ViewPager2 提供页面切换功能。它通过一个 PagerFragment 列表来管理每一页的内容。
 */
public class MyPageAdapter extends FragmentStateAdapter {

    // 保存 Fragment 列表，用于显示分页内容
    private List<PagerFragment> fragmentList;

    /**
     * 构造方法
     *
     * @param fragmentManager 用于管理 Fragment 的 FragmentManager
     * @param lifecycle       Fragment 的生命周期
     */
    public MyPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);  // 调用父类构造方法，初始化 FragmentManager 和 Lifecycle
    }

    /**
     * 设置适配器的数据源
     *
     * @param fragmentList 包含多个 PagerFragment 的列表
     */
    public void setFragmentList(List<PagerFragment> fragmentList) {
        this.fragmentList = fragmentList;  // 设置 Fragment 列表
    }

    /**
     * 获取适配器的数据源
     *
     * @return 当前的 Fragment 列表
     */
    public List<PagerFragment> getFragmentList() {
        return fragmentList;
    }

    /**
     * 创建并返回指定位置的 Fragment
     *
     * @param position 当前位置
     * @return 对应位置的 PagerFragment
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置返回对应的 Fragment
        return fragmentList.get(position);
    }

    /**
     * 获取适配器的数据源大小，即 ViewPager2 页面的数量
     *
     * @return 返回 Fragment 列表的大小
     */
    @Override
    public int getItemCount() {
        return fragmentList.size();  // 返回 Fragment 列表的长度，表示页面数量
    }
}


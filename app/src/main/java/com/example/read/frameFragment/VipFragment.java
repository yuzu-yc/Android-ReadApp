package com.example.read.frameFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import com.example.read.R;

import java.util.ArrayList;
import java.util.List;

public class VipFragment extends Fragment {

    private HorizontalScrollView vipHorizontalScrollView;
    private RadioGroup vipRadioGroup;
    private ViewPager2 vipViewPager2;
    private float vipCurrentCheckedLeft;
    private final List<Fragment> vFragments = new ArrayList<>();

    private final String[] vTitles = {"阅读会员", "听书会员"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vip, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        vipHorizontalScrollView = view.findViewById(R.id.vipHorizontalScrollView);
        vipRadioGroup = view.findViewById(R.id.vipRadioGroup);
        vipViewPager2 = view.findViewById(R.id.vipViewPager2);

        // 初始化导航栏按钮
        for (int i = 0; i < vTitles.length; i++) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(vTitles[i]);


            radioButton.setButtonDrawable(null); // 去除默认圆点,在按钮左侧
            radioButton.setPadding(20, 15, 20, 15);

            radioButton.setTextSize(16);
            radioButton.setTextSize(i == 0 ? 22: 16); // "推荐"按钮字体放大
            // 设置字体颜色为自定义的颜色选择器
            radioButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.radiobutton_text_color));

            radioButton.setId(i); // 设置ID
            vipRadioGroup.addView(radioButton);
        }

        // 初始化Fragment集合
        for (int i = 0; i < vTitles.length; i++) {

            BsTabFragment fragment = new BsTabFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", vTitles[i]);
            bundle.putInt("position", i);  // 传递位置给每个Fragment
            fragment.setArguments(bundle);
            vFragments.add(fragment);
        }

        // ViewPager2适配器
        vipViewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return vFragments.get(position);
            }

            @Override
            public int getItemCount() {
                return vFragments.size();
            }
        });

        // 设置默认选中第一个
        vipRadioGroup.check(0);
        vipCurrentCheckedLeft = 0;

        // 监听ViewPager2滑动
        vipViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                RadioButton radioButton = (RadioButton) vipRadioGroup.getChildAt(position);
                radioButton.performClick();
            }
        });

        // 监听RadioGroup点击
        vipRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) group.getChildAt(i);

                if (i == group.indexOfChild(group.findViewById(checkedId))) {
                    // 选中时：字体变大
                    radioButton.setTextSize(22); // 可以根据需要调整
                } else {
                    // 未选中时：字体保持默认大小
                    radioButton.setTextSize(16); // 默认字体大小
                }
            }
            RadioButton radioButton = group.findViewById(checkedId);
            int position = group.indexOfChild(radioButton);
            vipViewPager2.setCurrentItem(position, false);  // 设置页面并启用滑动动画

            animateToButton(radioButton);
        });

//
        // 设置页面切换动画（左右并排显示）
        vipViewPager2.setPageTransformer((page, position) -> {

            if (position <-1) { // 页面已经完全离开屏幕
                page.setAlpha(0);
            } else if (position <= 1) {
                // 设置平移和透明度
                page.setTranslationX(position);  // 左右平移
                //page.setAlpha(1 - Math.abs(position));  // 不透明度随着滑动逐渐改变
            } else { // 页面完全在屏幕之外
                page.setAlpha(0);
            }
        });
    }

    // 滚动HorizontalScrollView使选中按钮居中
    private void animateToButton(RadioButton radioButton) {
        float left = radioButton.getLeft();
        vipHorizontalScrollView.smoothScrollTo((int) (left - vipCurrentCheckedLeft), 0);
        vipCurrentCheckedLeft = left;
    }
}

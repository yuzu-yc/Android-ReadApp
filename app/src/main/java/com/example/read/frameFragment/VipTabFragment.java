package com.example.read.frameFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.read.R;


public class VipTabFragment extends Fragment {

    private int position;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取传递的参数
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }

        // Inflate fragment layout,保留用颜色看页面，名称删去。后续也可删
        View view = inflater.inflate(R.layout.fragment_vip_tab, container, false);

        // 根据位置为每个Fragment设置不同的背景颜色
        switch (position) {
            case 0:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1)); // 设置颜色1
                break;
            case 1:
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPage1)); // 设置颜色2
                break;

        }
        return view;
    }
}

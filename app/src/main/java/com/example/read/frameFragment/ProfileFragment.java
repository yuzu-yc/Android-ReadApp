package com.example.read.frameFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.read.R;
import com.example.read.startUi.LoginActivity;

public class ProfileFragment extends Fragment {

    private TextView userNameTextView,quitAPP;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载 ProfileFragment 布局文件
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 获取布局中的 userName TextView
        userNameTextView = view.findViewById(R.id.userName);
        quitAPP=view.findViewById(R.id.quitApp);


        // 获取 SharedPreferences 中保存的用户名
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "默认用户名");
        // 设置用户名到 TextView
        userNameTextView.setText(username);
        // 退出登录按钮的点击事件

        quitAPP.setOnClickListener(v -> {

            // 跳转到登录界面
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();  // 结束当前活动，防止按返回键返回
            // 在上一步后，清除 SharedPreferences 中的用户名
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.apply();
        });


        return view;
    }
}

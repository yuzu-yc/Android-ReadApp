package com.example.read;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.read.frameFragment.BookshelfFragment;
import com.example.read.frameFragment.BookstoreFragment;
import com.example.read.frameFragment.ProfileFragment;
import com.example.read.frameFragment.VipFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 加载默认的 BookshelfFragment
        loadFragment(new BookshelfFragment());

        // 底部导航栏点击事件（假设已设置监听器逻辑，加载对应 Fragment）
        findViewById(R.id.navBookshelf).setOnClickListener(v -> loadFragment(new BookshelfFragment()));
        findViewById(R.id.navBookstore).setOnClickListener(v -> loadFragment(new BookstoreFragment()));
        findViewById(R.id.navVip).setOnClickListener(v -> loadFragment(new VipFragment()));
        findViewById(R.id.navProfile).setOnClickListener(v -> loadFragment(new ProfileFragment()));

     SharedPreferences sharedPreferences = getSharedPreferences("ReadProgress", MODE_PRIVATE);
     SharedPreferences.Editor editor = sharedPreferences.edit();

//清除所有数据
     editor.clear();

////提交更改
//      editor.apply();  // 或者使用 editor.commit()，apply() 是异步操作，commit() 是同步操作


    }

    // 加载 Fragment 的方法
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }


    }


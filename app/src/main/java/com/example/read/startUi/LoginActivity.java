package com.example.read.startUi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.read.R;
import com.example.read.MainActivity;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private AccountDatabase accountDatabase;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化控件
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        // 初始化数据库帮助类
        accountDatabase = new AccountDatabase(this);

        // 检查 SharedPreferences 是否已保存用户名，自动跳过登录
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", null);

        // 确保 savedUsername 存在且用户输入框为空时才跳过登录
        if (savedUsername != null && editTextUsername.getText().toString().isEmpty()) {
            // 如果用户名已保存，直接跳转到主界面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // 处理登录逻辑
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // 检查输入是否为空
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 验证用户名和密码
            SQLiteDatabase db = accountDatabase.getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.query(AccountDatabase.TABLE_USERS,
                        new String[]{AccountDatabase.COLUMN_ID, AccountDatabase.COLUMN_USERNAME, AccountDatabase.COLUMN_PASSWORD},
                        AccountDatabase.COLUMN_USERNAME + "=? AND " + AccountDatabase.COLUMN_PASSWORD + "=?",
                        new String[]{username, password},
                        null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    // 登录成功
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                    // 存储用户名到 SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();

                    // 跳转到主界面
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 登录失败
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        });

        // 点击注册跳转到注册界面
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

}

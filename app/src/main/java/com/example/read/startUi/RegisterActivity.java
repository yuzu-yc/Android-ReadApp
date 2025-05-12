package com.example.read.startUi;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.read.R;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private AccountDatabase accountDatabase;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化控件
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin =findViewById(R.id.textViewLogin);

        // 初始化数据库帮助类
        accountDatabase = new AccountDatabase(this);

        // 处理注册逻辑
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (password.equals(confirmPassword)) {
                // 插入新用户数据到数据库
                SQLiteDatabase db = accountDatabase.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(AccountDatabase.COLUMN_USERNAME, username);
                values.put(AccountDatabase.COLUMN_PASSWORD, password);

                long newRowId = db.insert(AccountDatabase.TABLE_USERS, null, values);

                if (newRowId != -1) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();  // 注册成功后返回到登录界面
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
                }

                db.close();
            } else {
                Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
            }
        });
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}

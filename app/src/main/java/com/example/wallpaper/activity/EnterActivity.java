package com.example.wallpaper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallpaper.MainActivity;
import com.example.wallpaper.R;
import com.example.wallpaper.helper.DBHelper;

public class EnterActivity extends AppCompatActivity {

    private EditText username, password;
    private TextView btnLogin, register;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        username = findViewById(R.id.userName1);
        password = findViewById(R.id.password1);
        register = findViewById(R.id.register_tv);


        btnLogin = findViewById(R.id.btnSignIn1);
        DB = new DBHelper(this);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("")||pass.equals("")){
                    Toast.makeText(EnterActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();

                }else {

                    Boolean checkUserPass = DB.checkUsernameAndPassword(user,pass);
                    if (checkUserPass == true){
                        Toast.makeText(EnterActivity.this, "Sign in Succesfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EnterActivity.this, MainActivity.class));
                    }else {
                        Toast.makeText(EnterActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }

                username.setText("");
                password.setText("");
                finish();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterActivity.this, RegisterActivity.class));
            }
        });


    }
}
package ir.mohsenafshar.android.mychat.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ir.mohsenafshar.android.mychat.R;
import ir.mohsenafshar.android.mychat.mainChat.ChatActivity;
import ir.mohsenafshar.android.mychat.test.TestKotlinActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String USERNAME = "USERNAME";
    private EditText usernameEditText;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.et_username);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
            if(TextUtils.isEmpty(usernameEditText.getText().toString().trim())){
                usernameEditText.setError("This field cannot be blank!");
                return;
            }
            intent.putExtra(USERNAME, usernameEditText.getText().toString());
            startActivity(intent);
        });

        Intent i = new Intent(this, TestKotlinActivity.class);
        startActivity(i);

    }

}

package au.com.softclient.webrtc_screenshare_1.ui;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import au.com.softclient.webrtc_screenshare_1.databinding.ActivityLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        views.enterBtn.setOnClickListener(v -> {
            if (views.usernameEt.getText().toString().isEmpty()) {
                Toast.makeText(this, "please fill the username", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", views.usernameEt.getText().toString());
            startActivity(intent);
        });
    }
}


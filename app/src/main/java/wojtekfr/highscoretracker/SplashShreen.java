package wojtekfr.highscoretracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplashShreen extends AppCompatActivity {
Button okButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_shreen);
        okButton = findViewById(R.id.buttonSplashOk);
    okButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences sharedPreferences = getSharedPreferences("showSplashPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("showSplash", false);
            editor.apply();
            finish();
        }
    });
    }

}
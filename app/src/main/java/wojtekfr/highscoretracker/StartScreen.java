package wojtekfr.highscoretracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

public class StartScreen extends AppCompatActivity {
Button okButton;
        TextView textView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_shreen);
        textView = findViewById(R.id.textViewSplash);
        okButton = findViewById(R.id.buttonSplashOk);

        textView.setMovementMethod(new ScrollingMovementMethod());
    okButton.setOnClickListener(view -> {
        SharedPreferences sharedPreferences = getSharedPreferences("showSplashPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("showSplash", false);
        editor.apply();
        finish();
    });
    }

}
package wojtekfr.highscoretracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.Button;
import android.widget.TextView;

import wojtekfr.highscoretracker.util.ReviewInStore;

public class About extends AppCompatActivity {

    Button okButton;
    TextView textViewReview;
    Context context = this;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textViewReview = findViewById(R.id.textViewAboutReview);


        okButton = findViewById(R.id.buttonAboutOk);

        Spanned formattedAboutReviewText = Html.fromHtml( getString(R.string.aboutReview));
        textViewReview.setText(formattedAboutReviewText);
        textViewReview.setOnClickListener(view -> {
            ReviewInStore reviewInStore = new ReviewInStore();
            reviewInStore.reviewInStore(getPackageName(), context );
        });

        okButton.setOnClickListener(view -> finish());
    }
}





package wojtekfr.highscoretracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//                emailIntent.setType("plain/text");
//                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {"frabidev@gmail.com" });
//                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "feedback for High Score Tracker");
//                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"");
//                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//            }
//        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}





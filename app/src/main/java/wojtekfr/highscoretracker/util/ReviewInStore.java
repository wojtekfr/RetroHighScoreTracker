package wojtekfr.highscoretracker.util;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewInStore extends AppCompatActivity {
    private static final String appURL = "https://play.google.com/store/apps/details?id=";


    public void reviewInStore(String packageName, Context context) {
        {
            try {
                Intent rateIntent = new Intent(Intent.ACTION_VIEW);
                rateIntent.setData(Uri.parse(appURL + packageName));
                rateIntent.setPackage("wojtekfr.highscoretracker");
                context.startActivity(rateIntent);
            } catch (ActivityNotFoundException e) {
                Intent rateIntent = new Intent(Intent.ACTION_VIEW);
                rateIntent.setData(Uri.parse(appURL + packageName));
                context.startActivity(rateIntent);
            }

        }
    }
}





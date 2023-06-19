package wojtekfr.highscoretracker.util;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

public class EventLogger {

    private final FirebaseAnalytics mFirebaseAnalytics;

    public EventLogger(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logEvent(String event){

        mFirebaseAnalytics.logEvent(event, null);
    }
}

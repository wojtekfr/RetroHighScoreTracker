package wojtekfr.highscoretracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.review.ReviewException;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;

import java.util.Objects;

import wojtekfr.highscoretracker.adapter.RecyclerViewAdapter;
import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;
import wojtekfr.highscoretracker.util.CurrentSorting;
import wojtekfr.highscoretracker.util.EventLogger;
import wojtekfr.highscoretracker.util.RequestReview;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnGameClickListener, BottomSheetFragment.ChangeSortingListener {


    ReviewInfo reviewInfo;
    ReviewManager manager;
    private AdView mAdView;

    private static final int NEW_GAME_ACTIVITY_REQUEST_CODE = 1;
    //  private ArrayList<String> gameArrayList;
    FloatingActionButton addGameFloatingButton;
    Button searchButton;

    TextInputEditText textInputSearchCondition;
    Button sortButton;

    private GameViewModel gameViewModel;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    MainActivity mainActivity;
    public static BottomSheetDialogFragment bottomSheetFragment;

    String searchCondition;
    TextInputLayout textInputLayout;
    EditText textInput;
    Dialog dialog;
    TextView textNewGameHelp;
    ImageView arrowImage;

    CurrentSorting currentSorting = CurrentSorting.LASTUPDATE;

    //private FirebaseAnalytics mFirebaseAnalytics;
    EventLogger eventLogger = new EventLogger(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        //ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //showing splash screen only on first run
        SharedPreferences getSharedData = getSharedPreferences("showSplashPref", MODE_PRIVATE);
        boolean showSplashCreen = getSharedData.getBoolean("showSplash", true);
        if (showSplashCreen) {
            Intent intent2 = new Intent(MainActivity.this, SplashShreen.class);
            startActivity(intent2);
        }
        //Log.d("xxx", "startowy control code " + controlCode);

        //setting up UI

        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addGameFloatingButton = findViewById(R.id.floatingButtonAddGame);
        searchButton = findViewById(R.id.buttonSearch);
        textInputSearchCondition = findViewById(R.id.textInputSearchCondition);
        sortButton = findViewById(R.id.buttonSort);
        textInputLayout = findViewById(R.id.textInputLayout);
        textInput = findViewById(R.id.textInputSearchCondition);
        textNewGameHelp = findViewById(R.id.textViewAddFirstGameHelper);
        arrowImage = findViewById(R.id.imageViewFirstGameArrowImageView);
        recyclerView = findViewById(R.id.recyclerViewGamesList);

//
//        final ViewGroup.MarginLayoutParams lpt =
//                (ViewGroup.MarginLayoutParams)addGameFloatingButton.getLayoutParams();
//
//        lpt.setMargins(lpt.leftMargin,lpt.topMargin,lpt.rightMargin,0);
//
//        addGameFloatingButton.setLayoutParams(lpt);

//        ConstraintLayout.LayoutParams rel_btn = new ConstraintLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//

        //rel_btn.bottomMargin = 60;


//

        // addGameFloatingButton.setLayoutParams(rel_btn);


        //gameViewModel setup
        //gameArrayList = new ArrayList<>();
        gameViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
                .create(GameViewModel.class);
        // executes results methods
        gameViewModel.prepareResults();


        // necessary to address gameViewModel from other methods
        mainActivity = this;

        prepareReview();
        refreshSorting();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //setSortingByLastUpdate();
        //Log.d("xxx", "controlcode po domyslnym sortowaniu" + controlCode);
        //setting up bottomSheet for sorting
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();

        //setting dialog box for are you sure (ays)
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_ays);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button okButton = dialog.findViewById(R.id.buttonOkDialog);
        Button cancelButton = dialog.findViewById(R.id.buttonCancelDialog);

        checkIfHelperToBeShowed();


        //onClick listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
                GameViewModel.deleteAll();
                eventLogger.logEvent("MainActivity_ClearDatabase");
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        addGameFloatingButton.setOnClickListener(view -> {
            eventLogger.logEvent("MainActivity_AddGameClicked");

            Intent intent = new Intent(MainActivity.this, AddGame.class);
            startActivityForResult(intent, NEW_GAME_ACTIVITY_REQUEST_CODE);

        });

        searchButton.setOnClickListener(view -> {

            executeSearchByEnteredString();
            hideKeyboard(this);
        });

        textInputSearchCondition.setOnFocusChangeListener((view, b) -> {
            executeSearchByEnteredString();
            hideKeyboard(this);
        });

        sortButton.setOnClickListener(view -> {


            //Log.d("xxx", "control code w bottom listener "+ controlCode);
            eventLogger.logEvent("MainActivity_SortButtonClicked");
            bottomSheetFragment.setCurrentSorting(currentSorting);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        textInputLayout.setEndIconOnClickListener(view -> {
            //Log.d("qqq","yyy");
            eventLogger.logEvent("MainActivity_SearchInputEndIconClicked");
            textInputSearchCondition.setText("");
            executeSearchByEnteredString();
        });
        //Log.d("xxx", "controlcode chwile po domyslnym sortowaniu" + controlCode);
        textInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                executeSearchByEnteredString();
                hideKeyboard(mainActivity);
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkIfHelperToBeShowed();

        SharedPreferences sharedPreferences = getSharedPreferences("askReviewPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        gameViewModel.gameCount.observe(mainActivity, count -> {
            //Log.d("xxx", "licznik " + count);
            if (!sharedPreferences.getBoolean("reviewAlreadyAsked", false)) {
                Log.d("xxx", "jeszcze nie było review");
                if (count == 4) {
                    editor.putBoolean("askReview", true);
                    editor.apply();
                    Log.d("xxx", "oznaczam, że są warunki do review count ==2");
                } else {
                    Log.d("xxx", " nie ma warunków do review cont !=2)");
                }
            } else {
                //Log.d("xxx", "już był review");

            }
        });

        if (sharedPreferences.getBoolean("askReview", false)) {
            // Log.d("xxx", "teraz proszę o review");

            askForReview();
            editor.putBoolean("reviewAlreadyAsked", true);
            editor.putBoolean("askReview", false);
            editor.apply();

        } else {
            //Log.d("xxx", "nie ma powodu prosić o review"

        }

        hideKeyboard(this);
        refreshSorting();

    }

    private void checkIfHelperToBeShowed() {
        gameViewModel.gameCount.observe(mainActivity, count -> {
            if (count != 0) {
                textNewGameHelp.setVisibility(View.GONE);
                textNewGameHelp.clearAnimation();
                arrowImage.clearAnimation();
                arrowImage.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                textInputSearchCondition.setVisibility(View.VISIBLE);
                sortButton.setVisibility(View.VISIBLE);
                textInputLayout.setVisibility(View.VISIBLE);
                textInput.setVisibility(View.VISIBLE);

            } else {
                textNewGameHelp.setVisibility(View.VISIBLE);
                arrowImage.setVisibility(View.VISIBLE);
                Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
                animation.setDuration(1000); //1 second duration for each animation cycle
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
                arrowImage.startAnimation(animation); //to start animation
                textNewGameHelp.startAnimation(animation);
                searchButton.setVisibility(View.INVISIBLE);
                textInputSearchCondition.setVisibility(View.INVISIBLE);
                sortButton.setVisibility(View.INVISIBLE);
                textInputLayout.setVisibility(View.INVISIBLE);
                textInput.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.aboutItem) {
            eventLogger.logEvent("MainActivity_aboutClicked");

            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.clearItem) {
            dialog.show();
        }

        if (id == R.id.instrutionItem) {
            Intent intent = new Intent(MainActivity.this, SplashShreen.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.reviewItem || id == R.id.rateStar) {
            ReviewInStore reviewInStore = new ReviewInStore();
        reviewInStore.reviewInStore(getPackageName(),this);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onGameClick(int position) {

        eventLogger.logEvent("MainActivity_GameClicked");

        Game game = null;
        switch (currentSorting) {
            case ADDINGDATE:
                game = Objects.requireNonNull(gameViewModel.getAllGames().getValue().get(position));
                break;
            case ENTEREDSTRING:
                game = Objects.requireNonNull(gameViewModel.getFilteredGames().getValue().get(position));
                break;
            case ALPHABET:
                game = Objects.requireNonNull(gameViewModel.getAllGamesSortedByAlphabetGames().getValue().get(position));
                break;
            case LASTUPDATE:
                game = Objects.requireNonNull(gameViewModel.getAllGamesSortedByLastUpdate().getValue().get(position));
        }

        Intent intent = new Intent(MainActivity.this, AddGame.class);
        intent.putExtra("id", game.getId());
        eventLogger.logEvent("MainActivity_GameClicked");
        startActivity(intent);
    }

    public void applySorting(int selectedSortingOption) {

        switch (selectedSortingOption) {
            case R.id.radioButtonNoSort:
                setSortingByAddingDate();
                break;
            case R.id.radioButtonSortByName:
                setSortingByAlphabet();
                break;
            case R.id.radioButtonSortByLastUpdate:
                setSortingByLastUpdate();
                break;
        }
        eventLogger.logEvent("MainActivity_SortingApplied");
    }


    //sorting methods

    private void executeSearchByEnteredString() {
        //Log.d("xxx", "ustawiam executeSearchByEnteredString");
        //Log.d("xxx", "seach condition " + textInputSearchCondition.getText().toString().trim());
        if (!textInputSearchCondition.getText().toString().trim().isEmpty()) {
            //Log.d("xxx", "nie jest puste");
            searchCondition = "%" + textInputSearchCondition.getText().toString().trim() + "%";
            gameViewModel.setSearchCondition(searchCondition);
            gameViewModel.prepareResults();
            gameViewModel.getFilteredGames().observe(mainActivity, games -> {
                //Log.d("xxx", "wykonuje  getFilteredGames");
                recyclerViewAdapter = new RecyclerViewAdapter(games,
                        MainActivity.this, mainActivity);
                recyclerView.setAdapter(recyclerViewAdapter);
            });
            currentSorting = currentSorting.ENTEREDSTRING;
            eventLogger.logEvent("MainActivity_SearchByStringExecuted");
        } else {
            //Log.d("xxx", "resetuje do domyslneg sortowania czyli 3");
            setSortingByLastUpdate();
        }
    }

    public void setSortingByLastUpdate() {
        // Log.d("xxx", "wykonujesortingbylastupdate");
        gameViewModel.getAllGamesSortedByLastUpdate().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            MainActivity.this, mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");

                    //Log.d("xxx", "control code  zaraz po dom srt " + controlCode);
                });

        currentSorting = currentSorting.LASTUPDATE;
    }

    public void setSortingByAddingDate() {
        //   Log.d("xxx", "ustawiam setSortingByAddingDate");

        gameViewModel.getAllGames().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            MainActivity.this, mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");

                });

        currentSorting = currentSorting.ADDINGDATE;
    }

    public void setSortingByAlphabet() {
        //  Log.d("xxx", "ustawiam setSortingByAlphabet");

        gameViewModel.getAllGamesSortedByAlphabetGames().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            MainActivity.this, mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");

                });

        currentSorting = currentSorting.ALPHABET;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log.d("xxqq", " resume");
        refreshSorting();
        checkIfHelperToBeShowed();
        hideKeyboard(this);
    }

    private void refreshSorting() {
        // Log.d("xxqq", "sorting refreshed");
        gameViewModel.prepareResults();
        switch (currentSorting) {
            case ADDINGDATE:
                //   Log.d("xxqq", "refresh control code " + controlCode + " byaddingdate");
                setSortingByAddingDate();
                break;
            case ENTEREDSTRING:
                // Log.d("xxqq", "refresh control code " + controlCode + " byeneredstring");
                //Log.d("xxqq", "searchCondition " + searchCondition);
                executeSearchByEnteredString();
                break;
            case ALPHABET:
                //Log.d("xxqq", "refresh control code " + controlCode + " byAlphabet");
                setSortingByAlphabet();
                break;
            case LASTUPDATE:
                //Log.d("xxqq", "refresh control code " + controlCode + " bylastupdate");
                setSortingByLastUpdate();
                break;
        }


        //   Log.d("xxxqq", "size " + gameViewModel.gameCount.getValue());
    }

    public void prepareReview() {

        manager = ReviewManagerFactory.create(this);

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
                //Toast.makeText(this," review przygotowany", Toast.LENGTH_LONG).show();
            } else {
                // There was some problem, log or handle the error code.
                @ReviewErrorCode int reviewErrorCode = ((ReviewException) task.getException()).getErrorCode();
                //Toast.makeText(this,"coś nie tak z przygotowaniem review", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void askForReview() {
        if (reviewInfo != null) {
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(task -> {
                //Toast.makeText(this," review poszedł", Toast.LENGTH_LONG).show();
                // The flow has finished. The API does not indicate whether the user
                // reviewed or not, or even whether the review dialog was shown. Thus, no
                // matter the result, we continue our app flow.
                eventLogger.logEvent("MainActivity_AskForReviewDone");
            });
        }
    }

//
//    public void reviewInStore() {
//        try {
//            Intent rateIntent = new Intent(Intent.ACTION_VIEW);
//            rateIntent.setData(Uri.parse(appURL + getPackageName()));
//            rateIntent.setPackage("wojtekfr.highscoretracker");
//            startActivity(rateIntent);
//        } catch (ActivityNotFoundException e) {
//            Intent rateIntent = new Intent(Intent.ACTION_VIEW);
//            rateIntent.setData(Uri.parse(appURL + getPackageName()));
//            startActivity(rateIntent);
//        }

//    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}

package wojtekfr.highscoretracker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.util.Objects;

import wojtekfr.highscoretracker.adapter.RecyclerViewAdapter;
import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;
import wojtekfr.highscoretracker.util.CurrentSorting;
import wojtekfr.highscoretracker.util.EventLogger;
import wojtekfr.highscoretracker.util.ReviewInStore;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnGameClickListener, BottomSheetFragment.ChangeSortingListener {
    ReviewInfo reviewInfo;
    ReviewManager manager;
    // private static final int NEW_GAME_ACTIVITY_REQUEST_CODE = 1;
    FloatingActionButton addGameFloatingButton;
    Button searchButton;
    TextInputEditText textInputSearchCondition;
    Button sortButton;
    private GameViewModel gameViewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    MainActivity mainActivity;
    String searchCondition;
    TextInputLayout textInputLayout;
    Dialog dialog;
    TextView textNewGameHelp;
    ImageView arrowImage;
    CurrentSorting currentSorting = CurrentSorting.LAST_UPDATE;
    EventLogger eventLogger = new EventLogger(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAdds();
        checkIfSplashScreenToBeShowed();
        setUpDeleteDialogConfirmation();

        //setting up UI
        addGameFloatingButton = findViewById(R.id.floatingButtonAddGame);
        searchButton = findViewById(R.id.buttonSearch);
        textInputSearchCondition = findViewById(R.id.textInputSearchCondition);
        sortButton = findViewById(R.id.buttonSort);
        textInputLayout = findViewById(R.id.textInputLayout);
        //   textInput = findViewById(R.id.textInputSearchCondition);
        textNewGameHelp = findViewById(R.id.textViewAddFirstGameHelper);
        arrowImage = findViewById(R.id.imageViewFirstGameArrowImageView);
        Button okButton = dialog.findViewById(R.id.buttonOkDialog);
        Button cancelButton = dialog.findViewById(R.id.buttonCancelDialog);

        recyclerView = findViewById(R.id.recyclerViewGamesList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();

        gameViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
                .create(GameViewModel.class);
        gameViewModel.prepareResults();

        // necessary to address gameViewModel from other methods
        mainActivity = this;

        prepareReview();
        refreshSorting();
        checkIfHelperToBeShowed();

        //what happen after each return from addGame
        ActivityResultLauncher<Intent> addGameResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    checkIfHelperToBeShowed();

                    //asking for review
                    // first check if number of games added exceeded and register it in shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("askReviewPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    gameViewModel.gameCount.observe(mainActivity, count -> {
                        if (!sharedPreferences.getBoolean("reviewAlreadyAsked", false)) {
                            if (count == 4) {
                                editor.putBoolean("askReview", true);
                                editor.apply();
                            }
                        }
                    });
                    // second check shared preferences and launch ask for review if condition met
                    if (sharedPreferences.getBoolean("askReview", false)) {
                        askForReview();
                        editor.putBoolean("reviewAlreadyAsked", true);
                        editor.putBoolean("askReview", false);
                        editor.apply();
                    }

                    hideKeyboard(mainActivity);
                    refreshSorting();
                });


        //onClick listeners
        okButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
            GameViewModel.deleteAll();
            eventLogger.logEvent("MainActivity_ClearDatabase");
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        addGameFloatingButton.setOnClickListener(view -> {
            eventLogger.logEvent("MainActivity_AddGameClicked");

            Intent intent = new Intent(this, AddGame.class);
            //startActivityForResult(intent, NEW_GAME_ACTIVITY_REQUEST_CODE);
            addGameResultLauncher.launch(intent);


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
            eventLogger.logEvent("MainActivity_SortButtonClicked");
            bottomSheetFragment.setCurrentSorting(currentSorting);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        textInputLayout.setEndIconOnClickListener(view -> {
            eventLogger.logEvent("MainActivity_SearchInputEndIconClicked");
            textInputSearchCondition.setText("");
            executeSearchByEnteredString();
        });

        textInputSearchCondition.setOnEditorActionListener((textView, i, keyEvent) -> {
            executeSearchByEnteredString();
            hideKeyboard(mainActivity);
            return false;
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.aboutItem) {
            eventLogger.logEvent("MainActivity_aboutClicked");
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.clearItem) {
            dialog.show();
        }

        if (id == R.id.instructionItem) {
            eventLogger.logEvent("MainActivity_instructionClicked");
            Intent intent = new Intent(MainActivity.this, StartScreen.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.reviewItem || id == R.id.rateStar) {
            ReviewInStore reviewInStore = new ReviewInStore();
            reviewInStore.reviewInStore(getPackageName(), this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGameClick(int position) {
        eventLogger.logEvent("MainActivity_GameClicked");
        Game game = null;
        switch (currentSorting) {
            case ADDING_DATE:
                game = Objects.requireNonNull(Objects.requireNonNull(gameViewModel.getAllGames().getValue()).get(position));
                break;
            case ENTERED_STRING:
                game = Objects.requireNonNull(Objects.requireNonNull(gameViewModel.getFilteredGames().getValue()).get(position));
                break;
            case ALPHABET:
                game = Objects.requireNonNull(Objects.requireNonNull(gameViewModel.getAllGamesSortedByAlphabetGames().getValue()).get(position));
                break;
            case LAST_UPDATE:
                game = Objects.requireNonNull(Objects.requireNonNull(gameViewModel.getAllGamesSortedByLastUpdate().getValue()).get(position));
        }

        Intent intent = new Intent(MainActivity.this, AddGame.class);
        intent.putExtra("id", game.getId());
        eventLogger.logEvent("MainActivity_GameClicked");
        startActivity(intent);
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
                textInputSearchCondition.setVisibility(View.VISIBLE);
            } else {
                textNewGameHelp.setVisibility(View.VISIBLE);
                arrowImage.setVisibility(View.VISIBLE);
                Animation animation = new AlphaAnimation(1, 0);
                animation.setDuration(1000);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE);
                animation.setRepeatMode(Animation.REVERSE);
                arrowImage.startAnimation(animation);
                textNewGameHelp.startAnimation(animation);
                searchButton.setVisibility(View.INVISIBLE);
                textInputSearchCondition.setVisibility(View.INVISIBLE);
                sortButton.setVisibility(View.INVISIBLE);
                textInputLayout.setVisibility(View.INVISIBLE);
                textInputSearchCondition.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void applySorting(int selectedSortingOption) {
        if (selectedSortingOption == R.id.radioButtonNoSort) {
            setSortingByAddingDate();
        } else if (selectedSortingOption == R.id.radioButtonSortByName) {
            setSortingByAlphabet();
        } else if (selectedSortingOption == R.id.radioButtonSortByLastUpdate) {
            setSortingByLastUpdate();
        }
        eventLogger.logEvent("MainActivity_SortingApplied");
    }

    //sorting methods
    private void executeSearchByEnteredString() {
        if (!Objects.requireNonNull(textInputSearchCondition.getText()).toString().trim().isEmpty()) {
            searchCondition = "%" + textInputSearchCondition.getText().toString().trim() + "%";
            gameViewModel.setSearchCondition(searchCondition);
            gameViewModel.prepareResults();
            gameViewModel.getFilteredGames().observe(mainActivity, games -> {
                recyclerViewAdapter = new RecyclerViewAdapter(games,
                        mainActivity);
                recyclerView.setAdapter(recyclerViewAdapter);
            });
            currentSorting = CurrentSorting.ENTERED_STRING;
            eventLogger.logEvent("MainActivity_SearchByStringExecuted");
        } else {
            setSortingByLastUpdate();
        }
    }

    public void setSortingByLastUpdate() {
        gameViewModel.getAllGamesSortedByLastUpdate().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");
                });

        currentSorting = CurrentSorting.LAST_UPDATE;
    }

    public void setSortingByAddingDate() {
        gameViewModel.getAllGames().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");
                });
        currentSorting = CurrentSorting.ADDING_DATE;
    }

    public void setSortingByAlphabet() {
        gameViewModel.getAllGamesSortedByAlphabetGames().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");
                });
        currentSorting = CurrentSorting.ALPHABET;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSorting();
        checkIfHelperToBeShowed();
        hideKeyboard(this);
    }

    private void refreshSorting() {
        gameViewModel.prepareResults();
        switch (currentSorting) {
            case ADDING_DATE:
                setSortingByAddingDate();
                break;
            case ENTERED_STRING:
                executeSearchByEnteredString();
                break;
            case ALPHABET:
                setSortingByAlphabet();
                break;
            case LAST_UPDATE:
                setSortingByLastUpdate();
                break;
        }
    }

    public void prepareReview() {
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            }
        });
    }

    public void askForReview() {
        if (reviewInfo != null) {
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(task -> eventLogger.logEvent("MainActivity_AskForReviewDone"));
        }
    }

    private void setUpDeleteDialogConfirmation() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_ays);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    private void checkIfSplashScreenToBeShowed() {
        SharedPreferences getSharedData = getSharedPreferences("showSplashPref", MODE_PRIVATE);
        boolean showSplashScreen = getSharedData.getBoolean("showSplash", true);
        if (showSplashScreen) {
            Intent intent2 = new Intent(MainActivity.this, StartScreen.class);
            startActivity(intent2);
        }
    }

    private void initializeAdds() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

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

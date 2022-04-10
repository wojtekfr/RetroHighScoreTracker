package wojtekfr.highscoretracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

import wojtekfr.highscoretracker.adapter.RecyclerViewAdapter;
import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;

// git test
public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnGameClickListener {

    private static final int NEW_GAME_ACTIVITY_REQUEST_CODE = 1;
    private ArrayList<String> gameArrayList;
    FloatingActionButton addGameFloatingButton;
    Button searchButton;
    Button resetSearchButton;

    TextInputEditText textInputSearchCondition;
    Button sortButton;
    private GameViewModel gameViewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    MainActivity mainActivity;
    public static BottomSheetFragment bottomSheetFragment;
    Button button;
    int controlCode = 0;
    String searchCondition;
    TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = findViewById(R.id.bottomSheet);

        recyclerView = findViewById(R.id.recyclerViewGamesList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        addGameFloatingButton = findViewById(R.id.floatingButtonAddGame);
        searchButton = findViewById(R.id.buttonSearch);

        textInputSearchCondition = findViewById(R.id.textInputSearchCondition);
        resetSearchButton = findViewById(R.id.buttonSearchReset);
        sortButton = findViewById(R.id.buttonSort);
        textInputLayout = findViewById(R.id.textInputLayout);

        gameArrayList = new ArrayList<>();
        gameViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
                .create(GameViewModel.class);
        // executes results methods
        gameViewModel.prepareResults();

        // necessary to address gameViewModel from other methods
        mainActivity = this;


        setSortingByLastUpdate();


        //onClick listeners
        addGameFloatingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddGame.class);
            startActivityForResult(intent, NEW_GAME_ACTIVITY_REQUEST_CODE);
        });

        searchButton.setOnClickListener(view -> {
            executeSearch();
            hideKeyboard(this);
        });


        //editTextSearchCondition.setOnFocusChangeListener((view, b) -> executeSearch());
        textInputSearchCondition.setOnFocusChangeListener((view, b) -> executeSearch());
        resetSearchButton.setOnClickListener(view -> setSortingByLastUpdate());



        sortButton.setOnClickListener(view -> {
            if (controlCode == 2) {
                setSortingByLastUpdate();
            } else {
                gameViewModel.getAllGamesSortedByAlphabetGames().observe(
                        mainActivity, games -> {
                            recyclerViewAdapter = new RecyclerViewAdapter(games,
                                    MainActivity.this, mainActivity);
                            recyclerView.setAdapter(recyclerViewAdapter);
                            textInputSearchCondition.setText("");
                            controlCode = 2;
                        });
            }
        });

        textInputLayout.setEndIconOnClickListener(view -> {
            textInputSearchCondition.setText("");
            executeSearch();
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
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void executeSearch() {
        //searchCondition = "%" + editTextSearchCondition.getText().toString().trim() + "%";
        searchCondition = "%" + textInputSearchCondition.getText().toString().trim() + "%";
        gameViewModel.setSearchCondition(searchCondition);
        gameViewModel.prepareResults();
        gameViewModel.getFilteredGames().observe(mainActivity, games -> {
            recyclerViewAdapter = new RecyclerViewAdapter(games,
                    MainActivity.this, mainActivity);
            recyclerView.setAdapter(recyclerViewAdapter);
        });
        controlCode = 1;
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null) {
//            if (data.hasExtra("gameName")) {
//                Date lastUpdate = new Date(data.getStringExtra("lastUpdate"));
//                Game game = new Game(data.getStringExtra("gameName"),
//                        data.getIntExtra("score", 0),
//                        data.getStringExtra("note"),
//                        lastUpdate, Converters.convertByteArray2Image(data.getByteArrayExtra("image")));
//                Log.d("xxx", "a " + game.getGameName());
//                GameViewModel.insert(game);
//            }
//        }
//    }

    @Override
    public void onGameClick(int position) {

//        bottomSheetFragment.setPosition(position);
//        bottomSheetFragment.setControlCode(controlCode);
//        bottomSheetFragment.setSearchCondition(searchCondition);
//        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
//
        Game game = null;
        Log.d("xxx", "control code: "+ controlCode);
        if (controlCode == 0) {
            game = Objects.requireNonNull(gameViewModel.allGames.getValue().get(position));
        } else if (controlCode == 1) {
            game = Objects.requireNonNull(gameViewModel.filteredGames.getValue().get(position));
        } else if (controlCode == 2) {
            game = Objects.requireNonNull(gameViewModel.getAllGamesSortedByAlphabetGames().getValue().get(position));
        } else if (controlCode == 3) {
            game = Objects.requireNonNull(gameViewModel.getAllGamesSortedByLastUpdate().getValue().get(position));
        }
        Intent intent = new Intent(MainActivity.this, AddGame.class);
        intent.putExtra("id", game.getId());
        startActivity(intent);
    }

    public void setSortingByLastUpdate() {
        gameViewModel.getAllGamesSortedByLastUpdate().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            MainActivity.this, mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");
                    controlCode = 3;
                });
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

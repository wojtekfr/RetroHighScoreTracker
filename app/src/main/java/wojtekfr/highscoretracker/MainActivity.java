package wojtekfr.highscoretracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

import wojtekfr.highscoretracker.adapter.RecyclerViewAdapter;
import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;
import wojtekfr.highscoretracker.model.MojTekst;
import wojtekfr.highscoretracker.model.SharedModel;

// git test
public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnGameClickListener, BottomSheetFragment.ChangeSortingListener {


    private static final int NEW_GAME_ACTIVITY_REQUEST_CODE = 1;
    private ArrayList<String> gameArrayList;
    FloatingActionButton addGameFloatingButton;
    Button searchButton;
    Button resetSearchButton;

    TextInputEditText textInputSearchCondition;
    Button sortButton;
    private GameViewModel gameViewModel;
    private SharedModel sharedModel;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    MainActivity mainActivity;
    public static BottomSheetDialogFragment bottomSheetFragment;
    Button button;
    int controlCode = 0;
    String searchCondition;
    TextInputLayout textInputLayout;
    Dialog dialog;

    @Override
    public void applySorting(int selectedSortingOption) {
        Log.d("xxx", "applysortinglistener " + selectedSortingOption);
        switch (sharedModel.getSelectedSorting().getValue()) {
            case R.id.radioButtonNoSort:
                Log.d("xxx", "radio no sort");
                setSortingByAddingDate();
                break;
            case R.id.radioButtonSortByName:
                Log.d("xxx", "radio sort by name");
                setSortingByAlphabet();
                break;
            case R.id.radioButtonSortByLastUpdate:
                Log.d("xxx", "radio sort by last update");
                setSortingByLastUpdate();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("xxx", "resume");
    }

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
        sortButton = findViewById(R.id.buttonSort);
        textInputLayout = findViewById(R.id.textInputLayout);

        gameArrayList = new ArrayList<>();
        gameViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
                .create(GameViewModel.class);

        sharedModel = new ViewModelProvider(this).get(SharedModel.class);


        // executes results methods
        gameViewModel.prepareResults();

        // necessary to address gameViewModel from other methods
        mainActivity = this;

        //setting dialog box for are you sure (ays)
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_ays);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button okButton = dialog.findViewById(R.id.buttonOkDialog);
        Button cancelButton = dialog.findViewById(R.id.buttonCancelDialog);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
                GameViewModel.deleteAll();
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
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
        //resetSearchButton.setOnClickListener(view -> setSortingByLastUpdate());



        sortButton.setOnClickListener(view -> {
          Game game = new Game("aaa",11,"bb",null);
          sharedModel.setSelectedGame(game);
          MojTekst mojTekstDoUstawienia = new MojTekst("poczatkowe srutututu");
          sharedModel.setMojTekst(mojTekstDoUstawienia);
          String mojString = "poczatkowy string";



//            bottomSheetFragment.setPosition(position);
//            bottomSheetFragment.setControlCode(controlCode);
//            bottomSheetFragment.setSearchCondition(searchCondition);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());



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
        if (id == R.id.clearItem) {
            dialog.show();

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


//
        Game game = null;

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

    public void setSortingByAddingDate() {
        gameViewModel.getAllGames().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            MainActivity.this, mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");
                    controlCode = 0;
                });
    }

    public void setSortingByAlphabet() {
        gameViewModel.getAllGamesSortedByAlphabetGames().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            MainActivity.this, mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");
                    controlCode = 2;
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

    public static void test()
    {

    };


}

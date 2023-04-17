package wojtekfr.highscoretracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnGameClickListener, BottomSheetFragment.ChangeSortingListener {

    private  int controlCode;
    private static final int NEW_GAME_ACTIVITY_REQUEST_CODE = 1;
    private ArrayList<String> gameArrayList;
    FloatingActionButton addGameFloatingButton;
    Button searchButton;

    TextInputEditText textInputSearchCondition;
    Button sortButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//Log.d("xxx", "wrociłem do main i control code " + controlCode);
      ///  gameViewModel.prepareResults();
//        setSortingByLastUpdate();
    }

    private GameViewModel gameViewModel;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    MainActivity mainActivity;
    public static BottomSheetDialogFragment bottomSheetFragment;

    String searchCondition;
    TextInputLayout textInputLayout;
    Dialog dialog;

    @Override
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences getSharedData = getSharedPreferences("showSplashPref", MODE_PRIVATE);
        boolean showSplashCreen = getSharedData.getBoolean("showSplash",true);
        //Log.d("xxx", "show splash" + showSplashCreen);
        if (showSplashCreen) {
            Intent intent2 = new Intent(MainActivity.this, SplashShreen.class);
            startActivity(intent2);
        }
        //Log.d("xxx", "startowy control code " + controlCode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




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




        // executes results methods
        gameViewModel.prepareResults();

        // necessary to address gameViewModel from other methods
        mainActivity = this;

        setSortingByLastUpdate();
        //Log.d("xxx", "controlcode po domyslnym sortowaniu" + controlCode);
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();

        //setting dialog box for are you sure (ays)
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_ays);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button okButton = dialog.findViewById(R.id.buttonOkDialog);
        Button cancelButton = dialog.findViewById(R.id.buttonCancelDialog);



        //onClick listeners
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

        addGameFloatingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddGame.class);
            startActivityForResult(intent, NEW_GAME_ACTIVITY_REQUEST_CODE);

        });

        searchButton.setOnClickListener(view -> {
            executeSearchByEnteredString();
            hideKeyboard(this);
        });

        textInputSearchCondition.setOnFocusChangeListener((view, b) -> executeSearchByEnteredString());

        sortButton.setOnClickListener(view -> {
            //Log.d("xxx", "control code w bottom listener "+ controlCode);
            bottomSheetFragment.setControlCode(controlCode);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());


        });

        textInputLayout.setEndIconOnClickListener(view -> {
            //Log.d("qqq","yyy");
            textInputSearchCondition.setText("");
            executeSearchByEnteredString();
        });
        //Log.d("xxx", "controlcode chwile po domyslnym sortowaniu" + controlCode);
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
            Intent intent = new Intent(MainActivity.this, SplashShreen.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.clearItem) {
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onGameClick(int position) {
        //Log.d("xxx","control code = " + controlCode);
        //Log.d("xxx", "id klieknietej gry to = " + position);

        Game game = null;
        if (controlCode == 0) {
            //Log.d("xxx", " wielkość " + Objects.requireNonNull(gameViewModel.getAllGames().getValue().size()));
            game = Objects.requireNonNull(gameViewModel.getAllGames().getValue().get(position));
        } else if (controlCode == 1) {
            //Log.d("xxx", " wielkość " + Objects.requireNonNull(gameViewModel.getFilteredGames().getValue().size()));
            game = Objects.requireNonNull(gameViewModel.getFilteredGames().getValue().get(position));
        } else if (controlCode == 2) {
            //Log.d("xxx", " wielkość " + Objects.requireNonNull(gameViewModel.getAllGamesSortedByAlphabetGames().getValue().size()));
            game = Objects.requireNonNull(gameViewModel.getAllGamesSortedByAlphabetGames().getValue().get(position));
        } else if (controlCode == 3) {
            //Log.d("xxx", " wielkość " + Objects.requireNonNull(gameViewModel.getAllGamesSortedByLastUpdate().getValue().size()));
           // game = Objects.requireNonNull(Objects.requireNonNull(gameViewModel.getAllGamesSortedByLastUpdate().getValue()).get(position));
            game = Objects.requireNonNull(gameViewModel.getAllGamesSortedByLastUpdate().getValue().get(position));
        }
        Intent intent = new Intent(MainActivity.this, AddGame.class);
        intent.putExtra("id", game.getId());
        startActivity(intent);
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
                controlCode = 1;
            });
        } else {
            //Log.d("xxx", "resetuje do domyslneg sortowania czyli 3");
           setSortingByLastUpdate();
        }
    }

    public void setSortingByLastUpdate() {

        gameViewModel.getAllGamesSortedByLastUpdate().observe(

                mainActivity, games -> {

                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            MainActivity.this, mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    textInputSearchCondition.setText("");

                    //Log.d("xxx", "control code  zaraz po dom srt " + controlCode);
                });
        controlCode = 3;
    }

    public void setSortingByAddingDate() {
        //Log.d("xxx", "ustawiam setSortingByAddingDate");

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
        //Log.d("xxx", "ustawiam setSortingByAlphabet");

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


}

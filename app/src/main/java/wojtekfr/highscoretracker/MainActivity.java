package wojtekfr.highscoretracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import wojtekfr.highscoretracker.adapter.RecyclerViewAdapter;
import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnGameClickListener {

    private static final int NEW_GAME_ACTIVITY_REQUEST_CODE = 1;
    private ArrayList<String> gameArrayList;
    Button addGameButton;
    Button searchButton;
    Button resetSearchButton;
    EditText editTextSearchCondition;
    private GameViewModel gameViewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewGamesList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addGameButton = findViewById(R.id.buttonAddGame);
        searchButton = findViewById(R.id.buttonSearch);
        editTextSearchCondition = findViewById(R.id.editTextSearchCondition);
        resetSearchButton = findViewById(R.id.buttonSearchReset);

        gameArrayList = new ArrayList<>();
        gameViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
                .create(GameViewModel.class);
        // executes results methods
        gameViewModel.prepareResults();

        // necessary to address gameViewModel from other methods
        mainActivity = this;

        gameViewModel.getAllGames().observe(this, games -> {
            recyclerViewAdapter = new RecyclerViewAdapter(games,
                    MainActivity.this, this);
            recyclerView.setAdapter(recyclerViewAdapter);
        });


        //onClick listeners
        addGameButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddGame.class);
            startActivityForResult(intent, NEW_GAME_ACTIVITY_REQUEST_CODE);
        });

        searchButton.setOnClickListener(view -> executeSearch());


        editTextSearchCondition.setOnFocusChangeListener((view, b) -> executeSearch());

        resetSearchButton.setOnClickListener(view -> gameViewModel.getAllGames().observe(
                mainActivity, games -> {
                    recyclerViewAdapter = new RecyclerViewAdapter(games,
                            MainActivity.this, mainActivity);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    editTextSearchCondition.setText("");
                }));
    }

    private void executeSearch() {
        String searchCondition = "%" + editTextSearchCondition.getText().toString().trim() + "%";
        gameViewModel.setSearchCondition(searchCondition);
        gameViewModel.prepareResults();
        gameViewModel.getFilteredGames().observe(mainActivity, games -> {
            recyclerViewAdapter = new RecyclerViewAdapter(games,
                    MainActivity.this, mainActivity);
            recyclerView.setAdapter(recyclerViewAdapter);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.hasExtra("gameName")) {
                Date lastUpdate = new Date(data.getStringExtra("lastUpdate"));
                Game game = new Game(data.getStringExtra("gameName"),
                        data.getIntExtra("score", 0),
                        data.getStringExtra("note"),
                        lastUpdate);
                Log.d("xxx", "a " + game.getGameName());
                GameViewModel.insert(game);
            }
        }
    }

    @Override
    public void onGameClick(int position) {
        Game game = Objects.requireNonNull(gameViewModel.allGames.getValue().get(position));
        Intent intent = new Intent(MainActivity.this, AddGame.class);
        intent.putExtra("id", game.getId());
        startActivity(intent);
    }
}

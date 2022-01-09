package wojtekfr.highscoretracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;

public class AddGame extends AppCompatActivity {

    EditText gameEditText;
    EditText scoreEditText;
    EditText noteEditText;
    Button saveGameButton;
    Button updateButton;
    Button deleteButton;
    TextView lastUpdateTextView;
    boolean areDataCorrect;


    boolean isEditMode;
    int gameId;

    GameViewModel gameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        isEditMode = false;
        gameEditText = findViewById(R.id.editTextGame);
        scoreEditText = findViewById(R.id.editTextHighScore);
        noteEditText = findViewById(R.id.editTextNotes);
        saveGameButton = findViewById(R.id.buttonSaveGame);
        updateButton = findViewById(R.id.buttonUpdate);
        deleteButton = findViewById(R.id.buttonDelete);
        lastUpdateTextView = findViewById(R.id.textViewLastUpdate);

        gameViewModel = new ViewModelProvider.AndroidViewModelFactory(AddGame.this
                .getApplication()).create(GameViewModel.class);

        if (getIntent().hasExtra("id")) {
            isEditMode = true;
            gameId = getIntent().getIntExtra("id", 1);
            gameViewModel.get(gameId).observe(this, game -> {
                if (game != null) {
                    gameEditText.setText(game.getGameName());
                    scoreEditText.setText(String.valueOf(game.getHighScore()));
                    noteEditText.setText(game.getNote());
                    lastUpdateTextView.setText("last update:" + game.getLastUpdate());
                }
            });
        }

        if (isEditMode) {
            saveGameButton.setVisibility(View.GONE);
        } else {
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            lastUpdateTextView.setVisibility(View.GONE);
        }

        saveGameButton.setOnClickListener(view -> {
            areDataCorrect = checkIfDataCorrect();

            Log.d("xxx", "a " + areDataCorrect);
            if (areDataCorrect) {
                Intent replyIntent = new Intent();
                String gameName = gameEditText.getText().toString().trim();
                int score = Integer.valueOf(scoreEditText.getText().toString().trim());
                String note = noteEditText.getText().toString().trim();
                replyIntent.putExtra("gameName", gameName);

                replyIntent.putExtra("score", score);
                replyIntent.putExtra("note", note);
                replyIntent.putExtra("lastUpdate", new Date(System.currentTimeMillis()).toString());
                setResult(RESULT_OK, replyIntent);
                finish();
            }

        });

        updateButton.setOnClickListener(view -> {
            if (checkIfDataCorrect()) {
                Game game = prepareGameObject();
                GameViewModel.update(game);
                finish();
            }
        });

        deleteButton.setOnClickListener(view -> {
            Game game = prepareGameObject();
            GameViewModel.delete(game);
            finish();
        });
    }

    private Game prepareGameObject() {
        Game game = new Game(gameEditText.getText().toString().trim(),
                Integer.valueOf(scoreEditText.getText().toString().trim()),
                noteEditText.getText().toString().trim(),
                new Date(System.currentTimeMillis()));
        game.setId(gameId = getIntent().getIntExtra("id", 1));
        return game;
    }

    private boolean checkIfDataCorrect() {

        if (gameEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddGame.this, "Enter game title",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Integer.valueOf(scoreEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(AddGame.this, "Score has to be an integer number",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
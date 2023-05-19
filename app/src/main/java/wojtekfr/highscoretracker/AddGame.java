package wojtekfr.highscoretracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;

import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;
import wojtekfr.highscoretracker.util.Converters;
import wojtekfr.highscoretracker.util.EventLogger;

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
    Button takePhotoButton;
    ImageView imageView;
    Bitmap bmpImage;
    final int CAMERA_INTENT = 51;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    EventLogger eventLogger = new EventLogger(this);

//    private FirebaseAnalytics mFirebaseAnalytics;

//    public void setBottomSheetFragment(BottomSheetFragment bottomSheetFragment) {
//        this.bottomSheetFragment = bottomSheetFragment;
//    }

    //BottomSheetFragment bottomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        isEditMode = false;
        gameEditText = findViewById(R.id.editTextGame);
        scoreEditText = findViewById(R.id.editTextHighScore);
        noteEditText = findViewById(R.id.editTextNotes);
        saveGameButton = findViewById(R.id.buttonSaveGame);
        updateButton = findViewById(R.id.buttonUpdate);
        deleteButton = findViewById(R.id.buttonDelete);
        lastUpdateTextView = findViewById(R.id.textViewLastUpdate);
        takePhotoButton = findViewById(R.id.buttonTakePhoto);
        imageView = findViewById(R.id.imageViewPhoto);
        bmpImage = null;


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

                    if (game.getImage() != null) {
                        imageView.setVisibility(View.VISIBLE);
                        bmpImage = game.getImage();
                        imageView.setImageBitmap(bmpImage);
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                }
            });
        }

        if (isEditMode) {
            saveGameButton.setVisibility(View.GONE);
        } else {
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            lastUpdateTextView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }

        saveGameButton.setOnClickListener(view -> {
            areDataCorrect = checkIfDataCorrect();


            if (areDataCorrect) {

                //   Intent replyIntent = new Intent();
                String gameName = gameEditText.getText().toString().trim();
                int score = Integer.valueOf(scoreEditText.getText().toString().trim());
                String note = noteEditText.getText().toString().trim();


                Date lastUpdate = new Date(System.currentTimeMillis());
                Game game;
                if (bmpImage == null) {
                    game = new Game(gameName,
                            score,
                            note,
                            lastUpdate);

                } else {
                    game = new Game(gameName,
                            score,
                            note,
                            lastUpdate,
                            bmpImage);
                }

                if (game != null) {
                    GameViewModel.insert(game);
                    eventLogger.logEvent("AddGame_GameAdded");
                } else {
                    eventLogger.logEvent("AddGame_NullGameDuringAdding");
                }

                MainActivity.hideKeyboard(this);
                finish();
            }

        });

        updateButton.setOnClickListener(view -> {
            if (checkIfDataCorrect()) {

                Game game = prepareGameObject();

                GameViewModel.update(game);
                eventLogger.logEvent("AddGame_GameUpdated");
                MainActivity.hideKeyboard(this);
                finish();
            }
        });

        deleteButton.setOnClickListener(view -> {

            Game game = prepareGameObject();
            GameViewModel.delete(game);
            eventLogger.logEvent("AddGame_GameDeleted");
            MainActivity.hideKeyboard(this);
            finish();
        });

        takePhotoButton.setOnClickListener(view -> {
            boolean photoActivityAlreadyInitated = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                } else {
                    //   Log.d("xxx", "pierwsze zdjęcie");
                    photoActivityAlreadyInitated = true;
                    startPhotoActivity();
                }
            }

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                //  Log.d("xxx", "drugie zdjęcie");
                if (!photoActivityAlreadyInitated) {
                    startPhotoActivity();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPhotoActivity();
            }
        } else {
            Toast.makeText(this, "camera permission not yet granted", Toast.LENGTH_LONG).show();
        }
    }


    private void startPhotoActivity() {
        // Log.d("xxx", " robie zdjece");
        eventLogger.logEvent("AddGame_PhotoStarted");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_INTENT);
        }
    }

    private Game prepareGameObject() {
        Game game = new Game(gameEditText.getText().toString().trim(),
                Integer.valueOf(scoreEditText.getText().toString().trim()),
                noteEditText.getText().toString().trim(),
                new Date(System.currentTimeMillis()), bmpImage);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_INTENT) {

            if (resultCode == Activity.RESULT_OK) {
                bmpImage = (Bitmap) data.getExtras().get("data");
                if (bmpImage != null) {
                    imageView.setImageBitmap(bmpImage);
                    imageView.setVisibility(View.VISIBLE);
                    eventLogger.logEvent("AddGame_PhotoTaken");
                }
            }

        }
    }
}
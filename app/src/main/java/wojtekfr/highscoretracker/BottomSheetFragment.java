package wojtekfr.highscoretracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import wojtekfr.highscoretracker.adapter.RecyclerViewAdapter;
import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private TextView gameNameTextView;
    private ImageButton showDetailsImageButton;
    private TextView highScoreTextView;
    private ImageButton editImageButton;
    private GameViewModel gameViewModel;

    public int getHighScore() {
        return highScore;
    }


    private int highScore;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    private int position = 0;
    private String searchCondition;





    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }


    public int getControlCode() {
        return controlCode;
    }

    public void setControlCode(int controlCode) {
        this.controlCode = controlCode;
    }

    private int controlCode = 0;

    public BottomSheetFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        gameNameTextView = view.findViewById(R.id.textViewGameName);
        showDetailsImageButton = view.findViewById(R.id.imageButtonShowDetails);
        highScoreTextView = view.findViewById(R.id.textViewHighScore);
        editImageButton = view.findViewById(R.id.imageButtonEdit);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())
                .create(GameViewModel.class);
        gameViewModel.prepareResults();

//      Game game = Objects.requireNonNull(gameViewModel.allGames.getValue().get(position));
//      gameNameTextView.setText(game.getGameName().toString());

        Log.d("xxx control code", "a " + controlCode);
        if (controlCode == 0) {
            gameViewModel.getAllGames().observe(this, games -> {
                gameNameTextView.setText(games.get(position).getGameName());
                highScore = games.get(position).getHighScore();
            });
        } else if (controlCode == 1) {
            gameViewModel.setSearchCondition(searchCondition);
            gameViewModel.prepareResults();
            gameViewModel.getFilteredGames().observe(this, games -> {
                gameNameTextView.setText(games.get(position).getGameName());
                highScore = games.get(position).getHighScore();
            });
        } else if (controlCode == 2){
            gameViewModel.getAllGamesSortedByAlphabetGames().observe(this, games -> {
                gameNameTextView.setText(games.get(position).getGameName());
                highScore = games.get(position).getHighScore();
            });
        } else if (controlCode ==3){
            gameViewModel.getAllGamesSortedByLastUpdate().observe(this, games -> {
                gameNameTextView.setText(games.get(position).getGameName());
                highScore = games.get(position).getHighScore();
            });
        }


        showDetailsImageButton.setOnClickListener(view1 -> {

            highScoreTextView.setVisibility(highScoreTextView.getVisibility() ==
                    View.GONE ? View.VISIBLE : View.GONE);
            editImageButton.setVisibility(editImageButton.getVisibility() ==
                    View.GONE ? View.VISIBLE : View.GONE);
            highScoreTextView.setText(String.valueOf(highScore));
        });

        editImageButton.setOnClickListener(view12 -> {
            Game game = null;
            if (controlCode==0) {
                game = Objects.requireNonNull(gameViewModel.allGames.getValue().get(position));
            } else if (controlCode==1){
                game = Objects.requireNonNull(gameViewModel.filteredGames.getValue().get(position));
            } else if (controlCode==2){
                game = Objects.requireNonNull(gameViewModel.getAllGamesSortedByAlphabetGames().getValue().get(position));
            }
            else if (controlCode==3){
                game = Objects.requireNonNull(gameViewModel.getAllGamesSortedByLastUpdate().getValue().get(position));
            }

            Intent intent = new Intent(getContext(), AddGame.class);
            intent.putExtra("id", game.getId());
            MainActivity.DissmisBottomSheet();
            startActivity(intent);
        });

    }


}

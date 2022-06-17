package wojtekfr.highscoretracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import wojtekfr.highscoretracker.model.MojTekst;
import wojtekfr.highscoretracker.model.SharedModel;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private TextView gameNameTextView;
    private Button button;
    private GameViewModel gameViewModel;
    private SharedModel sharedModel;
    public int getHighScore() {
        return highScore;
    }


    private int highScore;


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
        button = view.findViewById(R.id.testButton);
        return view;


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())
                .create(GameViewModel.class);
        gameViewModel.prepareResults();

        sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
        Game game = sharedModel.getSelectedGame().getValue();
        MojTekst mojTekst = sharedModel.getMojTekst().getValue();
        String mojString = sharedModel.getMojString().getValue();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("xxx", game.getGameName());
                Log.d("xxx", "tekst bottom" + mojTekst.getMojtekst());
                Log.d("xxx", "tekst bottom string" + mojString);
                game.setGameName("nowa");
                MojTekst mojTekstDoZmiany = new MojTekst("nowe srutututu");
                String mojStringDoZmiany = "nowe string srututu";
                sharedModel.setSelectedGame(game);
                sharedModel.setMojTekst(mojTekstDoZmiany);
                sharedModel.setMojString(mojStringDoZmiany);
                dismiss();
            }
        });

//      Game game = Objects.requireNonNull(gameViewModel.allGames.getValue().get(position));
//      gameNameTextView.setText(game.getGameName().toString());

//        Log.d("xxx control code", "a " + controlCode);
//        if (controlCode == 0) {
//            gameViewModel.getAllGames().observe(this, games -> {
//                gameNameTextView.setText(games.get(position).getGameName());
//                highScore = games.get(position).getHighScore();
//            });
//        } else if (controlCode == 1) {
//            gameViewModel.setSearchCondition(searchCondition);
//            gameViewModel.prepareResults();
//            gameViewModel.getFilteredGames().observe(this, games -> {
//                gameNameTextView.setText(games.get(position).getGameName());
//                highScore = games.get(position).getHighScore();
//            });
//        } else if (controlCode == 2){
//            gameViewModel.getAllGamesSortedByAlphabetGames().observe(this, games -> {
//                gameNameTextView.setText(games.get(position).getGameName());
//                highScore = games.get(position).getHighScore();
//            });
//        } else if (controlCode ==3){
//            gameViewModel.getAllGamesSortedByLastUpdate().observe(this, games -> {
//                gameNameTextView.setText(games.get(position).getGameName());
//                highScore = games.get(position).getHighScore();
//            });
//        }



    }


}

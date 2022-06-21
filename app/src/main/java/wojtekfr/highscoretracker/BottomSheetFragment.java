package wojtekfr.highscoretracker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.model.GameViewModel;
import wojtekfr.highscoretracker.model.MojTekst;
import wojtekfr.highscoretracker.model.SharedModel;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    public ChangeSortingListener changeSortingListener;

    private TextView gameNameTextView;
    private Button okButton;
    private GameViewModel gameViewModel;
    private SharedModel sharedModel;

    public int getHighScore() {
        return highScore;
    }


    private int highScore;


    private int position = 0;
    private String searchCondition;


    private RadioButton noSortButton;
    private RadioButton sortByNameButton;
    private RadioButton sortByLastUpdateButton;
    private RadioGroup radioGroup;

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
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d("xxx","dismissed aaaaa");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("xxx", "on atrach");
        try {
            changeSortingListener = (ChangeSortingListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        gameNameTextView = view.findViewById(R.id.textViewGameName);
        okButton = view.findViewById(R.id.buttonBottomOk);
        radioGroup = view.findViewById(R.id.radioGroup);
        noSortButton = view.findViewById(R.id.radioButtonNoSort);
        sortByNameButton = view.findViewById(R.id.radioButtonSortByName);
        sortByLastUpdateButton = view.findViewById(R.id.radioButtonSortByLastUpdate);

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

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("xxx", game.getGameName());
                Log.d("xxx", "tekst bottom" + mojTekst.getMojtekst());

                game.setGameName("nowa");
                MojTekst mojTekstDoZmiany = new MojTekst("nowe srutututu");
                String mojStringDoZmiany = "nowe string srututu";
                sharedModel.setSelectedGame(game);
                sharedModel.setMojTekst(mojTekstDoZmiany);



                //dismissAllowingStateLoss();
                dismiss();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("xxx", "radio " + i);
                sharedModel.setSelectedSorting(i);
                changeSortingListener.applySorting(i);
            }
        });
//        boolean checked = ((RadioButton) view).isChecked();
//
//        switch (view.getId()) {
//            case R.id.radioButtonNoSort:
//                if (checked) Log.d("xxx", "radio no sort");
//                break;
//            case R.id.radioButtonSortByName:
//                if (checked) Log.d("xxx", "radio sort by name");
//                break;
//            case R.id.radioButtonSortByLastUpdate:
//                if (checked) Log.d("xxx", "radio sort by last update");
//                break;
//        }


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

public interface ChangeSortingListener{
        void applySorting(int selectedSortingOption);
}

}

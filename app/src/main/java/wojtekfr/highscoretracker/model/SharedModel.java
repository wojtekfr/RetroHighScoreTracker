package wojtekfr.highscoretracker.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedModel extends ViewModel {
    private final MutableLiveData<Game> selectedGame = new MutableLiveData<>();

    private final MutableLiveData<MojTekst> mojTekst = new MutableLiveData<>();

    private final MutableLiveData<Integer> selectedSorting = new MutableLiveData<>();

    public void setSelectedSorting(Integer integer){
        selectedSorting.setValue(integer);
    }

    public LiveData<Integer> getSelectedSorting(){
        return selectedSorting;
    }

    public void setSelectedGame(Game game) {
        selectedGame.setValue(game);
    }

    public void setMojTekst(MojTekst mojTekstdoUstawienia) {
        mojTekst.setValue(mojTekstdoUstawienia);
    }

    public LiveData<Game> getSelectedGame() {
        return selectedGame;
    }

    public LiveData<MojTekst> getMojTekst() {
        return mojTekst;
    }
}

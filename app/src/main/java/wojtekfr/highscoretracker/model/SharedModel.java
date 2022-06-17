package wojtekfr.highscoretracker.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedModel extends ViewModel {
    private final MutableLiveData<Game> selectedGame = new MutableLiveData<>();

    private final MutableLiveData<MojTekst> mojTekst = new MutableLiveData<>();

    private final MutableLiveData<String> mojString = new MutableLiveData<>();

    public void  setMojString(String string){
        mojString.setValue(string);
    }

    public LiveData<String> getMojString(){
        return  mojString;
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

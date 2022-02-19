package wojtekfr.highscoretracker.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import wojtekfr.highscoretracker.data.GameRepository;
import wojtekfr.highscoretracker.util.GameRoomDatabase;

public class GameViewModel extends AndroidViewModel {
    public static GameRepository repository;
    public LiveData<List<Game>> allGames;
    public LiveData<List<Game>> filteredGames;
    public LiveData<List<Game>> allGamesSortedByAlphabet;
    public LiveData<List<Game>> allGamesSortedByLastUpdate;
    public String searchCondition;
    Application application;

    public GameViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }

    public void prepareResults() {
        if (searchCondition == null) {
            repository = new GameRepository(application, "");
        } else {
            repository = new GameRepository(application, searchCondition);
        }
        allGames = repository.getAllGames();
        filteredGames = repository.getFilteredGames();
        allGamesSortedByAlphabet = repository.getAllGamesSortedByAlphabet();
        allGamesSortedByLastUpdate = repository.getAllGamesSortedByLastUpdate();
    }

    public LiveData<List<Game>> getAllGames() {
        return allGames;
    }

    public LiveData<List<Game>> getFilteredGames() {
        return filteredGames;
    }

    public LiveData<List<Game>> getAllGamesSortedByAlphabetGames() {
        return allGamesSortedByAlphabet;
    }

    public LiveData<List<Game>> getAllGamesSortedByLastUpdate() {
        return allGamesSortedByLastUpdate;
    }

    public static void insert(Game game) {
        repository.insert(game);
    }

    public LiveData<Game> get(int id) {
        return repository.get(id);
    }

    public static void update(Game game) {
        repository.update(game);
    }

    public static void delete(Game game) {
        repository.delete(game);
    }

    public static void deleteAll() {
        repository.deleteAll();
    }
}

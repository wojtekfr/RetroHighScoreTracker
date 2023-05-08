package wojtekfr.highscoretracker.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;

import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.util.GameRoomDatabase;

public class GameRepository {

    private GameDao gameDao;
    private LiveData<List<Game>> allGames;
    private LiveData<List<Game>> filteredGames;
    private LiveData<List<Game>> allGamesSortedByAlphabet;
    private LiveData<List<Game>> allGamesSortedByLastUpdate;

    private LiveData<Integer> gameCountLive;

    public GameRepository(Application application, String searchCondition) {
        GameRoomDatabase db = GameRoomDatabase.getDatabase(application);
        gameDao = db.gameDao();
        allGames = gameDao.getAllGames();
        filteredGames = gameDao.getFilteredGames(searchCondition);
        allGamesSortedByAlphabet = gameDao.getAllGamesSortedByAlphabet();
        allGamesSortedByLastUpdate = gameDao.getAllGamesSortedByLastUpdate();
        gameCountLive = gameDao.countGames();

    }

    public LiveData<List<Game>> getAllGames() {
        return allGames;
    }
    public LiveData<List<Game>> getFilteredGames(){return filteredGames;}
    public LiveData<List<Game>> getAllGamesSortedByAlphabet(){return allGamesSortedByAlphabet;}
    public LiveData<List<Game>> getAllGamesSortedByLastUpdate(){return allGamesSortedByLastUpdate;}

    public void insert(Game game) {
        GameRoomDatabase.databaseWriterExecutor.execute(() -> {
            gameDao.insert(game);
        });
    }

    public LiveData<Game> get(int id) {
        return gameDao.get(id);
    }

    public void update(Game game) {
        GameRoomDatabase.databaseWriterExecutor.execute(() -> gameDao.update(game));
    }

    public void delete(Game game){
        GameRoomDatabase.databaseWriterExecutor.execute(() -> gameDao.delete(game));
    }

    public void deleteAll(){
        GameRoomDatabase.databaseWriterExecutor.execute(() -> gameDao.deleteAll());
    }
    public LiveData<Integer> getCountGames(){
        return gameCountLive;
    }
}

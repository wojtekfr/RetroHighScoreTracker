package wojtekfr.highscoretracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import wojtekfr.highscoretracker.model.Game;

@Dao
public interface GameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Game game);

    @Query("DELETE FROM game_table")
    void deleteAll();

    @Query("SELECT * FROM game_table")
    LiveData<List<Game>> getAllGames();

    @Query("SELECT * FROM game_table  ORDER BY game COLLATE NOCASE ASC ")
    LiveData<List<Game>> getAllGamesSortedByAlphabet();


    @Query("SELECT * FROM game_table ORDER BY last_update DESC")
    LiveData<List<Game>> getAllGamesSortedByLastUpdate();


    @Query("SELECT * FROM game_table WHERE game LIKE :searchCondition ORDER BY game COLLATE NOCASE ASC")
    LiveData<List<Game>> getFilteredGames(String searchCondition);


    @Query("SELECT * FROM game_table WHERE game_table.id == :id")
    LiveData<Game> get(int id);

    @Update
    void update(Game game);

    @Delete
    void delete(Game game);

    @Query("SELECT COUNT(id) FROM game_table")
    LiveData<Integer> countGames();

}

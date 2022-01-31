package wojtekfr.highscoretracker.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wojtekfr.highscoretracker.data.GameDao;
import wojtekfr.highscoretracker.model.Game;
//aa
@Database(entities = {Game.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class GameRoomDatabase extends RoomDatabase {

    public abstract GameDao gameDao();

    public static final int NUMBER_OF_THREADS = 4;

    public static volatile GameRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriterExecutor = Executors
            .newFixedThreadPool(NUMBER_OF_THREADS);

    public static GameRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GameRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GameRoomDatabase.class, "game_database").
                            addCallback(sRoomDatabaseCallback).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

    public static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriterExecutor.execute(() -> {
                GameDao gameDao = INSTANCE.gameDao();
                gameDao.deleteAll();

                Game game = new Game("Mario", 1);
                gameDao.insert(game);
                game = new Game("Wario", 2);
                game.setNote("note");
                gameDao.insert(game);

            });
        }
    };

}

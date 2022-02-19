package wojtekfr.highscoretracker.model;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Date;

@Entity(tableName = "game_table")
public class Game {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "game")
    private String gameName;
    @ColumnInfo(name ="high_score")
    private int highScore;
    @ColumnInfo(name = "note")
    private String note;
    @ColumnInfo(name = "last_update")
    private Date lastUpdate;
    @ColumnInfo(typeAffinity =  ColumnInfo.BLOB)
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }



//    public Game(String gameName, int highScore, String note, Date lastUpdate) {
    public Game(String gameName, int highScore, String note, Date lastUpdate,  Bitmap image) {
        this.gameName = gameName;
        this.highScore = highScore;
        this.note = note;
        this.lastUpdate = lastUpdate;
        this.image = image;

    }

    public Game(String gameName, int highScore, String note, Date lastUpdate) {
        this.gameName = gameName;
        this.highScore = highScore;
        this.note = note;
        this.lastUpdate = lastUpdate;
        this.image = image;

    }



    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }






    public Game(String gameName, int highScore) {
        this.id = id;
        this.gameName = gameName;
        this.highScore = highScore;
    }

    public Game() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}

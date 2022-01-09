package wojtekfr.highscoretracker.util;

import androidx.room.TypeConverter;

import java.sql.Timestamp;
import java.util.Date;

public class Converters {


    @TypeConverter
    public static Long dateToTimeStamp(Date date){
        return  date.getTime();
    }

    @TypeConverter
    public static Date timeStampToDate(long timeStamp){
        return new Date (timeStamp);
    }

}

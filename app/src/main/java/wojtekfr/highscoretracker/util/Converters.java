package wojtekfr.highscoretracker.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class Converters {


    @TypeConverter
    public static Long dateToTimeStamp(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public static Date timeStampToDate(long timeStamp) {
        return new Date(timeStamp);
    }

    @TypeConverter
    public static byte[] convertImage2ByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }

    @TypeConverter
    public static Bitmap convertByteArray2Image(byte[] array) {
        if (array != null) {
            return BitmapFactory.decodeByteArray(array, 0, array.length);
        } else {
            return null;
        }
    }
}

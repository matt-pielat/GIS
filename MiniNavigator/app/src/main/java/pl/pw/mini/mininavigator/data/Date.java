package pl.pw.mini.mininavigator.data;

import java.io.Serializable;
import java.util.Calendar;

public class Date implements Serializable{
    public int Year;
    public int Month;
    public int Day;
    public int Hour;
    public int Minute;
    public int Second;

    public static Date Now() {
        Calendar c = Calendar.getInstance();

        Date date = new Date();
        date.Year = c.get(Calendar.YEAR);
        date.Month = c.get(Calendar.MONTH);
        date.Day = c.get(Calendar.DAY_OF_MONTH);
        date.Hour = c.get(Calendar.HOUR_OF_DAY);
        date.Minute = c.get(Calendar.MINUTE);
        date.Second = c.get(Calendar.SECOND);

        return date;
    }

    @Override
    public String toString() {

        return Day + "." + Month + "." + Year + "r. " + Hour + ":" + Minute + ":" + Second;
    }
}

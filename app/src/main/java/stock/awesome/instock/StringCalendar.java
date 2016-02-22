package stock.awesome.instock;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class StringCalendar {

    private static SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
    private static GregorianCalendar gregCal = null;

    public static String toString(GregorianCalendar calendar) {
        fmt.setCalendar(calendar);
        return fmt.format(calendar.getTime());
    }

    public static GregorianCalendar toCalendar(String calendarStr) {
        try {
            Date date = fmt.parse(calendarStr);
            gregCal.setTime(date);

        } catch (ParseException e) {
            Log.e("str to cal failed", e.getMessage());
        } finally {
            return gregCal;
        }
    }

}

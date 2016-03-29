package stock.awesome.instock.misc_classes;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class StringCalendar {

    // only this format is parseable by firebase
    private static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static GregorianCalendar gregCal = new GregorianCalendar();
    private static SimpleDateFormat displayFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    public static String toProperDateString(GregorianCalendar calendar) {
        displayFmt.setCalendar(calendar);
        return displayFmt.format(calendar.getTime());
    }

    public static String toString(GregorianCalendar calendar) {
        fmt.setCalendar(calendar);
        return fmt.format(calendar.getTime());
    }

    public static GregorianCalendar toCalendar(String calendarStr) {
        try {
            Date date = fmt.parse(calendarStr);
            Log.e("TESTING", "\t" + date.toString());
            gregCal.setTime(date);

        } catch (ParseException e) {
            Log.e("str to cal failed", e.getMessage());
        }
        return gregCal;
    }

    public static GregorianCalendar toCalendarProper(String calendarStr) {
        try {
            Date date = displayFmt.parse(calendarStr);
            gregCal.setTime(date);

        } catch (ParseException e) {
            Log.e("str to cal failed", e.getMessage());
        }
        return gregCal;
    }

}

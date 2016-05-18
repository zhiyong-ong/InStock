package stock.awesome.instock.misc_classes;

import android.util.Log;

import java.text.DateFormat;
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
    private static Boolean flag = false;

    public static String toProperDateString(GregorianCalendar calendar) {

        if(calendar == null) {
            Log.e("EXPIRY", "calendar null");
            flag = true;
            return "";
        }
        else {
            Log.e("EXPIRY", "calendar not null");
            displayFmt.setCalendar(calendar);
            flag = false;
            return displayFmt.format(calendar.getTime());
        }
    }

    public static String toString(GregorianCalendar calendar) {
        if (calendar == null) {
            return "";
        } else {
            fmt.setCalendar(calendar);
            return fmt.format(calendar.getTime());
        }
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
        Log.e("DATE", "FLAG: " + flag);
        if(calendarStr.equals("")) {
            return null;
        }
        else {
            if (flag) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = df.parse(calendarStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.e("DATE", "DATE string is: " + calendarStr);
                Log.e("DATE", "DATE is: " + date.toString());
                gregCal.setTime(date);
                return gregCal;
            } else {
                try {

                    Date date = displayFmt.parse(calendarStr);
                    gregCal.setTime(date);

                } catch (ParseException e) {
                    Log.e("str to cal failed", e.getMessage());
                }
                return gregCal;
            }
        }
    }

}

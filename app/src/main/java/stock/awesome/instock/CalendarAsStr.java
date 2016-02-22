package stock.awesome.instock;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Kabir on 22/02/2016.
 */
public class CalendarAsStr {

    public static String format(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        fmt.setCalendar(calendar);
        return fmt.format(calendar.getTime());
    }

}

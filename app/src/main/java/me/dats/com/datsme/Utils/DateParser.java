package me.dats.com.datsme.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//DateParse.java to parse date for grouping the chat
public class DateParser {
    private static DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

    public static String convertDateToString(long date) {
        Date d = new Date(date);
        String strDate = "";
        strDate = dateFormat1.format(d);
        return strDate;
    }
}

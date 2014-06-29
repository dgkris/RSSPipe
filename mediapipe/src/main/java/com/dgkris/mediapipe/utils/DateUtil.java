package com.dgkris.mediapipe.utils;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Useful Date utilities.
 */
public final class DateUtil {


    public static DateTime parse(String dateString) throws ParseException {
        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(dateString);
        if (groups != null && groups.size() > 0) {
            return new DateTime(groups.get(0).getDates().get(0));
        } else {
            return null;
        }
    }

    public static boolean isValidDate(String dateString) {
        try {
            parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String format(DateTime dateTime, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format == null ? "EEE MMM dd HH:mm:ss zzz yyyy" : format);
        return formatter.format(dateTime);
    }

}
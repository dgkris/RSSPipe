package com.dgkris.mediapipe.utils;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class Utils {

    public static String stripCDATA(String s) {
        s = s.trim();
        if (s.startsWith("<![CDATA[")) {
            s = s.substring(9);
            int i = s.indexOf("]]&gt;");
            if (i == -1) {
                throw new IllegalStateException(
                        "argument starts with <![CDATA[ but cannot find pairing ]]&gt;");
            }
            s = s.substring(0, i);
        }
        return s;
    }

    public static DateTime convertToDateTime(String s) {
        if (s.contains("CDATA"))
            s = stripCDATA(s);
        try {
            return new DateTime(DateUtil.parse(s));
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getStringFromStream(InputStream inputStream) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(inputStream, writer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return writer.toString();
    }

    public static byte[] getMD5HashForString(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(s.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
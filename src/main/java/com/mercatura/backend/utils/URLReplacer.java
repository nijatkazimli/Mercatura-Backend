package com.mercatura.backend.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLReplacer {
    public static String replaceAzDocker(String url) {
        String regex = "^http://azuriteDocker:10000/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.replaceFirst("http://localhost:10000/");
        }
        return url;
    }
}


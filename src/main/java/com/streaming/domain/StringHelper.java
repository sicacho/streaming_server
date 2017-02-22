package com.streaming.domain;

import java.util.Arrays;

/**
 * Created by Administrator on 6/9/2016.
 */
public class StringHelper {

    public static String[] removeEmptyElement(String[] urls) {
        return Arrays.stream(urls).filter(s -> !s.isEmpty()).toArray(value -> new String[value]);
    }

    public static boolean checkIsEmptyArray(String[] url) {
        return Arrays.stream(url).noneMatch(s -> !s.equals(""));
    }

    public static boolean isNotStartwith(String url,Enum[] temp) {
        for (int i = 0; i < temp.length; i++) {
            if(url.startsWith(temp[i].toString())){
                return true;
            }
        }
        return false;
    }
}

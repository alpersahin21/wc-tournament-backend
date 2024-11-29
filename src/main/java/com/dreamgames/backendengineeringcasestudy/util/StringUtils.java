package com.dreamgames.backendengineeringcasestudy.util;

public class StringUtils {

    private StringUtils() {
    }

    public static Boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}


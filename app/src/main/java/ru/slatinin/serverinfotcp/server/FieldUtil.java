package ru.slatinin.serverinfotcp.server;

import java.lang.reflect.Field;

public class FieldUtil {

    public static String getFromFloatArray(Object floatArray) {
        String loadAverage = "";
        try {
            float[] array = (float[]) floatArray;
            StringBuilder sb = new StringBuilder();
            for (float single : array) {
                sb.append(single).append(", ");
            }
            loadAverage = sb.toString();
            if (!loadAverage.isEmpty()) {
                loadAverage = loadAverage.substring(0, loadAverage.length() - 2);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return loadAverage;
    }

    public static String getStringFromString(Object string) {
        String normalString = "";
        try {
            normalString = (String) string;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return normalString;
    }

    public static String getStringFromInt(Object integer) {
        String intToString = "";
        try {
            int normalInt = (int) integer;
            intToString = String.valueOf(normalInt);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return intToString;
    }

    public String getFullClassString(Field[] fields){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            Object fieldType = fields[i].getType();
            if (fieldType instanceof String) {

            }
        }
        return "";
    }
}

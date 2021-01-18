package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

public class JsonUtil {

    public static String getString(JsonObject object, String key) {
        if (object != null && object.has(key)) {
            return object.get(key).getAsString();
        }
        return "";
    }

    public static int getInt(JsonObject object, String key) {
        if (object != null && object.has(key)) {
            try {
                return object.get(key).getAsInt();
            } catch (NumberFormatException | UnsupportedOperationException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static float getFloat(JsonObject object, String key) {
        if (object != null && object.has(key)) {
            try {
                String floatWithComma = object.get(key).getAsString();
                return getFloatFromStringWithComma(floatWithComma);
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
        }
        return 0f;
    }

    public static long getLong(JsonObject object, String key) {
        if (object != null && object.has(key)) {
            try {
                return object.get(key).getAsLong();
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private static float getFloatFromStringWithComma(String floatWithComma) {
        try {
            if (floatWithComma.contains(",")) {
                floatWithComma = floatWithComma.replace(",", ".");
            }
            return Float.parseFloat(floatWithComma);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    public static boolean getBoolean(JsonObject object, String key) {
        if (object != null && object.has(key)) {
            try {
                return object.get(key).getAsBoolean();
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

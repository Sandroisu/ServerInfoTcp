package ru.slatinin.serverinfotcp.server.serverutil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonUtil {

    public static JsonArray getJsonArray(JsonObject object, String key){
        JsonArray array = new JsonArray();
        if (object!=null && object.has(key)){
            try {
                array = object.getAsJsonArray(key);
            }catch (UnsupportedOperationException e){
                e.printStackTrace();
            }
        }else {
            try {
                array = object.getAsJsonArray();
            }catch (UnsupportedOperationException e){
                e.printStackTrace();
            }
        }
        return array;
    }

    public static JsonObject getJsonObject(JsonObject jsonObject, String key) {
        if (jsonObject != null && jsonObject.has(key)) {
            try {
                return jsonObject.getAsJsonObject(key);
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

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

}

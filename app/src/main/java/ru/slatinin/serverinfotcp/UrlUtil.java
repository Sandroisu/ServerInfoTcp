package ru.slatinin.serverinfotcp;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static ru.slatinin.serverinfotcp.ui.MainActivity.BASE_URL;
import static ru.slatinin.serverinfotcp.ui.MainActivity.REPO;
import static ru.slatinin.serverinfotcp.ui.MainActivity.SHARED_PREFS;

public class UrlUtil {

    public static String getUrl(String ip, String type, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String repo = sharedPreferences.getString(REPO, "");
        String baseUrl = sharedPreferences.getString(BASE_URL, "");
        String url = baseUrl + repo + "/%3Ahome%3Atcp%3A" + type + "-monitor.prpt/generatedContent?c_server="
                + ip + "&userid=tcp&password=monitor-0&output-target=pageable/pdf";
        if (url.contains("//")) {
            url = url.replace("//", "/");
        }
        url = "http://" + url;
        return url;
    }

    public static String getUrl(String ip, String type, String parameter, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String repo = sharedPreferences.getString(REPO, "");
        String baseUrl = sharedPreferences.getString(BASE_URL, "");
        String url = baseUrl + repo + "/%3Ahome%3Atcp%3A" + type + "-monitor.prpt/generatedContent?c_server="
                + ip + parameter + "&userid=tcp&password=monitor-0&output-target=pageable/pdf";
        if (url.contains("//")) {
            url = url.replace("//", "/");
        }
        url = "http://" + url;
        return url;
    }
}
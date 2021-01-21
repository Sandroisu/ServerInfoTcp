package ru.slatinin.serverinfotcp.server.servertop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.slatinin.serverinfotcp.server.JsonUtil;

public class ServerCommon extends BaseTopInfo {

    private final String TIME = "time";
    private final String UP_HOURS = "up_hours";
    private final String UP_DAYS = "up_days";
    private final String USER = "user";
    private final String LOAD_AVERAGE = "load_average";
    private final String N_LA1 = "n_la1";
    private final String N_LA2 = "n_la2";
    private final String N_LA3 = "n_la3";

    public String time;
    public String up_hours;
    public String user;
    public String up_days;
    public float[] load_average;

    public ServerCommon(JsonObject object) {
        super();
        time = JsonUtil.getString(object, TIME);
        up_hours = JsonUtil.getString(object, UP_HOURS);
        up_days = JsonUtil.getString(object, UP_DAYS);
        user = JsonUtil.getString(object, USER);
        if (object.has(LOAD_AVERAGE)) {
            JsonArray array = object.get(LOAD_AVERAGE).getAsJsonArray();
            load_average = new float[array.size()];
            for (int i = 0; i < array.size(); i++) {
                try {
                    String number = array.get(i).getAsString();
                    if (number.contains(",")) {
                        number = number.replace(",", ".");
                    }
                    load_average[i] = Float.parseFloat(number);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }else {
            if (object.has(N_LA1)&&object.has(N_LA2)&&object.has(N_LA3)){}
            load_average = new float[3];
            load_average[0] = JsonUtil.getFloat(object, N_LA1);
            load_average[1] = JsonUtil.getFloat(object, N_LA2);
            load_average[2] = JsonUtil.getFloat(object, N_LA3);
        }
    }

    public String getValueByName(String name) {
        switch (name) {
            case TIME:
                return String.valueOf(time);
            case UP_DAYS:
                return String.valueOf(up_days);
            case UP_HOURS:
                return String.valueOf(up_hours);
            case USER:
                return String.valueOf(user);
            case LOAD_AVERAGE:
                StringBuilder sb = new StringBuilder();
                for (float single : load_average) {
                    sb.append(single).append(", ");
                }
                String loadAverage = sb.toString();
                if (!loadAverage.isEmpty()) {
                    loadAverage = loadAverage.substring(0, loadAverage.length() - 2);
                }
                return loadAverage;
        }
        return "";
    }

    @Override
    public String[] getFieldAsLabels() {
        String[] labels = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            labels[i] = fields.get(i).getName();
        }
        return labels;
    }

    @Override
    public float[] getFieldValues() {
        return load_average;
    }

    @Override
    protected List<Field> initFields() {
        return new ArrayList<>(Arrays.asList(getClass().getFields()));
    }
}

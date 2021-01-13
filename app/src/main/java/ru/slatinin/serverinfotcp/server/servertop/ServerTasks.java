package ru.slatinin.serverinfotcp.server.servertop;

import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.slatinin.serverinfotcp.server.JsonUtil;

public class ServerTasks extends BaseTopInfo{

    private final String TOTAL = "total";
    private final String RUNNING = "running";
    private final String SLEEPING = "sleeping";
    private final String STOPPED = "stopped";
    private final String ZOMBIE = "zombie";

    public int total;
    public int running;
    public int sleeping;
    public int stopped;
    public int zombie;

    public ServerTasks(JsonObject object) {
        super();
        total = JsonUtil.getInt(object, TOTAL);
        running = JsonUtil.getInt(object, RUNNING);
        sleeping = JsonUtil.getInt(object, SLEEPING);
        stopped = JsonUtil.getInt(object, STOPPED);
        zombie = JsonUtil.getInt(object, ZOMBIE);
    }

    protected String getValueByName(String name){
        switch (name){
            case TOTAL:
                return String.valueOf(total);
            case RUNNING:
                return String.valueOf(running);
            case SLEEPING:
                return String.valueOf(sleeping);
            case STOPPED:
                return String.valueOf(stopped);
            case ZOMBIE:
                return String.valueOf(zombie);
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
        float [] values = new float[fields.size()];
        for (int i = 0; i < values.length; i++) {
            try {
                values[i] = fields.get(i).getFloat(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    @Override
    protected List<Field> initFields() {
        return new ArrayList<>(Arrays.asList(getClass().getFields()));
    }
}

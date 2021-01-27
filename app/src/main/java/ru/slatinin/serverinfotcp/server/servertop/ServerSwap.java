package ru.slatinin.serverinfotcp.server.servertop;

import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverutil.JsonUtil;

public class ServerSwap extends BaseTopInfo{

    private final String TOTAL = "total";
    private final String USED = "used";
    private final String FREE = "free";
    private final String AVAIL_MEM = "avail_mem";

    public int total;
    public int used;
    public int free;
    public int avail_mem;

    public ServerSwap() {
        super();
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

    public void update(JsonObject object){
        total = JsonUtil.getInt(object, TOTAL);
        used = JsonUtil.getInt(object, USED);
        free = JsonUtil.getInt(object, FREE);
        avail_mem = JsonUtil.getInt(object, AVAIL_MEM);
    }
}

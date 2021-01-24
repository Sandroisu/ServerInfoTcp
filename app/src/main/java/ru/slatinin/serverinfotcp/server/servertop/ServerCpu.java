package ru.slatinin.serverinfotcp.server.servertop;

import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverutil.JsonUtil;

public class ServerCpu extends BaseTopInfo{

    private final String US = "us";
    private final String SY = "sy";
    private final String NI = "ni";
    private final String ID = "id";
    private final String WA = "wa";
    private final String HI = "hi";
    private final String SI = "si";
    private final String ST = "st";

    public float us;
    public float sy;
    public float ni;
    public float id;
    public float wa;
    public float hi;
    public float si;
    public float st;

    public ServerCpu() {
        super();
    }

    protected String getValueByName(String name){
        switch (name){
            case US:
                return String.valueOf(us);
            case SY:
                return String.valueOf(sy);
            case NI:
                return String.valueOf(ni);
            case ID:
                return String.valueOf(id);
            case WA:
                return String.valueOf(wa);
            case HI:
                return String.valueOf(hi);
            case SI:
                return String.valueOf(si);
            case ST:
                return String.valueOf(st);
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

    public void update(JsonObject object) {
        us = JsonUtil.getFloat(object, US);
        sy = JsonUtil.getFloat(object, SY);
        ni = JsonUtil.getFloat(object, NI);
        id = JsonUtil.getFloat(object, ID);
        wa = JsonUtil.getFloat(object, WA);
        hi = JsonUtil.getFloat(object, HI);
        si = JsonUtil.getFloat(object, SI);
        st = JsonUtil.getFloat(object, ST);
    }
}

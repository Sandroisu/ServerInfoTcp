package ru.slatinin.serverinfotcp.server.servertop;


import android.graphics.Color;

import java.lang.reflect.Field;
import java.util.List;

public abstract class BaseTopInfo {

    protected List<Field> fields;

    public abstract String [] getFieldAsLabels();

    public abstract float [] getFieldValues();

    public static int [] getBarChartColors(){
        int [] colors = new int[16];
        colors[0] = Color.parseColor("#0091ea");
        colors[1] = Color.parseColor("#00c853");
        colors[2] = Color.parseColor("#dd2c00");
        colors[3] = Color.parseColor("#ffc400");
        colors[4] = Color.parseColor("#e040fb");
        colors[5] = Color.parseColor("#18ffff");
        colors[6] = Color.parseColor("#33691e");
        colors[7] = Color.parseColor("#ff6f00");
        colors[8] = Color.parseColor("#3e2723");
        colors[9] = Color.parseColor("#4a148c");
        colors[10] = Color.parseColor("#00acc1");
        colors[11] = Color.parseColor("#2979ff");
        colors[12] = Color.parseColor("#dd2c00");
        colors[13] = Color.parseColor("#3d5afe");
        colors[14] = Color.parseColor("#00838f");
        colors[15] = Color.parseColor("#fdd835");
        return colors;
    }

    public BaseTopInfo(){
        this.fields = initFields();
        for (int i = 0; i < fields.size(); i++) {
            if ("total".equals(fields.get(i).getName())){
                Field itemToMove = fields.get(i);
                fields.remove(fields.get(i));
                fields.add(0, itemToMove);
                break;
            }
        }
    }

    protected abstract List<Field> initFields();

}

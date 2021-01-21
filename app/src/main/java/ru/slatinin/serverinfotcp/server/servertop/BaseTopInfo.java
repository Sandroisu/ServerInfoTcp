package ru.slatinin.serverinfotcp.server.servertop;


import android.graphics.Color;

import java.lang.reflect.Field;
import java.util.List;

public abstract class BaseTopInfo {

    protected List<Field> fields;

    public abstract String [] getFieldAsLabels();

    public abstract float [] getFieldValues();

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

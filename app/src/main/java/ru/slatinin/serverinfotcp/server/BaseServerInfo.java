package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

public abstract class BaseServerInfo {

    public static final String C_IP = "c_ip";
    public final String c_ip;

    public BaseServerInfo(JsonObject object){
        c_ip = JsonUtil.getString(object, C_IP);
    }
}

package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

public class ServerNetLog extends BaseServerInfo {

    public static final String C_NAME = "c_name";
    public static final String N_SENT = "n_sent";
    public static final String N_RECEIVED = "n_received";
    public static final String C_SENT_NAME = "c_sent_name";
    public static final String C_RECEIVED_NAME = "c_received_name";
    public static final String N_RATE = "n_rate";
    public static final String C_RATE_NAME = "c_rate_name";

    public final String c_name;
    public final String c_sent_name;
    public final String c_received_name;
    public final String c_rate_name;
    public final float n_sent;
    public final float n_received;
    public final float n_rate;

    public ServerNetLog(JsonObject object) {
        super(object);
        c_name = JsonUtil.getString(object, C_NAME);
        c_sent_name = JsonUtil.getString(object, C_SENT_NAME);
        c_received_name = JsonUtil.getString(object, C_RECEIVED_NAME);
        c_rate_name = JsonUtil.getString(object, C_RATE_NAME);
        n_sent = JsonUtil.getFloat(object, N_SENT);
        n_received = JsonUtil.getFloat(object, N_RECEIVED);
        n_rate = JsonUtil.getFloat(object, N_RATE);
    }

}

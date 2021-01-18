package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

public class ServerNET extends BaseServerInfo {

    public static final String C_NAME = "c_name";
    public static final String N_SENT = "n_sent";
    public static final String N_RECEIVED = "n_received";

    public final String c_name;
    public final float n_sent;
    public final float n_received;

    public ServerNET(JsonObject object) {
        super(object);
        c_name = JsonUtil.getString(object, C_NAME);
        n_sent = JsonUtil.getFloat(object, N_SENT);
        n_received = JsonUtil.getFloat(object, N_RECEIVED);
    }

    public String getInfoString() {
        return N_SENT + " - " + n_sent + "KB/s;\t\t" + N_RECEIVED + " - " + n_received + "Kb/s;";
    }
}

package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

import ru.slatinin.serverinfotcp.TimeUtil;

public class ServerNET extends BaseServerInfo {

    public static final String C_NAME = "c_name";
    public static final String N_SENT = "n_sent";
    public static final String N_RECEIVED = "n_received";
    private final String DX_CREATED = "dx_created";

    public final String c_name;
    public final float n_sent;
    public final float n_received;
    public final String time;

    public ServerNET(JsonObject object) {
        super(object);
        c_name = JsonUtil.getString(object, C_NAME);
        n_sent = JsonUtil.getFloat(object, N_SENT);
        n_received = JsonUtil.getFloat(object, N_RECEIVED);
        if (object.has(DX_CREATED)) {
            time = TimeUtil.formatTimeToMinutes(JsonUtil.getString(object, DX_CREATED));
        } else {
            time = TimeUtil.formatMillisToMinutes(System.currentTimeMillis());
        }
    }

    public String getInfoString() {
        return N_SENT + " - " + n_sent + "KB/s;\t\t" + N_RECEIVED + " - " + n_received + "Kb/s;";
    }
}

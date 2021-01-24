package ru.slatinin.serverinfotcp.server.servernetlog;

import com.google.gson.JsonObject;

import ru.slatinin.serverinfotcp.TimeUtil;
import ru.slatinin.serverinfotcp.server.BaseServerInfo;
import ru.slatinin.serverinfotcp.server.servernet.ServerNet;
import ru.slatinin.serverinfotcp.server.serverutil.JsonUtil;

public class ServerNetLog extends BaseServerInfo {

    public static final String C_NAME = "c_name";
    public static final String N_SENT = "n_sent";
    public static final String N_RECEIVED = "n_received";
    public static final String C_SENT_NAME = "c_sent_name";
    public static final String C_RECEIVED_NAME = "c_received_name";
    public static final String N_RATE = "n_rate";
    public static final String C_RATE_NAME = "c_rate_name";
    private final String DX_CREATED = "dx_created";

    public final String c_name;
    public final String c_sent_name;
    public final String c_received_name;
    public final String c_rate_name;
    public final float n_sent;
    public final float n_received;
    public final float n_rate;
    public final String time;

    public ServerNetLog(JsonObject object) {
        super(object);
        c_name = JsonUtil.getString(object, C_NAME);
        c_sent_name = JsonUtil.getString(object, C_SENT_NAME);
        c_received_name = JsonUtil.getString(object, C_RECEIVED_NAME);
        c_rate_name = JsonUtil.getString(object, C_RATE_NAME);
        n_sent = JsonUtil.getFloat(object, N_SENT);
        n_received = JsonUtil.getFloat(object, N_RECEIVED);
        n_rate = JsonUtil.getFloat(object, N_RATE);
        if (object.has(DX_CREATED)) {
            time = TimeUtil.formatTimeToMinutes(JsonUtil.getString(object, DX_CREATED));
        } else {
            time = TimeUtil.formatMillisToMinutes(System.currentTimeMillis());
        }
    }

    public ServerNetLog(ServerNetLog serverNetLog) {
        super(serverNetLog.c_ip);
        c_name = serverNetLog.c_name;
        c_sent_name = serverNetLog.c_sent_name;
        c_received_name = serverNetLog.c_received_name;
        c_rate_name = serverNetLog.c_rate_name;
        n_sent = serverNetLog.n_sent;
        n_received = serverNetLog.n_received;
        n_rate = serverNetLog.n_rate;
        time = serverNetLog.time;
    }

}

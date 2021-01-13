package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

public class ServerPSQL extends BaseServerInfo {

    public static final String C_DATNAME = "c_datname";
    public static final String N_XACT_COMMIT = "n_xact_commit";
    public static final String N_NUMBACKENDS = "n_numbackends";

    public final String c_datname;
    public final int n_xact_commit;
    public final int n_numbackends;

    public ServerPSQL(JsonObject object) {
        super(object);
        c_datname = JsonUtil.getString(object, C_DATNAME);
        n_xact_commit = JsonUtil.getInt(object, N_XACT_COMMIT);
        n_numbackends = JsonUtil.getInt(object, N_NUMBACKENDS);
    }
}

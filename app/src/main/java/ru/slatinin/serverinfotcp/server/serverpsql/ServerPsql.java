package ru.slatinin.serverinfotcp.server.serverpsql;

import com.google.gson.JsonObject;

import ru.slatinin.serverinfotcp.TimeUtil;
import ru.slatinin.serverinfotcp.server.BaseServerInfo;
import ru.slatinin.serverinfotcp.server.serverutil.JsonUtil;

public class ServerPsql extends BaseServerInfo {

    public static final String C_DATNAME = "c_datname";
    public static final String N_XACT_COMMIT = "n_xact_commit";
    public static final String N_NUMBACKENDS = "n_numbackends";
    private final String DX_CREATED = "dx_created";

    public final String c_datname;
    public final int n_xact_commit;
    public int n_xact_commit_calculated;
    public final int n_numbackends;
    public final long createTime;
    public final String time;

    public ServerPsql(JsonObject object) {
        super(object);
        c_datname = JsonUtil.getString(object, C_DATNAME);
        n_xact_commit = JsonUtil.getInt(object, N_XACT_COMMIT);
        n_numbackends = JsonUtil.getInt(object, N_NUMBACKENDS);
        createTime = System.currentTimeMillis();
        n_xact_commit_calculated = 0;
        if (object.has(DX_CREATED)) {
            time = TimeUtil.formatTimeToMinutes(JsonUtil.getString(object, DX_CREATED));
        } else {
            time = TimeUtil.formatMillisToMinutes(System.currentTimeMillis());
        }
    }

    public ServerPsql(ServerPsql psqlToCopy) {
        super(psqlToCopy.c_ip);
        c_datname = psqlToCopy.c_datname;
        n_xact_commit = psqlToCopy.n_xact_commit;
        n_numbackends = psqlToCopy.n_numbackends;
        createTime = psqlToCopy.createTime;
        time = psqlToCopy.time;
        n_xact_commit_calculated = psqlToCopy.n_xact_commit_calculated;
    }

    public void calculateXactCommit(int previous) {
        n_xact_commit_calculated = n_xact_commit - previous;
    }
}

package ru.slatinin.serverinfotcp.server.serverdf;

import com.google.gson.JsonObject;

import ru.slatinin.serverinfotcp.server.BaseServerInfo;
import ru.slatinin.serverinfotcp.server.JsonUtil;

public class SingleServerDF extends BaseServerInfo {

    public static final String C_NAME = "c_name";
    public static final String N_BLOCKS = "n_blocks";
    public static final String N_USED = "n_used";
    public static final String N_AVAILABLE = "n_available";
    public static final String N_USE = "n_use";

    public final String c_name;
    public final int n_blocks;
    public final int n_used;
    public final int n_available;
    public final int n_use;

    public SingleServerDF(JsonObject object) {
        super(object);
        c_name = JsonUtil.getString(object, C_NAME);
        n_blocks = JsonUtil.getInt(object, N_BLOCKS);
        n_used = JsonUtil.getInt(object, N_USED);
        n_available = JsonUtil.getInt(object, N_AVAILABLE);
        n_use = JsonUtil.getInt(object, N_USE);
    }
}

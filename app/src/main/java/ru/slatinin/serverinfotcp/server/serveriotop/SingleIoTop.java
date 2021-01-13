package ru.slatinin.serverinfotcp.server.serveriotop;

import com.google.gson.JsonObject;

import ru.slatinin.serverinfotcp.server.BaseServerInfo;
import ru.slatinin.serverinfotcp.server.JsonUtil;

public class SingleIoTop extends BaseServerInfo {

    private static final String C_DEVICE = "c_device";
    private static final String N_TPS = "n_tps";
    private static final String N_KB_READ_S = "n_kb_read_s";
    private static final String N_KB_WRTN_S = "n_kb_wrtn_s";
    private static final String N_KB_READ = "n_kb_read";
    private static final String N_KB_WRTN = "n_kb_wrtn";

    public final String c_device;
    public final float n_tps;
    public final float n_kb_read_s;
    public final float n_kb_wrtn_s;
    public final float n_kb_read;
    public final float n_kb_wrtn;

    public SingleIoTop(JsonObject object) {
        super(object);
        c_device = JsonUtil.getString(object, C_DEVICE);
        n_tps = JsonUtil.getFloat(object, N_TPS);
        n_kb_read_s = JsonUtil.getFloat(object, N_KB_READ_S);
        n_kb_wrtn_s = JsonUtil.getFloat(object, N_KB_WRTN_S);
        n_kb_read = JsonUtil.getFloat(object, N_KB_READ);
        n_kb_wrtn = JsonUtil.getFloat(object, N_KB_WRTN);
    }

    protected String getInfoString() {
        if (n_tps == 0f && n_kb_read_s == 0f && n_kb_wrtn_s == 0f && n_kb_read == 0f && n_kb_wrtn == 0f) {
            return "";
        }
        return C_DEVICE + " - " + c_device +
                N_TPS + " - " + n_tps + ";\t\t" +
                N_KB_READ_S + " - " + n_kb_read_s + ";\t\t" +
                N_KB_WRTN_S + " - " + n_kb_wrtn_s + ";\t\t" +
                N_KB_READ + " - " + n_kb_read + ";\t\t" +
                N_KB_WRTN + " - " + n_kb_wrtn;
    }
}

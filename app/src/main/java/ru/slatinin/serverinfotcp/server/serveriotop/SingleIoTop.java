package ru.slatinin.serverinfotcp.server.serveriotop;

import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.server.BaseServerInfo;
import ru.slatinin.serverinfotcp.server.JsonUtil;

public class SingleIoTop extends BaseServerInfo {

    protected static final String C_DEVICE = "c_device";
    protected static final String N_TPS = "n_tps";
    protected static final String N_KB_READ_S = "n_kb_read_s";
    protected static final String N_KB_WRTN_S = "n_kb_wrtn_s";
    protected static final String N_KB_READ = "n_kb_read";
    protected static final String N_KB_WRTN = "n_kb_wrtn";

    public final String c_device;
    public final float n_tps;
    public final float n_kb_read_s;
    public final float n_kb_wrtn_s;
    public final long n_kb_read;
    public final long n_kb_wrtn;
    public final List<String> serverIoTopStringValues;
    public boolean isMoreThenOne = false;

    public SingleIoTop(JsonObject object) {
        super(object);
        serverIoTopStringValues = new ArrayList<>();
        c_device = JsonUtil.getString(object, C_DEVICE);
        serverIoTopStringValues.add(c_device);

        n_tps = JsonUtil.getFloat(object, N_TPS);
        serverIoTopStringValues.add(String.valueOf(n_tps));

        n_kb_read_s = JsonUtil.getFloat(object, N_KB_READ_S);
        serverIoTopStringValues.add(String.valueOf(n_kb_read_s));

        n_kb_wrtn_s = JsonUtil.getFloat(object, N_KB_WRTN_S);
        serverIoTopStringValues.add(String.valueOf(n_kb_wrtn_s));

        n_kb_read = JsonUtil.getLong(object, N_KB_READ);
        double read = (double) n_kb_read / 1024 / 1024;
        serverIoTopStringValues.add(formatDouble(read));

        n_kb_wrtn = JsonUtil.getLong(object, N_KB_WRTN);
        double write = (double) n_kb_wrtn / 1024 / 1024;
        serverIoTopStringValues.add(formatDouble(write));
    }

    protected List<String> getServerIoTopStringValues() {
        return serverIoTopStringValues;

    }

    private String formatDouble(double num){
        String readString = "";
        if (num > 1) {
            NumberFormat readFormatter = new DecimalFormat("#0.00");
            readString = readFormatter.format(num);
            isMoreThenOne = true;
        }
        return readString;
    }
}

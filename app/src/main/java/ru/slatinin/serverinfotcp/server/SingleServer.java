package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverdf.ServerDFObjectKeeper;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopObjectKeeper;
import ru.slatinin.serverinfotcp.server.servernet.ServerNetObjectKeeper;
import ru.slatinin.serverinfotcp.server.servernetlog.ServerNetLogObjectKeeper;
import ru.slatinin.serverinfotcp.server.serverpsql.ServerPsqlObjectListKeeper;
import ru.slatinin.serverinfotcp.server.servertop.ServerTOP;

public class SingleServer {

    public static final String NET = "net";
    public static final String NET_LOG = "net-log";
    public static final String TOP = "top";
    public static final String IOTOP = "iotop";
    public static final String DF = "df";
    public static final String PSQL = "psql";

    public final String ip;
    public final ServerNetLogObjectKeeper serverNetLogObjectKeeper;
    public final ServerDFObjectKeeper serverDFObjectKeeper;
    public final ServerIoTopObjectKeeper serverIoTopObjectKeeper;
    public final ServerTOP serverTOP;
    public final ServerNetObjectKeeper serverNetObjectKeeper;
    public final ServerPsqlObjectListKeeper serverPsqlObjectListKeeper;
    public final List<String> dataInfoList;

    public volatile long time;
    public volatile String dataInfo;

    public SingleServer(String ip, String dataInfo) {
        this.ip = ip;
        this.time = System.currentTimeMillis();
        this.dataInfo = dataInfo;
        serverDFObjectKeeper = new ServerDFObjectKeeper();
        serverIoTopObjectKeeper = new ServerIoTopObjectKeeper();
        serverPsqlObjectListKeeper = new ServerPsqlObjectListKeeper();
        serverTOP = new ServerTOP();
        serverNetObjectKeeper = new ServerNetObjectKeeper();
        serverNetLogObjectKeeper = new ServerNetLogObjectKeeper();
        dataInfoList = Collections.synchronizedList(new ArrayList<>());
        dataInfoList.add(TOP);
        dataInfoList.add(NET);
        dataInfoList.add(NET_LOG);
        dataInfoList.add(PSQL);
    }

    public boolean needCall(String dataInfo) {
        if (dataInfoList.size() == 0) {
            return false;
        }
        if (dataInfoList.contains(dataInfo)) {
            dataInfoList.remove(dataInfo);
            return true;
        }
        return false;
    }

    public void updateDF(JsonObject[] objects) {
        dataInfo = DF;
        serverDFObjectKeeper.update(objects);
    }

    public void updateIoTop(JsonObject[] objects) {
        dataInfo = IOTOP;
        serverIoTopObjectKeeper.update(objects);
    }

    public boolean updatePsql(JsonObject[] objects) {
        dataInfo = PSQL;
        serverPsqlObjectListKeeper.updatePsqlKeeper(objects);
        serverPsqlObjectListKeeper.calculateDiff();
        return needCall(PSQL);
    }

    public boolean updateTop(JsonObject[] objects) {
        dataInfo = TOP;
        serverTOP.update(objects);
        return needCall(TOP);
    }

    public boolean updateNet(JsonObject[] objects) {
        dataInfo = NET;
        serverNetObjectKeeper.update(objects);
        return needCall(NET);
    }

    public boolean updateNetLog(JsonObject[] objects) {
        dataInfo = NET_LOG;
        serverNetLogObjectKeeper.update(objects);
        serverNetLogObjectKeeper.calculateDiff();
        return needCall(NET_LOG);
    }
}

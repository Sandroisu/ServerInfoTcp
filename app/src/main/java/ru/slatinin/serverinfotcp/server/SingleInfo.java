package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverdf.ServerDFList;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopList;
import ru.slatinin.serverinfotcp.server.servertop.ServerCommon;
import ru.slatinin.serverinfotcp.server.servertop.ServerTOP;

public class SingleInfo {

    public static final String NET = "net";
    public static final String TOP = "top";
    public static final String IOTOP = "iotop";
    public static final String DF = "df";
    public static final String PSQL = "psql";

    public final String ip;
    public long time;
    public String dataInfo;
    private final List<ServerNET> serverNETList;
    private ServerNET lastServerNET;
    private ServerDFList serverDFList;
    private ServerIoTopList serverIoTopList;
    private ServerTOP serverTOP;
    private List<ServerPSQL> tempPSQLList;
    private final List<List<ServerPSQL>> serverPsqlLists;
    private final List<ServerCommon> serverCommonList;

    public SingleInfo(String ip, String dataInfo) {
        this.ip = ip;
        this.dataInfo = dataInfo;
        this.time = System.currentTimeMillis();
        serverNETList = new ArrayList<>();
        serverCommonList = new ArrayList<>();
        serverPsqlLists = new ArrayList<>();
    }

    public void updateServerInfo(SingleInfo info, String dataInfo) {
        this.dataInfo = dataInfo;
        this.time = info.time;
        switch (dataInfo) {
            case NET:
                lastServerNET = info.lastServerNET;
                if (serverNETList.size() > 59) {
                    serverNETList.remove(0);
                }
                serverNETList.add(info.lastServerNET);
                break;
            case TOP:
                serverTOP = info.serverTOP;
                if (serverCommonList.size() > 59) {
                    serverCommonList.remove(0);
                }
                serverCommonList.add(info.serverTOP.serverCommon);
                break;
            case DF:
                serverDFList = info.serverDFList;
                break;
            case PSQL:
                if (serverPsqlLists.size()>9){
                    serverPsqlLists.remove(0);
                }
                serverPsqlLists.add(info.getTempPSQLList());
                break;
            case IOTOP:
                serverIoTopList = info.serverIoTopList;
                break;
        }
    }

    public void init(String dataInfo, JsonObject... object) {
        switch (dataInfo) {
            case NET:
                lastServerNET = new ServerNET(object[0]);
                break;
            case TOP:
                serverTOP = new ServerTOP(object[0]);
                break;
            case DF:
                serverDFList = new ServerDFList(object);
                break;
            case PSQL:
                tempPSQLList = new ArrayList<>();
                for (JsonObject obj:object) {
                    tempPSQLList.add(new ServerPSQL(obj));
                }
                break;
            case IOTOP:
                serverIoTopList = new ServerIoTopList(object);
                break;
        }
    }

    public ServerDFList getServerDFList() {
        return serverDFList;
    }

    public ServerNET getLastServerNET() {
        return lastServerNET;
    }

    public ServerTOP getServerTOP() {
        return serverTOP;
    }

    public List<ServerNET> getServerNETList() {
        return serverNETList;
    }

    public List<ServerCommon> getServerCommonList() {
        return serverCommonList;
    }

    public ServerIoTopList getServerIoTopList() {
        return serverIoTopList;
    }

    public List<ServerPSQL> getTempPSQLList() {
        return tempPSQLList;
    }

    public List<List<ServerPSQL>> getServerPsqlLists() {
        return serverPsqlLists;
    }

    public boolean hasValues() {
        boolean hasValues = false;
        if (getServerDFList() != null) {
            hasValues = true;
        }
        if (getLastServerNET() != null) {
            hasValues = true;
        }
        if (getServerTOP() != null) {
            hasValues = true;
        }
        if (getServerNETList().size() > 0) {
            hasValues = true;
        }
        if (getServerCommonList().size() > 0) {
            hasValues = true;
        }
        return hasValues;
    }

}

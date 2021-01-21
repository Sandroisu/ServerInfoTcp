package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverdf.ServerDFList;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopList;
import ru.slatinin.serverinfotcp.server.servertop.ServerCommon;
import ru.slatinin.serverinfotcp.server.servertop.ServerTOP;

public class SingleInfo {

    public static final String NET = "net";
    public static final String NET_LOG = "net-log";
    public static final String TOP = "top";
    public static final String IOTOP = "iotop";
    public static final String DF = "df";
    public static final String PSQL = "psql";

    public final String ip;
    public long time;
    public String dataInfo;
    private ServerNET lastServerNET;
    private final List<ServerNET> serverNETList;
    private ServerNetLog lastServerNetLog;
    private final List<ServerNetLog> serverNetLogList;
    private ServerDFList serverDFList;
    private ServerIoTopList serverIoTopList;
    private ServerTOP serverTOP;
    private List<ServerPSQL> tempPSQLList;
    private final List<List<ServerPSQL>> serverPsqlLists;
    private final List<ServerCommon> serverCommonList;
    private final List<String> dataInfoList;

    public SingleInfo(String ip, String dataInfo) {
        this.ip = ip;
        this.dataInfo = dataInfo;
        this.time = System.currentTimeMillis();
        serverNETList = new ArrayList<>();
        serverCommonList = new ArrayList<>();
        serverPsqlLists = new ArrayList<>();
        serverNetLogList = new ArrayList<>();
        dataInfoList = new ArrayList<>();
        dataInfoList.add(TOP);
        dataInfoList.add(NET);
        dataInfoList.add(NET_LOG);
        dataInfoList.add(PSQL);
    }

    public void updateServerInfo(SingleInfo info, String dataInfo) {
        this.dataInfo = dataInfo;
        this.time = info.time;
        switch (dataInfo) {
            case NET:
                if (info.serverNETList.size() > 0 && serverNETList.size() < 10) {
                    List<ServerNET> temp = info.serverNETList;
                    temp.addAll(serverNETList);
                    serverNETList.clear();
                    serverNETList.addAll(temp);
                } else {
                    lastServerNET = info.lastServerNET;
                    if (serverNETList.size() > 59) {
                        serverNETList.remove(0);
                    }
                    serverNETList.add(info.lastServerNET);
                }
                break;
            case TOP:
                serverTOP = info.serverTOP;
                if (info.serverCommonList.size() > 0 && serverCommonList.size() < 10) {
                    List<ServerCommon> temp = info.serverCommonList;
                    Collections.reverse(temp);
                    temp.addAll(serverCommonList);
                    serverCommonList.clear();
                    serverCommonList.addAll(temp);
                } else {
                    if (serverCommonList.size() > 59) {
                        serverCommonList.remove(0);
                    }
                    serverCommonList.add(info.serverTOP.serverCommon);
                }
                break;
            case DF:
                serverDFList = info.serverDFList;
                break;
            case PSQL:
                if (serverPsqlLists.size() > 9) {
                    serverPsqlLists.remove(0);
                }
                serverPsqlLists.add(info.getTempPSQLList());
                break;
            case IOTOP:
                serverIoTopList = info.serverIoTopList;
                break;
            case NET_LOG:
                lastServerNetLog = info.lastServerNetLog;
                if (serverNetLogList.size() > 59) {
                    serverNetLogList.remove(0);
                }
                serverNetLogList.add(info.lastServerNetLog);
                break;
        }
    }

    public void init(String dataInfo, JsonObject... object) {
        switch (dataInfo) {
            case NET:
                lastServerNET = new ServerNET(object[0]);
                if (object.length > 1) {
                    for (JsonObject netObject : object) {
                        ServerNET sNet = new ServerNET(netObject);
                        serverNETList.add(sNet);
                        Collections.reverse(serverNETList);
                    }
                }
                break;
            case TOP:
                if (object.length > 1) {
                    for (JsonObject jsonObject : object) {
                        ServerTOP st = new ServerTOP(jsonObject);
                        serverCommonList.add(st.serverCommon);
                    }
                }
                serverTOP = new ServerTOP(object[0]);
                break;
            case DF:
                serverDFList = new ServerDFList(object);
                break;
            case PSQL:
                tempPSQLList = new ArrayList<>();
                for (JsonObject obj : object) {
                    tempPSQLList.add(new ServerPSQL(obj));
                }
                break;
            case IOTOP:
                serverIoTopList = new ServerIoTopList(object);
                break;
            case NET_LOG:
                lastServerNetLog = new ServerNetLog(object[0]);
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

    public List<ServerNetLog> getServerNetLogList() {
        return serverNetLogList;
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
        if (getServerNetLogList().size() > 0) {
            hasValues = true;
        }
        if (getServerIoTopList() != null) {
            hasValues = true;
        }
        if (getServerPsqlLists().size() > 0) {
            hasValues = true;
        }
        return hasValues;
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

}

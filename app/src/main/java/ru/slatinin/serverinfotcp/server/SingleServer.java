package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverdf.ServerDFList;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopList;
import ru.slatinin.serverinfotcp.server.serverpsql.ServerPsql;
import ru.slatinin.serverinfotcp.server.servertop.ServerCommon;
import ru.slatinin.serverinfotcp.server.servertop.ServerTOP;

public class SingleServer {

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
    private ServerPsql[] tempPsqlArray;
    private final List<List<ServerPsql>> serverPsqlLists;
    private final List<ServerCommon> serverCommonList;
    private final List<String> dataInfoList;

    private boolean firstPsql = true;
    public boolean firstCall = true;
    private int psqlCapacity;

    public SingleServer(String ip, String dataInfo) {
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

    public void updateServerInfo(SingleServer info, String dataInfo) {
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
                    serverTOP = info.serverTOP;
                    serverCommonList.add(info.serverTOP.serverCommon);
                }
                break;
            case DF:
                serverDFList = info.serverDFList;
                break;
            case PSQL:
                if (firstPsql) {
                    psqlCapacity = info.getTempPsqlArray().length;
                    firstPsql = false;
                }
                if (info.getTempPsqlArray().length > psqlCapacity && serverPsqlLists.size() < 3 && info.getTempPsqlArray().length / psqlCapacity < 10) {
                    ServerPsql[] list = info.getTempPsqlArray();
                    List<List<ServerPsql>> temp = new ArrayList<>(serverPsqlLists);
                    serverPsqlLists.clear();
                    for (int i = 0; i < list.length; i += psqlCapacity) {
                        if (i + psqlCapacity < list.length) {
                            ServerPsql[] toBeAdded = Arrays.copyOfRange(list, i, i + psqlCapacity);
                            ArrayList<ServerPsql> toBeAddedList = new ArrayList<>(Arrays.asList(toBeAdded));
                            Collections.reverse(toBeAddedList);
                            serverPsqlLists.add(toBeAddedList);
                        }
                    }
                    serverPsqlLists.addAll(temp);
                } else {
                    if (serverPsqlLists.size() > 9) {
                        serverPsqlLists.remove(0);
                    }
                    serverPsqlLists.add(Arrays.asList(info.getTempPsqlArray()));
                }
                if (serverPsqlLists.size() > 0)
                    for (int i = 0; i < serverPsqlLists.get(0).size(); i++) {
                        for (int j = 0; j < serverPsqlLists.size()-1; j++) {
                            serverPsqlLists.get(j + 1).get(i).calculateXactCommit(serverPsqlLists.get(j).get(i).n_xact_commit);
                        }
                    }
                break;
            case IOTOP:
                serverIoTopList = info.serverIoTopList;
                break;
            case NET_LOG:
                if (info.serverNetLogList.size() > 0 && serverNetLogList.size() < 10) {
                    List<ServerNetLog> temp = info.serverNetLogList;
                    temp.addAll(serverNetLogList);
                    serverNetLogList.clear();
                    serverNetLogList.addAll(temp);
                } else {
                    lastServerNetLog = info.lastServerNetLog;
                    if (serverNetLogList.size() > 59) {
                        serverNetLogList.remove(0);
                    }
                    serverNetLogList.add(info.lastServerNetLog);
                }
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
                tempPsqlArray = new ServerPsql[object.length];
                for (int i = 0; i < object.length; i++) {
                    tempPsqlArray[i] = (new ServerPsql(object[i]));
                }
                break;
            case IOTOP:
                serverIoTopList = new ServerIoTopList(object);
                break;
            case NET_LOG:
                if (object.length > 1) {
                    for (JsonObject netObject : object) {
                        ServerNetLog sNet = new ServerNetLog(netObject);
                        serverNetLogList.add(sNet);
                        Collections.reverse(serverNetLogList);
                    }
                }
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

    public ServerPsql[] getTempPsqlArray() {
        return tempPsqlArray;
    }

    public List<List<ServerPsql>> getServerPsqlLists() {
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

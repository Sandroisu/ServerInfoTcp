package ru.slatinin.serverinfotcp.server.servertop;

import android.icu.lang.UScript;

import com.google.gson.JsonObject;

import ru.slatinin.serverinfotcp.server.JsonUtil;

public class ServerProcesses {
    private static final String PID = "pid";
    private static final String USER = "user";
    private static final String CPU = "cpu";
    private static final String MEM = "mem";
    private static final String COMMAND = "command";

    public final String pid;
    public final String user;
    public final float cpu;
    public final float mem;
    public final String command;

    public ServerProcesses(JsonObject jsonObject){
       pid = JsonUtil.getString(jsonObject, PID);
        user = JsonUtil.getString(jsonObject, USER);
        cpu = JsonUtil.getFloat(jsonObject, CPU);
        mem = JsonUtil.getFloat(jsonObject, MEM);
        command = JsonUtil.getString(jsonObject, COMMAND);
    }

    public static String[] getNames(){
        return new String[]{PID, USER, CPU, MEM, COMMAND};
    }
}

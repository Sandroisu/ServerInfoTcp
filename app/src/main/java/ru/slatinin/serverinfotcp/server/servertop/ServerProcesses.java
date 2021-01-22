package ru.slatinin.serverinfotcp.server.servertop;

import android.icu.lang.UScript;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

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
    public boolean isNotZero = false;
    public final List<String> serverProcessesStringValues;

    public ServerProcesses(JsonObject jsonObject) {
        serverProcessesStringValues = new ArrayList<>();
        pid = JsonUtil.getString(jsonObject, PID);
        serverProcessesStringValues.add(pid);
        user = JsonUtil.getString(jsonObject, USER);
        serverProcessesStringValues.add(user);
        cpu = JsonUtil.getFloat(jsonObject, CPU);
        serverProcessesStringValues.add(String.valueOf(cpu));
        mem = JsonUtil.getFloat(jsonObject, MEM);
        serverProcessesStringValues.add(String.valueOf(mem));
        if (mem > 0 || cpu > 0) {
            isNotZero = true;
        }
        command = JsonUtil.getString(jsonObject, COMMAND);
        serverProcessesStringValues.add(command);
    }

    public static String[] getNames() {
        return new String[]{PID, USER, CPU, MEM, COMMAND};
    }
}

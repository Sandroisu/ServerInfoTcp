package ru.slatinin.serverinfotcp.server.servertop;

import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ServerTOP {

    private final String TOP = "top";
    private final String TASKS = "tasks";
    private final String CPU = "cpu";
    private final String MEM = "mem";
    private final String SWAP = "swap";

    public ServerCommon serverCommon;
    public ServerTasks serverTasks;
    public ServerSwap serverSWAP;
    public ServerMem serverMem;
    public ServerCpu serverCPU;

    public ServerTOP(JsonObject object) {
        serverCommon = new ServerCommon(object.get(TOP).getAsJsonObject());
        serverTasks = new ServerTasks(object.get(TASKS).getAsJsonObject());
        serverCPU = new ServerCpu(object.get(CPU).getAsJsonObject());
        serverMem = new ServerMem(object.get(MEM).getAsJsonObject());
        serverSWAP = new ServerSwap(object.get(SWAP).getAsJsonObject());
    }

    public String getInfo() {
        StringBuilder info = new StringBuilder();
        Field[] commonFields = serverCommon.getClass().getFields();
            info.append(TOP + ":\n");
        for (Field field : commonFields) {
            info.append(field.getName()).append(": ").append(serverCommon.getValueByName(field.getName())).append("; ");
        }
        /*
        info.append("\n");
        info.append(TASKS + ":\n");
        Field[] tasksFields = serverTasks.getClass().getFields();
        for (Field field : tasksFields) {
            info.append(field.getName()).append(": ").append(serverTasks.getValueByName(field.getName())).append("; ");
        }
        info.append("\n");
        info.append(SWAP + ":\n");
        Field[] swapFields = serverSWAP.getClass().getFields();
        for (Field field : swapFields) {
            info.append(field.getName()).append(": ").append(serverSWAP.getValueByName(field.getName())).append("; ");
        }
        info.append("\n");
        info.append(MEM + ":\n");
        Field[] memFields = serverMem.getClass().getFields();
        for (Field field : memFields) {
            info.append(field.getName()).append(": ").append(serverMem.getValueByName(field.getName())).append("; ");
        }
        info.append("\n");
        info.append(CPU + ":\n");
        Field[] cpuFields = serverCPU.getClass().getFields();
        for (Field field : cpuFields) {
            info.append(field.getName()).append(": ").append(serverCPU.getValueByName(field.getName())).append("; ");
        }
         */
        return info.toString();
    }
}

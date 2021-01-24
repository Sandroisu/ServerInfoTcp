package ru.slatinin.serverinfotcp.server.servertop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverutil.JsonUtil;

public class ServerTOP {

    private final String TOP = "top";
    private final String TASKS = "tasks";
    private final String CPU = "cpu";
    private final String MEM = "mem";
    private final String SWAP = "swap";
    private final String JB_PROCESSES = "jb_processes";
    private final String PROCESSES = "processes";

    public final ServerTasks serverTasks;
    public final ServerSwap serverSWAP;
    public final ServerMem serverMem;
    public final ServerCpu serverCPU;
    public final List<ServerProcesses> serverProcesses;
    public final List<ServerCommon> serverCommonList;
    public ServerCommon lastServerCommon;

    public ServerTOP() {
        serverTasks = new ServerTasks();
        serverCPU = new ServerCpu();
        serverMem = new ServerMem();
        serverSWAP = new ServerSwap();
        serverProcesses = new ArrayList<>();
        serverCommonList = new ArrayList<>();
    }

    public void update(JsonObject[] objects) {
        if (objects.length == 1) {
            serverTasks.update(JsonUtil.getJsonObject(objects[0], TASKS));
            serverCPU.update(JsonUtil.getJsonObject(objects[0], CPU));
            serverMem.update(JsonUtil.getJsonObject(objects[0], MEM));
            serverSWAP.update(JsonUtil.getJsonObject(objects[0], SWAP));
            lastServerCommon = new ServerCommon(JsonUtil.getJsonObject(objects[0], TOP));
            if (serverCommonList.size() > 59) {
                serverCommonList.remove(0);
            }
            serverCommonList.add(new ServerCommon(JsonUtil.getJsonObject(objects[0], TOP)));
            serverProcesses.clear();
            JsonArray array;
            if (objects[0].has(JB_PROCESSES)) {
                array = JsonUtil.getJsonArray(objects[0], JB_PROCESSES);
            } else {
                array = JsonUtil.getJsonArray(objects[0], PROCESSES);
            }
            JsonArray innerArray;
            if (array.isJsonArray() && array.size() > 0 && array.get(0).isJsonArray()) {
                innerArray = array.get(0).getAsJsonArray();
            } else {
                innerArray = array;
            }
            for (int i = 1; i < innerArray.size(); i++) {
                ServerProcesses serverProcess = new ServerProcesses(innerArray.get(i).getAsJsonObject());
                serverProcesses.add(serverProcess);
            }
            return;
        }
        if (serverCommonList.size() < 9) {
            List<ServerCommon> temp = new ArrayList<>(serverCommonList.size());
            for (int i = 0; i < serverCommonList.size(); i++) {
                temp.add(new ServerCommon(serverCommonList.get(i)));
            }
            for (int i = 0; i < objects.length; i++) {
                if (i<temp.size()) {
                    serverCommonList.set(i, new ServerCommon(JsonUtil.getJsonObject(objects[0], TOP)));
                }else {
                    serverCommonList.add(new ServerCommon(JsonUtil.getJsonObject(objects[0], TOP)));
                }
            }
            Collections.reverse(serverCommonList);
            serverCommonList.addAll(temp);
            temp.clear();
        }
    }

}



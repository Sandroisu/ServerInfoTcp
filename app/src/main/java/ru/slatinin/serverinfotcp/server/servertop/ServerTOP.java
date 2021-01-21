package ru.slatinin.serverinfotcp.server.servertop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.server.JsonUtil;

public class ServerTOP {

    private final String TOP = "top";
    private final String TASKS = "tasks";
    private final String CPU = "cpu";
    private final String MEM = "mem";
    private final String SWAP = "swap";
    private final String JB_PROCESSES = "jb_processes";
    private final String PROCESSES = "processes";

    public ServerCommon serverCommon;
    public ServerTasks serverTasks;
    public ServerSwap serverSWAP;
    public ServerMem serverMem;
    public ServerCpu serverCPU;
    public List<ServerProcesses> serverProcesses;

    public ServerTOP(JsonObject object) {
        serverCommon = new ServerCommon(JsonUtil.getJsonObject(object, TOP));
        serverTasks = new ServerTasks(JsonUtil.getJsonObject(object, TASKS));
        serverCPU = new ServerCpu(JsonUtil.getJsonObject(object, CPU));
        serverMem = new ServerMem(JsonUtil.getJsonObject(object, MEM));
        serverSWAP = new ServerSwap(JsonUtil.getJsonObject(object, SWAP));
        serverProcesses = new ArrayList<>();
        JsonArray array;
        if (object.has(JB_PROCESSES)) {
            array = JsonUtil.getJsonArray(object, JB_PROCESSES);
        } else {
            array = JsonUtil.getJsonArray(object, PROCESSES);
        }
        JsonArray innerArray;
        if (array.isJsonArray() && array.size() > 0 && array.get(0).isJsonArray()) {
            innerArray = array.get(0).getAsJsonArray();
        } else {
            innerArray = array;
        }
        for (int i = 0; i < innerArray.size(); i++) {
            ServerProcesses serverProcess = new ServerProcesses(innerArray.get(i).getAsJsonObject());
            serverProcesses.add(serverProcess);
        }
    }
}



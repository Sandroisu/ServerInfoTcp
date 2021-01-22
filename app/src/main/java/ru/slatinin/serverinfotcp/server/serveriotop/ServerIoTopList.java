package ru.slatinin.serverinfotcp.server.serveriotop;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerIoTopList {


    public final List<SingleIoTop> serverIoTopList;
    public static final String[] columnNames = new String[]{"name", "tps", "read, Kb/s", "write, Kb/s", "read, Gb", "write, Gb"};

    public ServerIoTopList(JsonObject[] object) {
        serverIoTopList = new ArrayList<>();
        for (JsonObject obj : object) {
            SingleIoTop singleIoTop = new SingleIoTop(obj);
            serverIoTopList.add(singleIoTop);
        }
        if (object.length > 0 && object[0].has("dx_created")) {
            Collections.reverse(serverIoTopList);
        }
    }

}

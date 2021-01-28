package ru.slatinin.serverinfotcp.server.serveriotop;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerIoTopObjectKeeper {

    public final List<SingleIoTop> serverIoTopList;
    public final List<String> tempNames;
    public static final String[] columnNames = new String[]{"name", "tps", "read, Kb/s", "write, Kb/s", "read, Gb", "write, Gb"};
    public boolean firstIoTop;

    public ServerIoTopObjectKeeper() {
        firstIoTop = true;
        serverIoTopList = Collections.synchronizedList(new ArrayList<>());
        tempNames = Collections.synchronizedList(new ArrayList<>());
    }

    public void update(JsonObject[] objects) {
        if (serverIoTopList.size() == 0) {
            for (JsonObject obj : objects) {
                SingleIoTop singleIoTop = new SingleIoTop(obj);
                if (!tempNames.contains(singleIoTop.c_device)){
                    tempNames.add(singleIoTop.c_device);
                    serverIoTopList.add(singleIoTop);
                }
            }
            if (firstIoTop){
                firstIoTop = false;
                Collections.reverse(serverIoTopList);
            }
            return;
        }
        if (objects.length == serverIoTopList.size()) {
            for (int i = 0; i < objects.length; i++) {
                serverIoTopList.set(i, new SingleIoTop(objects[i]));
            }
        } else {
            serverIoTopList.clear();
            update(objects);
        }
    }

}

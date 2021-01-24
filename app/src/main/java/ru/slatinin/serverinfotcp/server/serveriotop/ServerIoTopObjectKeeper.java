package ru.slatinin.serverinfotcp.server.serveriotop;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverdf.SingleServerDF;

public class ServerIoTopObjectKeeper {

    public final List<SingleIoTop> serverIoTopList;
    public static final String[] columnNames = new String[]{"name", "tps", "read, Kb/s", "write, Kb/s", "read, Gb", "write, Gb"};
    public boolean firstIoTop;

    public ServerIoTopObjectKeeper() {
        firstIoTop = true;
        serverIoTopList = new ArrayList<>();
    }

    public void update(JsonObject[] objects) {
        if (serverIoTopList.size() == 0) {
            for (JsonObject obj : objects) {
                serverIoTopList.add(new SingleIoTop(obj));
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

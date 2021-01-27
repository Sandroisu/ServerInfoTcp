package ru.slatinin.serverinfotcp.server.servernet;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerNetObjectKeeper {

    public final List<ServerNet> serverNetList;
    public volatile ServerNet lastServerNet;

    public ServerNetObjectKeeper() {
        serverNetList = Collections.synchronizedList(new ArrayList<>());
    }

    public void update(JsonObject[] objects) {
        if (objects.length == 1) {
            if (serverNetList.size() > 59) {
                serverNetList.remove(0);
            }
            lastServerNet = new ServerNet(objects[0]);
            serverNetList.add(new ServerNet(objects[0]));
            return;
        }
        if (serverNetList.size() > 0 && serverNetList.size() < 10) {
            List<ServerNet> temp = new ArrayList<>(serverNetList.size());
            for (int i = 0; i < serverNetList.size(); i++) {
                temp.add(new ServerNet(serverNetList.get(i)));
            }
            for (int i = 0; i < objects.length; i++) {
                if (i < temp.size()) {
                    serverNetList.set(i, new ServerNet(objects[i]));
                } else {
                    serverNetList.add(new ServerNet(objects[i]));
                }
            }
            Collections.reverse(serverNetList);
            serverNetList.addAll(temp);
            temp.clear();
        }
    }
}

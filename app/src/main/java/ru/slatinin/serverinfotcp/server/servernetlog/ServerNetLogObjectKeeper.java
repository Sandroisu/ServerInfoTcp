package ru.slatinin.serverinfotcp.server.servernetlog;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.slatinin.serverinfotcp.server.servernet.ServerNet;

public class ServerNetLogObjectKeeper {
    public final List<ServerNetLog> serverNetLogList;

    public ServerNetLogObjectKeeper() {
        serverNetLogList = Collections.synchronizedList(new ArrayList<>());
    }

    public void update(JsonObject[] objects) {
        if (objects.length == 1) {
            if (serverNetLogList.size() > 59) {
                serverNetLogList.remove(0);
            }
            serverNetLogList.add(new ServerNetLog(objects[0]));
            return;
        }
        if (serverNetLogList.size() < 10) {
            List<ServerNetLog> temp = new ArrayList<>(serverNetLogList.size());
            for (int i = 0; i < serverNetLogList.size(); i++) {
                temp.add(new ServerNetLog(serverNetLogList.get(i)));
            }
            for (int i = 0; i < objects.length; i++) {
                if (i < temp.size()) {
                    serverNetLogList.set(i, new ServerNetLog(objects[i]));
                } else {
                    serverNetLogList.add(new ServerNetLog(objects[i]));
                }
            }
            Collections.reverse(serverNetLogList);
            serverNetLogList.addAll(temp);
            temp.clear();
        }
    }

    public void calculateDiff(){
        if (serverNetLogList.size()<2){
            return;
        }
        for (int i = 1; i <serverNetLogList.size(); i++) {
            serverNetLogList.get(i).calculateValues(serverNetLogList.get(i-1).n_received,
                    serverNetLogList.get(i-1).n_sent);
        }
    }
}

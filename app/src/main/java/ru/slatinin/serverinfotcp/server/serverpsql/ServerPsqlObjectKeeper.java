package ru.slatinin.serverinfotcp.server.serverpsql;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerPsqlObjectKeeper {

    public final List<ServerPsql> serverPsqlList;

    public ServerPsqlObjectKeeper() {
        serverPsqlList = Collections.synchronizedList(new ArrayList<>());
    }

    public ServerPsqlObjectKeeper(ServerPsqlObjectKeeper keeper) {
        serverPsqlList = new ArrayList<>();
        for (ServerPsql serverPsql : keeper.serverPsqlList) {
            serverPsqlList.add(new ServerPsql(serverPsql));
        }
    }

    public void updateInnerKeeper(JsonObject[] jsonObjects, boolean invocation) {
        if (invocation) {
            for (JsonObject object : jsonObjects) {
                serverPsqlList.add(new ServerPsql(object));
            }
            return;
        }
        for (JsonObject object : jsonObjects) {
            serverPsqlList.add(new ServerPsql(object));
        }
        Collections.reverse(serverPsqlList);
    }
}

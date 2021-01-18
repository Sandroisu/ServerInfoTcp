package ru.slatinin.serverinfotcp.server.serverpsql;

import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.server.ServerPSQL;

public class ServerPsqlList {

    public final List<ServerPSQL> serverPsqlList;
    private final String datname;

    public ServerPsqlList(ServerPSQL serverPSQL) {
        serverPsqlList = new ArrayList<>();
        serverPsqlList.add(serverPSQL);
        datname = serverPSQL.c_datname;
    }

    public void addServerPsql(List<ServerPSQL>  list) {
        if (serverPsqlList.size()>9){
            removeFirst();
        }
        for (ServerPSQL serverPSQL:list) {
            if (serverPSQL.c_datname.equals(datname)){
                serverPsqlList.add(serverPSQL);
                break;
            }
        }
    }
    private void removeFirst(){
        serverPsqlList.remove(0);
    }
}

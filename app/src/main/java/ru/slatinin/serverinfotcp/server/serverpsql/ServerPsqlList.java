package ru.slatinin.serverinfotcp.server.serverpsql;

import java.util.ArrayList;
import java.util.List;

public class ServerPsqlList {

    public final List<ServerPsql> serverPsqlList;
    private final String datname;

    public ServerPsqlList(ServerPsql serverPSQL) {
        serverPsqlList = new ArrayList<>();
        datname = serverPSQL.c_datname;
        serverPsqlList.add(serverPSQL);
    }

    public void addServerPsql(List<ServerPsql>  list) {
        if (serverPsqlList.size()>9){
            removeFirst();
        }
        for (ServerPsql serverPSQL:list) {
            if (serverPSQL.c_datname.equals(datname)){
                serverPsqlList.add(serverPSQL);
            }
        }
    }

    public void addIfMatch(ServerPsql serverPsql){
        if (datname.equals(serverPsql.c_datname)){
            serverPsqlList.add(serverPsql);
        }
    }
    private void removeFirst(){
        serverPsqlList.remove(0);
    }
}

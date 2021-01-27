package ru.slatinin.serverinfotcp.server.serverpsql;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServerPsqlObjectListKeeper {

    public final List<ServerPsqlObjectKeeper> serverPsqlObjectKeeperList;
    private int psqlTabelsCount;

    public ServerPsqlObjectListKeeper() {
        psqlTabelsCount = 0;
        serverPsqlObjectKeeperList = Collections.synchronizedList(new ArrayList<>());
    }

    public void updatePsqlKeeper(JsonObject[] jsonObjects) {
        if (serverPsqlObjectKeeperList.size() == 0) {
            psqlTabelsCount = jsonObjects.length;
            ServerPsqlObjectKeeper single = new ServerPsqlObjectKeeper();
            single.updateInnerKeeper(jsonObjects, true);
            serverPsqlObjectKeeperList.add(single);
            return;
        }
        if (serverPsqlObjectKeeperList.size() > 8) {
            serverPsqlObjectKeeperList.remove(0);
        }
        if (jsonObjects.length == psqlTabelsCount) {
            ServerPsqlObjectKeeper single = new ServerPsqlObjectKeeper();
            single.updateInnerKeeper(jsonObjects, true);
            serverPsqlObjectKeeperList.add(single);
            return;
        }

        if (jsonObjects.length < psqlTabelsCount) {
            return;
        }
        int sizeToBeAdded = 8 - serverPsqlObjectKeeperList.size();
        int maxJsonLength = (sizeToBeAdded) * psqlTabelsCount;
        if (maxJsonLength <= psqlTabelsCount) {
            return;
        }
        if (jsonObjects.length > maxJsonLength) {
            jsonObjects = Arrays.copyOfRange(jsonObjects, 0, maxJsonLength);
        }
        List<ServerPsqlObjectKeeper> tempList = new ArrayList<>(serverPsqlObjectKeeperList.size());
        for (ServerPsqlObjectKeeper tempKeeper : serverPsqlObjectKeeperList) {
            tempList.add(new ServerPsqlObjectKeeper(tempKeeper));
        }
        serverPsqlObjectKeeperList.clear();
        int count = 0;
        for (int i = 0; i < sizeToBeAdded - 1; i++) {
            if (count + psqlTabelsCount < jsonObjects.length - 1) {
                JsonObject[] insertArray = Arrays.copyOfRange(jsonObjects, count, count + psqlTabelsCount);
                count += psqlTabelsCount;
                ServerPsqlObjectKeeper single = new ServerPsqlObjectKeeper();
                single.updateInnerKeeper(insertArray, false);
                serverPsqlObjectKeeperList.add(single);
            }
        }
        Collections.reverse(serverPsqlObjectKeeperList);
        serverPsqlObjectKeeperList.addAll(tempList);
        tempList.clear();
    }

    public void calculateDiff() {
        if (serverPsqlObjectKeeperList.size() < 2) {
            return;
        }
        for (int i = 0; i < serverPsqlObjectKeeperList.get(0).serverPsqlList.size(); i++) {
            for (int j = 1; j < serverPsqlObjectKeeperList.size(); j++) {
                serverPsqlObjectKeeperList.get(j).serverPsqlList.get(i).calculateXactCommit(serverPsqlObjectKeeperList.get(j - 1).serverPsqlList.get(i).n_xact_commit);
            }
        }
    }

    public ServerPsql[] getSinglePsqlLineData(int psqlListPosition) {
        ServerPsql[] serverPsqlArray = new ServerPsql[serverPsqlObjectKeeperList.size()];
        for (int i = 0; i < serverPsqlObjectKeeperList.size(); i++) {
            serverPsqlArray[i] = serverPsqlObjectKeeperList.get(i).serverPsqlList.get(psqlListPosition);
        }
        return serverPsqlArray;
    }

    public int getLinesCount() {
        return serverPsqlObjectKeeperList.get(0).serverPsqlList.size();
    }

    public String getLineName(int psqlListPosition) {
        return serverPsqlObjectKeeperList.get(0).serverPsqlList.get(psqlListPosition).c_datname;
    }
}

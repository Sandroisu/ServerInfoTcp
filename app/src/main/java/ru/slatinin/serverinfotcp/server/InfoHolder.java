package ru.slatinin.serverinfotcp.server;

import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.CallSqlQueryListener;

import static ru.slatinin.serverinfotcp.server.SingleServer.IOTOP;

public class InfoHolder {
    private final CallSqlQueryListener callSqlQueryListener;
    private final ArrayList<SingleServer> singleServerList;

    public InfoHolder(CallSqlQueryListener callSqlQueryListener) {
        this.callSqlQueryListener = callSqlQueryListener;
        singleServerList = new ArrayList<>();
    }

    public int updateOrAddInfo(SingleServer singleServer, String dataInfo, String address, String port) {
        boolean alreadyExists = false;
        int position = -1;
        for (int i = 0; i < singleServerList.size(); i++) {
            if (singleServerList.get(i).ip.equals(singleServer.ip)) {
                singleServerList.get(i).updateServerInfo(singleServer, dataInfo);
                if(singleServerList.get(i).firstCall){
                    singleServerList.get(i).firstCall = false;
                    callSqlQueryListener.onMustCallOldData(IOTOP, singleServer.ip);
                }
                alreadyExists = true;
                position = i;
                break;
            }
        }
        if (!alreadyExists && singleServerList.size() == 0) {
            callSqlQueryListener.onSaveAddressAndPort(address, port);
        }

        if (!alreadyExists && singleServer.hasValues()) {
            singleServerList.add(singleServer);
            position = singleServerList.size() - 1;
        }
        for (SingleServer sInfo : singleServerList) {
            if (sInfo.ip.equals(singleServer.ip) && sInfo.needCall(dataInfo)) {
                callSqlQueryListener.onMustCallOldData(dataInfo, singleServer.ip);
                break;
            }
        }
        return position;
    }

    public List<SingleServer> getSingleServerList() {
        return singleServerList;
    }

    public void clear() {
        singleServerList.clear();
    }

}
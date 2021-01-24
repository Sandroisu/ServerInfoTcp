package ru.slatinin.serverinfotcp.server;

import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.CallSqlQueryListener;

import static ru.slatinin.serverinfotcp.server.SingleServer.DF;
import static ru.slatinin.serverinfotcp.server.SingleServer.IOTOP;

public class InfoHolder {
    private final CallSqlQueryListener callSqlQueryListener;
    private final ArrayList<SingleServer> singleServerList;

    public InfoHolder(CallSqlQueryListener callSqlQueryListener) {
        this.callSqlQueryListener = callSqlQueryListener;
        singleServerList = new ArrayList<>();
    }

    public List<SingleServer> getSingleServerList() {
        return singleServerList;
    }

    public void clear() {
        singleServerList.clear();
    }

    public SingleServer getSingleServerByIp(String ip, String dataInfo) {
        for (int i = 0; i < singleServerList.size(); i++) {
            if (singleServerList.get(i).ip.equals(ip)) {
                singleServerList.get(i).time = System.currentTimeMillis();
                singleServerList.get(i).dataInfo = dataInfo;
                return singleServerList.get(i);
            }
        }
        SingleServer singleServer = new SingleServer(ip, dataInfo);
        singleServerList.add(singleServer);
        callSqlQueryListener.onMustCallOldData(IOTOP, ip);
        return singleServer;
    }


}
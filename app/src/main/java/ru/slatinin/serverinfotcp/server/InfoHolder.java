package ru.slatinin.serverinfotcp.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.slatinin.serverinfotcp.CallSqlQueryListener;
import static ru.slatinin.serverinfotcp.server.SingleServer.IOTOP;

public class InfoHolder {
    private final CallSqlQueryListener callSqlQueryListener;
    private final List<SingleServer> singleServerList;

    public InfoHolder(CallSqlQueryListener callSqlQueryListener) {
        this.callSqlQueryListener = callSqlQueryListener;
        singleServerList = Collections.synchronizedList(new ArrayList<>());
    }

    public List<SingleServer> getSingleServerList() {
        return singleServerList;
    }

    public void clear() {
        singleServerList.clear();
    }

    public int getSingleServerByIp(String ip, String dataInfo) {
        for (int i = 0; i < singleServerList.size(); i++) {
            if (singleServerList.get(i).ip.equals(ip)) {
                singleServerList.get(i).time = System.currentTimeMillis();
                singleServerList.get(i).dataInfo = dataInfo;
                return i;
            }
        }
        SingleServer singleServer = new SingleServer(ip, dataInfo);
        singleServerList.add(singleServer);
        callSqlQueryListener.onMustCallOldData(IOTOP, ip);
        callSqlQueryListener.firstTimeInserted(singleServerList.size()-1);
        return singleServerList.size()-1;
    }


}
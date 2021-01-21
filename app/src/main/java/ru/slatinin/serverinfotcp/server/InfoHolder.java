package ru.slatinin.serverinfotcp.server;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.CallSqlQueryListener;

import static ru.slatinin.serverinfotcp.server.SingleInfo.NET;
import static ru.slatinin.serverinfotcp.server.SingleInfo.NET_LOG;
import static ru.slatinin.serverinfotcp.server.SingleInfo.PSQL;
import static ru.slatinin.serverinfotcp.server.SingleInfo.TOP;

public class InfoHolder {
    private final CallSqlQueryListener callSqlQueryListener;
    private final ArrayList<SingleInfo> singleInfoList;
    private final ArrayList<String> dataInfoList;

    public InfoHolder(CallSqlQueryListener callSqlQueryListener) {
        this.callSqlQueryListener = callSqlQueryListener;
        singleInfoList = new ArrayList<>();
        dataInfoList = new ArrayList<>();
        dataInfoList.add(TOP);
        dataInfoList.add(NET);
        dataInfoList.add(NET_LOG);
        dataInfoList.add(PSQL);
    }

    public int updateOrAddInfo(SingleInfo singleInfo, String dataInfo, String address, String port) {
        boolean alreadyExists = false;
        int position = -1;
        for (int i = 0; i < singleInfoList.size(); i++) {
            if (singleInfoList.get(i).ip.equals(singleInfo.ip)) {
                singleInfoList.get(i).updateServerInfo(singleInfo, dataInfo);
                alreadyExists = true;
                position = i;
                break;
            }
        }
        if (!alreadyExists && singleInfoList.size() == 0) {
            callSqlQueryListener.onSaveAddressAndPort(address, port);
        }

        if (!alreadyExists && singleInfo.hasValues()) {
            singleInfoList.add(singleInfo);
            position = singleInfoList.size() - 1;
        }
        for (SingleInfo sInfo : singleInfoList) {
            if (sInfo.ip.equals(singleInfo.ip)&&sInfo.needCall(dataInfo)){
                callSqlQueryListener.onMustCallOldData(dataInfo, singleInfo.ip);
                break;
            }
        }
        return position;
    }

    public List<SingleInfo> getSingleInfoList() {
        return singleInfoList;
    }

    public void clear() {
        singleInfoList.clear();
    }

}
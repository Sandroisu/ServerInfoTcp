package ru.slatinin.serverinfotcp.server;

import java.util.ArrayList;
import java.util.List;

public class InfoHolder {

    private final ArrayList<SingleInfo> singleInfoList;

    public InfoHolder() {
        singleInfoList = new ArrayList<>();
    }

    public int updateOrAddInfo(SingleInfo singleInfo, String dataInfo) {
        boolean alreadyExists = false;
        int position = -1 ;
        for (int i = 0; i < singleInfoList.size(); i++) {
            if (singleInfoList.get(i).ip.equals(singleInfo.ip)) {
                singleInfoList.get(i).updateServerInfo(singleInfo, dataInfo);
                alreadyExists = true;
                position = i;
                break;
            }
        }
        if (!alreadyExists && singleInfo.hasValues()) {
            singleInfoList.add(singleInfo);
            position = singleInfoList.size() - 1;
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
package ru.slatinin.serverinfotcp.ui;

import ru.slatinin.serverinfotcp.server.InfoHolder;
import ru.slatinin.serverinfotcp.server.SingleInfo;

public interface OnTcpInfoReceived{
    void updateTcpInfo(SingleInfo info, String dataInfo, int position);
    void createTcpInfo(InfoHolder holder);
    void showError(String errorMessage);
}
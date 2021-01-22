
package ru.slatinin.serverinfotcp.ui;

import ru.slatinin.serverinfotcp.server.InfoHolder;
import ru.slatinin.serverinfotcp.server.SingleServer;

public interface OnTcpInfoReceived{
    void updateTcpInfo(SingleServer info, String dataInfo, int position);
    void createTcpInfo(InfoHolder holder);
    void showError(String errorMessage);
}
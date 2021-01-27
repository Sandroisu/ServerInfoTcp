
package ru.slatinin.serverinfotcp.ui;

import ru.slatinin.serverinfotcp.server.InfoHolder;

public interface OnTcpInfoReceived{
    void updateTcpInfo(int position);
    void createTcpInfo(InfoHolder holder);
    void showError(String errorMessage);
    void insertNewRvItem(int position);
}
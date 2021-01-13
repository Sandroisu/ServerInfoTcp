package ru.slatinin.serverinfotcp.ui;


import ru.slatinin.serverinfotcp.server.SingleInfo;

public interface OnTcpInfoReceived{
    void updateTcpInfo(SingleInfo info);
    void showError(String errorMessage);
}
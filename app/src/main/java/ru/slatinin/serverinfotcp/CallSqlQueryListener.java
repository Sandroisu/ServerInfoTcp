package ru.slatinin.serverinfotcp;

public interface CallSqlQueryListener {
    void onMustCallOldData(String dataInfo, String ip);
    void onSaveAddressAndPort(String address, String port);
}

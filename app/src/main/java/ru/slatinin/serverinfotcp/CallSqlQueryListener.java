package ru.slatinin.serverinfotcp;

public interface CallSqlQueryListener {
    void onMustCallOldData(String dataInfo, String ip);
}

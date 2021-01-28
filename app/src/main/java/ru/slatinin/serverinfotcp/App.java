package ru.slatinin.serverinfotcp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import ru.slatinin.serverinfotcp.server.InfoHolder;
import ru.slatinin.serverinfotcp.server.serverutil.JsonUtil;
import ru.slatinin.serverinfotcp.sevice.TcpClient;
import ru.slatinin.serverinfotcp.sevice.TcpService;
import ru.slatinin.serverinfotcp.ui.OnTcpInfoReceived;

import static ru.slatinin.serverinfotcp.server.SingleServer.DF;
import static ru.slatinin.serverinfotcp.server.SingleServer.IOTOP;
import static ru.slatinin.serverinfotcp.server.SingleServer.NET;
import static ru.slatinin.serverinfotcp.server.SingleServer.NET_LOG;
import static ru.slatinin.serverinfotcp.server.SingleServer.PSQL;
import static ru.slatinin.serverinfotcp.server.SingleServer.TOP;
import static ru.slatinin.serverinfotcp.ui.MainActivity.ADDRESS;
import static ru.slatinin.serverinfotcp.ui.MainActivity.BASE_URL;
import static ru.slatinin.serverinfotcp.ui.MainActivity.PORT;
import static ru.slatinin.serverinfotcp.ui.MainActivity.REPO;
import static ru.slatinin.serverinfotcp.ui.MainActivity.SHARED_PREFS;

public class App extends Application implements CallSqlQueryListener {
    private final String ARGS = "args";
    private final static String REPOS = "repos";

    private InfoHolder infoHolder;
    private TcpClient tcpClient;
    private ArrayList<OnTcpInfoReceived> listenersList;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(TcpService.NOTIFICATION_CHANEL_ID,
                    "TcpClientChannel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
        infoHolder = new InfoHolder(this);
        listenersList = new ArrayList<>();
        Intent intent = new Intent();
        intent.setClass(this, TcpService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public void connect(String address, String port) {
        if (tcpClient != null) {
            tcpClient.stopClient();
            infoHolder.clear();
            tcpClient = null;
            infoHolder = null;
            infoHolder = new InfoHolder(App.this);
            for (OnTcpInfoReceived listener : listenersList) {
                listener.createTcpInfo(infoHolder);
            }
        }
        Thread thread = new Thread(() -> {
            tcpClient = new TcpClient(address, port, new TcpClient.OnMessageReceivedListener() {
                @Override
                public synchronized void onServerMessageReceived(JsonObject[] objects, String ip, String dataInfo) {
                    if (dataInfo.equals(PSQL)){
                        for (OnTcpInfoReceived listener : listenersList) {
                            listener.showError(PSQL);
                        }
                    }
                    if (objects == null || objects.length == 0) {
                        return;
                    }
                    int position = -1;
                    saveAddressAndPort(address, port);
                    boolean needCallOldData = false;
                    switch (dataInfo) {
                        case ARGS:
                            saveRepo(JsonUtil.getString(objects[0], REPOS));
                            return;
                        case DF:
                            position = infoHolder.getSingleServerByIp(ip, dataInfo);
                            infoHolder.getSingleServerList().get(position).updateDF(objects);
                            break;
                        case IOTOP:
                            position = infoHolder.getSingleServerByIp(ip, dataInfo);
                            infoHolder.getSingleServerList().get(position).updateIoTop(objects);
                            break;
                        case PSQL:
                            position = infoHolder.getSingleServerByIp(ip, dataInfo);
                            needCallOldData = infoHolder.getSingleServerList().get(position).updatePsql(objects);
                            break;
                        case TOP:
                            position = infoHolder.getSingleServerByIp(ip, dataInfo);
                            needCallOldData = infoHolder.getSingleServerList().get(position).updateTop(objects);
                            break;
                        case NET:
                            position = infoHolder.getSingleServerByIp(ip, dataInfo);
                            needCallOldData = infoHolder.getSingleServerList().get(position).updateNet(objects);
                            break;
                        case NET_LOG:
                            position = infoHolder.getSingleServerByIp(ip, dataInfo);
                            needCallOldData = infoHolder.getSingleServerList().get(position).updateNetLog(objects);
                            break;
                    }
                    if (position >= 0) {
                        for (OnTcpInfoReceived listener : listenersList) {
                            listener.updateTcpInfo(position);
                        }
                    }
                    if (needCallOldData) {
                        onMustCallOldData(dataInfo, ip);
                    }
                }

                @Override
                public void onErrorOccurred(String message) {
                    for (OnTcpInfoReceived listener : listenersList) {
                        listener.showError(message);
                    }
                }
            });
            tcpClient.run();
        });
        thread.start();
    }

    private void saveAddressAndPort(String address, String port) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String prefAddress = sharedPreferences.getString(ADDRESS, "");
        StringBuilder sb = new StringBuilder();
        if (!prefAddress.isEmpty()) {
            String[] playlists = prefAddress.split("\\)\\(");
            for (String playlist : playlists) {
                if (!playlist.equals(address)) {
                    sb.append(playlist).append(")(");
                }
            }
        }
        sb.append(address).append(")(");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADDRESS, sb.toString());
        editor.putString(BASE_URL, address);
        editor.putString(PORT, port);
        editor.apply();
    }

    public void saveRepo(String repo) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(REPO, repo);
        editor.apply();
    }

    public TcpClient getTcpClient() {
        return tcpClient;
    }

    public InfoHolder getInfoHolder() {
        return infoHolder;
    }

    public void addTcpChangeListener(OnTcpInfoReceived listener) {
        listenersList.add(listener);
    }

    public void removeTcpChangeListener(OnTcpInfoReceived listener) {
        listenersList.remove(listener);
    }

    @Override
    public synchronized void onMustCallOldData(String dataInfo, String ip) {
        String databaseName = "";
        String limit = "";
        switch (dataInfo) {
            case TOP:
                databaseName = "dbo.cd_top";
                limit = "50";
                break;
            case NET_LOG:
                databaseName = "dbo.cd_net_log";
                limit = "50";
                break;
            case NET:
                databaseName = "dbo.cd_net";
                limit = "50";
                break;
            case PSQL:
                databaseName = "dbo.cd_psql";
                limit = "1000";
                break;
            case IOTOP:
                databaseName = "dbo.cd_iotop";
                limit = "5";
                break;
            default:
                break;
        }
        if (databaseName.isEmpty()) {
            return;
        }
        if (tcpClient != null) {
            String query = "[sql " + dataInfo + " " + ip + "] select * from " + databaseName
                    + " where c_ip = '" + ip + "' order by id desc limit " + limit;
            tcpClient.sendMessage(query);
        }
    }

    @Override
    public void firstTimeInserted(int position) {
        for (OnTcpInfoReceived listener : listenersList) {
            listener.insertNewRvItem(position);
        }
    }

}
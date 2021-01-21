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
import ru.slatinin.serverinfotcp.server.ServerArgs;
import ru.slatinin.serverinfotcp.server.SingleInfo;
import ru.slatinin.serverinfotcp.sevice.TcpClient;
import ru.slatinin.serverinfotcp.sevice.TcpService;
import ru.slatinin.serverinfotcp.ui.OnTcpInfoReceived;

import static ru.slatinin.serverinfotcp.server.SingleInfo.DF;
import static ru.slatinin.serverinfotcp.server.SingleInfo.IOTOP;
import static ru.slatinin.serverinfotcp.server.SingleInfo.NET;
import static ru.slatinin.serverinfotcp.server.SingleInfo.NET_LOG;
import static ru.slatinin.serverinfotcp.server.SingleInfo.PSQL;
import static ru.slatinin.serverinfotcp.server.SingleInfo.TOP;
import static ru.slatinin.serverinfotcp.ui.MainActivity.ADDRESS;
import static ru.slatinin.serverinfotcp.ui.MainActivity.BASE_URL;
import static ru.slatinin.serverinfotcp.ui.MainActivity.PORT;
import static ru.slatinin.serverinfotcp.ui.MainActivity.REPO;
import static ru.slatinin.serverinfotcp.ui.MainActivity.SHARED_PREFS;

public class App extends Application implements CallSqlQueryListener {
    private final String ARGS = "args";

    private ServerArgs serverArgs;
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
            infoHolder = new InfoHolder(this);
            for (OnTcpInfoReceived listener : listenersList) {
                listener.createTcpInfo(infoHolder);
            }
        }
        Thread thread = new Thread(() -> {
            tcpClient = new TcpClient(address, port, new TcpClient.OnMessageReceivedListener() {
                @Override
                public void onServerMessageReceived(JsonObject[] objects, String ip, String dataInfo) {
                    if (ARGS.equals(dataInfo)) {
                        serverArgs = new ServerArgs(objects[0]);
                        saveRepo(serverArgs.repos);
                        return;
                    }
                    int position = -1;
                    if (DF.equals(dataInfo) || IOTOP.equals(dataInfo) || PSQL.equals(dataInfo)) {
                        SingleInfo info = new SingleInfo(ip, dataInfo);
                        info.init(dataInfo, objects);
                        position = infoHolder.updateOrAddInfo(info, dataInfo, address, port);

                    } else {
                        if (objects.length > 1) {
                            SingleInfo info = new SingleInfo(ip, dataInfo);
                            info.init(dataInfo, objects);
                            position = infoHolder.updateOrAddInfo(info, dataInfo, address, port);
                        } else {
                            for (JsonObject object : objects) {
                                SingleInfo info = new SingleInfo(ip, dataInfo);
                                info.init(dataInfo, object);
                                position = infoHolder.updateOrAddInfo(info, dataInfo, address, port);
                            }
                        }
                    }
                    if (position >= 0) {
                        for (OnTcpInfoReceived listener : listenersList) {
                            listener.updateTcpInfo(infoHolder.getSingleInfoList().get(position), dataInfo, position);
                        }
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
            for (int i = 0; i < playlists.length; i++) {
                if (!playlists[i].equals(address)) {
                    sb.append(playlists[i]).append(")(");
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

    private void saveRepo(String repo) {
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

    public ServerArgs getServerArgs() {
        return serverArgs;
    }

    @Override
    public void onMustCallOldData(String dataInfo, String ip) {
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
                limit = "7";
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
    public void onSaveAddressAndPort(String address, String port) {
        saveAddressAndPort(address, port);
    }
}
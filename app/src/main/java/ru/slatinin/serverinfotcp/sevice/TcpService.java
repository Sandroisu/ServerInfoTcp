package ru.slatinin.serverinfotcp.sevice;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import ru.slatinin.serverinfotcp.App;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;

public class TcpService extends Service {
    private TcpClient tcpClient;
    private Binder binder;

    public final static String NOTIFICATION_CHANEL_ID = "tcpService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new Binder();
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANEL_ID).setContentTitle("TcpClient")
                .setContentText("Фоновый режим запущен").setPriority(PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SERVICE).build();
        startForeground(1411, notification);

        App app = (App) getApplication();
        tcpClient = app.getTcpClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        App app = (App) getApplication();
        if (tcpClient != null) {
            tcpClient.stopClient();
            tcpClient = null;
        }
        if (app.getInfoHolder()!=null){
            app.getInfoHolder().clear();
        }
        stopForeground(true);
        this.stopSelf();
    }

    public class Binder extends android.os.Binder {
        public TcpService getService() {
            return TcpService.this;
        }
    }
}

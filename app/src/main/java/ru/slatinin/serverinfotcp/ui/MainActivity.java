package ru.slatinin.serverinfotcp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.slatinin.serverinfotcp.App;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.sevice.TcpClient;
import ru.slatinin.serverinfotcp.server.InfoHolder;
import ru.slatinin.serverinfotcp.server.SingleInfo;

public class MainActivity extends AppCompatActivity implements OnTcpInfoReceived, ServerInfoAdapter.OnServerInfoHolderClickListener, OnConnectAttempt {
    public static final String SHARED_PREFS = "ru.slatinin.serverinfotcp.shared.prefs";
    public static final String ADDRESS = "ru.slatinin.serverinfotcp.address";
    public static final String PORT = "ru.slatinin.serverinfotcp.port";
    public static final String IP = "ru.slatinin.serverinfotcp.ip";
    private TcpClient mTcpClient;
    private TextView tvError;
    private ServerInfoAdapter serverInfoAdapter;
    private InfoHolder infoHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App app = (App) getApplication();
        infoHolder = app.getInfoHolder();
        setContentView(R.layout.activity_main);
        RecyclerView mRecyclerView = findViewById(R.id.activity_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serverInfoAdapter = new ServerInfoAdapter(infoHolder.getSingleInfoList(), this);
        mRecyclerView.setAdapter(serverInfoAdapter);
        ServerInfoDialog serverInfoDialog = new ServerInfoDialog(this);
        serverInfoDialog.show(getSupportFragmentManager(), "map-help-dialog");
        Button reconnect = findViewById(R.id.activity_main_reconnect);
        reconnect.setOnClickListener(v -> {
            ServerInfoDialog reconnectServerInfoDialog = new ServerInfoDialog(this);
            reconnectServerInfoDialog.show(getSupportFragmentManager(), "map-help-dialog");
        });
        Button send = findViewById(R.id.activity_main_send);
        send.setOnClickListener(v -> {
            Thread thread = new Thread(() -> {
                mTcpClient = app.getTcpClient();
                if (mTcpClient != null) {
                    mTcpClient.sendMessage("Hello, World!!!");
                }
            });
            thread.start();
        });
        tvError = findViewById(R.id.activity_main_error);
    }

    @Override
    protected void onStart() {
        super.onStart();
        App app = (App) getApplication();
        app.addTcpChangeListener(this);
        if (infoHolder == null) {
            infoHolder = app.getInfoHolder();
        }
    }

    @Override
    public void updateTcpInfo(SingleInfo info, String dataInfo) {
        runOnUiThread(() ->

                serverInfoAdapter.notifyDataSetChanged());
    }

    @Override
    public void showError(String errorMessage) {
        runOnUiThread(() -> {
            tvError.setText(errorMessage);
            tvError.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        App app = (App) getApplication();
        app.removeTcpChangeListener(this);
    }

    @Override
    public void onClicked(String ip) {
        Intent intent = new Intent(this, DetailedActivity.class);
        intent.putExtra(IP, ip);
        startActivity(intent);
    }

    @Override
    public void onOpenBrowser(String ip) {
        App app = (App) getApplication();
        if (app.getServerArgs() != null) {
            String url = App.BASE_URL + app.getServerArgs().repos + "/%3Ahome%3Atcp%3Atcp-monitor.prpt/generatedContent?c_server="
                    + ip + "&userid=tcp&password=monitor-0&output-target=pageable/pdf";
            if (url.contains("//")) {
                url = url.replace("//", "/");
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url));
            startActivity(browserIntent);
        } else {
            Toast.makeText(app, "Невозможно получить файл", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRedSignal(ImageView signal) {
        runOnUiThread(() -> signal.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_server_problem_signal_24)));
    }

    @Override
    public void onConnectAttempt() {
        tvError.setVisibility(View.GONE);
    }
}
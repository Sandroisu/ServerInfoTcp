package ru.slatinin.serverinfotcp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import ru.slatinin.serverinfotcp.App;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.sevice.TcpClient;
import ru.slatinin.serverinfotcp.server.InfoHolder;
import ru.slatinin.serverinfotcp.server.SingleServer;

public class MainActivity extends AppCompatActivity implements OnTcpInfoReceived, ServerInfoAdapter.OnServerInfoHolderClickListener, OnConnectAttempt {
    public static final String SHARED_PREFS = "ru.slatinin.serverinfotcp.shared.prefs";
    public static final String ADDRESS = "ru.slatinin.serverinfotcp.address";
    public static final String PORT = "ru.slatinin.serverinfotcp.port";
    public static final String IP = "ru.slatinin.serverinfotcp.ip";
    public static final String REPO = "ru.slatinin.serverinfotcp.repo";
    public static final String BASE_URL = "ru.slatinin.serverinfotcp.base.url";

    private TcpClient mTcpClient;
    private TextView tvError;
    private ServerInfoAdapter serverInfoAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Мониторинг серверов");
        App app = (App) getApplication();
        InfoHolder infoHolder = app.getInfoHolder();
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.activity_main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serverInfoAdapter = new ServerInfoAdapter(infoHolder.getSingleServerList(), this);
        recyclerView.setAdapter(serverInfoAdapter);
        ServerInfoDialog serverInfoDialog = new ServerInfoDialog(this);
        serverInfoDialog.show(getSupportFragmentManager(), "server_address_dialog");
        tvError = findViewById(R.id.activity_main_error);
    }

    @Override
    protected void onStart() {
        super.onStart();
        App app = (App) getApplication();
        app.addTcpChangeListener(this);
    }

    @Override
    public void updateTcpInfo(SingleServer info) {
        runOnUiThread(() -> serverInfoAdapter.notifyDataSetChanged());
    }

    @Override
    public void createTcpInfo(InfoHolder infoHolder) {
        runOnUiThread(() -> {
            serverInfoAdapter = new ServerInfoAdapter(infoHolder.getSingleServerList(), MainActivity.this);
            recyclerView.setAdapter(serverInfoAdapter);
        });

    }

    @Override
    public void showError(String errorMessage) {
        runOnUiThread(() -> {
            getSupportActionBar().setSubtitle("");
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
    public void onRedSignal(ImageView signal) {
        runOnUiThread(() -> signal.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_server_problem_signal_24)));
    }

    @Override
    public void onConnectAttempt(String address) {
        getSupportActionBar().setSubtitle(address);
        tvError.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.main_menu_reconnect) {
            ServerInfoDialog reconnectServerInfoDialog = new ServerInfoDialog(this);
            reconnectServerInfoDialog.show(getSupportFragmentManager(), "server_address_dialog");
        }
        if (item.getItemId() == R.id.main_menu_send) {
            App app = (App) getApplication();
            Thread thread = new Thread(() -> {
                mTcpClient = app.getTcpClient();
                if (mTcpClient != null) {
                    mTcpClient.sendMessage("Hello, World!!!");
                }
            });
            thread.start();
        }
        return super.onOptionsItemSelected(item);
    }
}
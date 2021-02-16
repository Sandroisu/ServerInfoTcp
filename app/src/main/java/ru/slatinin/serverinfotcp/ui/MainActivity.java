package ru.slatinin.serverinfotcp.ui;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import ru.slatinin.serverinfotcp.sevice.TcpService;
import ru.slatinin.serverinfotcp.ui.adapter.ServerInfoAdapter;
import ru.slatinin.serverinfotcp.ui.adapter.ServerInfoHolder;

public class MainActivity extends BaseActivity implements OnTcpInfoReceived, ServerInfoHolder.OnServerInfoHolderClickListener, OnConnectAttempt {
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
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        serverInfoAdapter = new ServerInfoAdapter(infoHolder.getSingleServerList(), this);
        recyclerView.setAdapter(serverInfoAdapter);
        ServerInfoDialog serverInfoDialog = new ServerInfoDialog(this);
        serverInfoDialog.show(getSupportFragmentManager(), "server_address_dialog");
        tvError = findViewById(R.id.activity_main_error);
        Intent intent = new Intent();
        intent.setClass(this, TcpService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        App app = (App) getApplication();
        app.addTcpChangeListener(this);
    }

    @Override
    public void updateTcpInfo(int position) {
        runOnUiThread(() -> {
            //serverInfoAdapter.notifyDataSetChanged();
            serverInfoAdapter.notifyItemChanged(position);
        });
    }

    @Override
    public void createTcpInfo(InfoHolder infoHolder) {
        runOnUiThread(() -> {
            serverInfoAdapter = null;
            serverInfoAdapter = new ServerInfoAdapter(infoHolder.getSingleServerList(), MainActivity.this);
            recyclerView.setAdapter(serverInfoAdapter);
        });

    }

    @Override
    public void showError(String errorMessage) {
        runOnUiThread(() -> {
            Objects.requireNonNull(getSupportActionBar()).setSubtitle("");
            tvError.setText(errorMessage);
            tvError.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void insertNewRvItem(int position) {
        runOnUiThread(() -> serverInfoAdapter.notifyItemInserted(position));
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
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(address);
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
        if (item.getItemId() == R.id.main_menu_json) {
            JsonInfoDialog jsonInfoDialog = new JsonInfoDialog();
            jsonInfoDialog.show(getSupportFragmentManager(), "last_json");
        }
        return super.onOptionsItemSelected(item);
    }

    public static class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

}
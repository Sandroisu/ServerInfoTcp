package ru.slatinin.serverinfotcp.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import ru.slatinin.serverinfotcp.App;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.server.SingleInfo;
import ru.slatinin.serverinfotcp.server.servertop.ServerTOP;


public class DetailedActivity extends AppCompatActivity implements OnTcpInfoReceived, ViewTreeObserver.OnGlobalLayoutListener, OnConnectAttempt{

    private LineChart lcNetInfo;

    private BarChart bcDiskInfo;

    private TextView tvTopCommon;
    private PieChart pcTopTasks;
    private BarChart bcTopMem;
    private BarChart bcTopSwap;
    private BarChart bcTopCpu;
    private LineChart lcCpuInfo;

    private LineChart lcPsqlInfo;

    private TextView tvError;
    private Button btnReconnect;

    private ConstraintLayout clPsql;
    private ConstraintLayout clDf;

    private String ip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);
        App app = (App) getApplication();
        app.addTcpChangeListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lcNetInfo = findViewById(R.id.da_net);
        lcCpuInfo = findViewById(R.id.da_load_average);
        bcDiskInfo = findViewById(R.id.da_df);
        tvTopCommon = findViewById(R.id.da_top_common);
        pcTopTasks = findViewById(R.id.da_top_tasks);
        bcTopMem = findViewById(R.id.da_top_mem);
        bcTopSwap = findViewById(R.id.da_top_swap);
        bcTopCpu = findViewById(R.id.da_top_cpu);
        lcPsqlInfo = findViewById(R.id.da_psql);
        tvError = findViewById(R.id.da_error);
        clPsql = findViewById(R.id.da_psql_block);
        clDf = findViewById(R.id.da_df_block);

        bcTopMem.getViewTreeObserver().addOnGlobalLayoutListener(this);

        ip = getIntent().getStringExtra(MainActivity.IP);

        ChartUtil.initLineChart(lcNetInfo);
        ChartUtil.initLineChart(lcCpuInfo);
        ChartUtil.initLineChart(lcPsqlInfo);
        ChartUtil.initBarChart(bcTopMem, true, true, false, true, Legend.LegendForm.CIRCLE);
        ChartUtil.initBarChart(bcTopSwap, true, true, false, true, Legend.LegendForm.CIRCLE);
        ChartUtil.initBarChart(bcTopCpu, false, true, false, true, Legend.LegendForm.CIRCLE);
        ChartUtil.initPieChart(pcTopTasks);
        ChartUtil.initBarChart(bcDiskInfo, true, false, true, false, Legend.LegendForm.NONE);

        btnReconnect = findViewById(R.id.da_reconnect);
        btnReconnect.setOnClickListener(v -> {
            ServerInfoDialog reconnectServerInfoDialog = new ServerInfoDialog(this);
            reconnectServerInfoDialog.show(getSupportFragmentManager(), "map-help-dialog");
        });
    }


    @Override
    public void updateTcpInfo(SingleInfo info) {
        runOnUiThread(() -> {
            if (info.getServerNETList() != null && info.ip.equals(ip)) {
                ChartUtil.updateNetList(info.getServerNETList(), lcNetInfo);
            }
            if (info.getServerDFList() != null && info.ip.equals(ip)) {
                if (clDf.getVisibility()==View.GONE){
                    clDf.setVisibility(View.VISIBLE);
                }
                ChartUtil.updateDiskInfo(info.getServerDFList(), bcDiskInfo);
            }
            if (info.getServerTOP() != null && info.ip.equals(ip)) {
                updateTop(info.getServerTOP());
                ChartUtil.updateCpuList(info.getServerCommonList(), lcCpuInfo);
            }
            int x = info.getServerPsqlLists().size();
            if (x>0 && info.ip.equals(ip)) {
                if (clPsql.getVisibility()==View.GONE){
                    clPsql.setVisibility(View.VISIBLE);
                }
                ChartUtil.updatePsqlList(info.getServerPsqlLists(), lcPsqlInfo, this);
            }
        });

    }

    @Override
    public void showError(String errorMessage) {
        runOnUiThread(() -> {
            tvError.setVisibility(View.VISIBLE);
            btnReconnect.setVisibility(View.VISIBLE);
            tvError.setText(errorMessage);
        });
    }


    private void updateTop(ServerTOP serverTOP) {
        ChartUtil.updateTopBarChart(serverTOP.serverMem, bcTopMem, true);
        ChartUtil.updateTopBarChart(serverTOP.serverSWAP, bcTopSwap, true);
        ChartUtil.updateTopBarChart(serverTOP.serverCPU, bcTopCpu, false);
        ChartUtil.updateServerTasks(serverTOP.serverTasks, pcTopTasks);
        ChartUtil.updateServerCommon(serverTOP.serverCommon, tvTopCommon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App app = (App) getApplication();
        app.removeTcpChangeListener(this);
    }

    @Override
    public void onGlobalLayout() {
        int btmx = (int) (bcTopMem.getWidth() - bcTopMem.getViewPortHandler().offsetRight());
        bcTopMem.getDescription().setText("TOP MEM");
        bcTopMem.getDescription().setPosition(btmx, 20);
        int btsx = (int) (bcTopSwap.getWidth() - bcTopSwap.getViewPortHandler().offsetRight());
        bcTopSwap.getDescription().setText("TOP SWAP");
        bcTopSwap.getDescription().setPosition(btsx, 20);
        int btcx = (int) (bcTopCpu.getWidth() - bcTopCpu.getViewPortHandler().offsetRight());
        bcTopCpu.getDescription().setText("TOP CPU");
        bcTopCpu.getDescription().setPosition(btcx, 20);
        int pctt = (int) (pcTopTasks.getWidth() - pcTopTasks.getViewPortHandler().offsetRight());
        pcTopTasks.getDescription().setText("TOP TASKS");
        pcTopTasks.getDescription().setPosition(pctt, 20);
    }

    @Override
    public void onConnectAttempt() {
        btnReconnect.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

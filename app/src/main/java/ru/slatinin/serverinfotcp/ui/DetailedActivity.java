package ru.slatinin.serverinfotcp.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.List;
import java.util.Objects;

import ru.slatinin.serverinfotcp.App;
import ru.slatinin.serverinfotcp.DownloadPdfView;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.UrlUtil;
import ru.slatinin.serverinfotcp.server.InfoHolder;
import ru.slatinin.serverinfotcp.server.servernetlog.ServerNetLog;
import ru.slatinin.serverinfotcp.server.serverpsql.ServerPsql;
import ru.slatinin.serverinfotcp.server.SingleServer;
import ru.slatinin.serverinfotcp.server.serverdf.ServerDFObjectKeeper;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopObjectKeeper;
import ru.slatinin.serverinfotcp.server.serverpsql.ServerPsqlObjectListKeeper;
import ru.slatinin.serverinfotcp.server.servertop.ServerTOP;

import static ru.slatinin.serverinfotcp.server.SingleServer.DF;
import static ru.slatinin.serverinfotcp.server.SingleServer.IOTOP;
import static ru.slatinin.serverinfotcp.server.SingleServer.NET;
import static ru.slatinin.serverinfotcp.server.SingleServer.NET_LOG;
import static ru.slatinin.serverinfotcp.server.SingleServer.PSQL;
import static ru.slatinin.serverinfotcp.server.SingleServer.TOP;


public class DetailedActivity extends AppCompatActivity implements OnTcpInfoReceived, ViewTreeObserver.OnGlobalLayoutListener, OnConnectAttempt {

    private LineChart lcNet;
    private LineChart lcNetLog;

    private BarChart bcDiskInfo;

    private TextView tvTopCommon;
    private PieChart pcTopTasks;
    private BarChart bcTopMem;
    private BarChart bcTopSwap;
    private BarChart bcTopCpu;
    private BarChart bcIoTopSpeed;
    private BarChart bcIoTopTotal;

    private LineChart lcCpuInfo;
    private LineChart lcPsqlXac;
    private LineChart lcPsqlNbe;

    private TextView tvError;
    private Button btnReconnect;
    private Button btnShowProcesses;
    private TableLayout tlProcesses;

    private ConstraintLayout clPsql;
    private ConstraintLayout clDf;
    private ConstraintLayout clIoTop;
    private ConstraintLayout clNetLog;

    private DownloadPdfView ivCpuPdf;
    private DownloadPdfView ivMemPdf;
    private DownloadPdfView ivNetPdf;
    private DownloadPdfView ivIoTopPdf;
    private DownloadPdfView ivNetLogPdf;
    private ImageView ivPsqlPdf;
    private ImageView ivDfPdf;

    private String ip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);
        App app = (App) getApplication();
        app.addTcpChangeListener(this);
        ip = getIntent().getStringExtra(MainActivity.IP);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Мониторинг серверов");
        getSupportActionBar().setSubtitle(ip);

        lcNet = findViewById(R.id.da_net);
        lcNetLog = findViewById(R.id.da_net_log);
        lcCpuInfo = findViewById(R.id.da_cpu_load);
        bcDiskInfo = findViewById(R.id.da_df);
        tvTopCommon = findViewById(R.id.da_top_common);
        pcTopTasks = findViewById(R.id.da_top_tasks);
        bcTopMem = findViewById(R.id.da_top_mem);
        bcTopSwap = findViewById(R.id.da_top_swap);
        bcTopCpu = findViewById(R.id.da_top_cpu);
        bcIoTopSpeed = findViewById(R.id.da_iotop_speed);
        bcIoTopTotal = findViewById(R.id.da_iotop_total);
        lcPsqlXac = findViewById(R.id.da_psql_xac);
        lcPsqlNbe = findViewById(R.id.da_psql_nbe);
        tvError = findViewById(R.id.da_error);
        clPsql = findViewById(R.id.da_psql_block);
        clDf = findViewById(R.id.da_df_block);
        clIoTop = findViewById(R.id.da_iotop_block);
        clNetLog = findViewById(R.id.da_net_log_block);
        ivCpuPdf = findViewById(R.id.da_cpu_load_pdf);
        ivMemPdf = findViewById(R.id.da_top_mem_pdf);
        ivNetPdf = findViewById(R.id.da_net_pdf);
        ivDfPdf = findViewById(R.id.da_df_pdf);
        ivIoTopPdf = findViewById(R.id.da_iotop_pdf);
        ivNetLogPdf = findViewById(R.id.da_net_log_pdf);
        ivPsqlPdf = findViewById(R.id.da_psql_pdf);
        tlProcesses = findViewById(R.id.da_processes);

        bcTopMem.getViewTreeObserver().addOnGlobalLayoutListener(this);

        ChartUtil.initLineChart(lcNet, true);
        ChartUtil.initLineChart(lcNetLog, true);
        ChartUtil.initLineChart(lcCpuInfo, true);
        ChartUtil.initLineChart(lcPsqlXac, false);
        ChartUtil.initLineChart(lcPsqlNbe, false);
        ChartUtil.initBarChart(bcTopMem, true, true, false, true);
        ChartUtil.initBarChart(bcTopSwap, true, true, false, true);
        ChartUtil.initBarChart(bcTopCpu, true, true, false, true);
        ChartUtil.initBarChart(bcIoTopSpeed, true, false, true, false);
        ChartUtil.initBarChart(bcIoTopTotal, true, false, true, false);
        ChartUtil.initPieChart(pcTopTasks);
        ChartUtil.initBarChart(bcDiskInfo, true, false, true, false);

        btnReconnect = findViewById(R.id.da_reconnect);
        btnReconnect.setOnClickListener(v -> {
            ServerInfoDialog reconnectServerInfoDialog = new ServerInfoDialog(this);
            reconnectServerInfoDialog.show(getSupportFragmentManager(), "server_address_dialog");
        });

        btnShowProcesses = findViewById(R.id.da_show_processes);

        btnShowProcesses.setOnClickListener(v -> {
            if (tlProcesses.getVisibility() == View.GONE) {
                tlProcesses.setVisibility(View.VISIBLE);
            }else {
                tlProcesses.setVisibility(View.GONE);
            }
        });

        ivCpuPdf.setUrl(UrlUtil.getUrl(ip, "cpu", this), ip + "cpu.pdf");
        ivMemPdf.setUrl(UrlUtil.getUrl(ip, TOP, this), ip + TOP + ".pdf");
        ivNetPdf.setUrl(UrlUtil.getUrl(ip, NET, this), ip + NET + ".pdf");
        ivNetLogPdf.setUrl(UrlUtil.getUrl(ip, NET_LOG, this), ip + NET_LOG + ".pdf");

        for (int i = 0; i < app.getInfoHolder().getSingleServerList().size(); i++) {
            if (app.getInfoHolder().getSingleServerList().get(i).ip.equals(ip)) {
                SingleServer info = app.getInfoHolder().getSingleServerList().get(i);
                ChartUtil.updateNetList(info.serverNetObjectKeeper.serverNetList, lcNet);
                updateDiskInfo(info.serverDFObjectKeeper);
                updateTop(info.serverTOP);
                ChartUtil.updateCpuList(info.serverTOP.serverCommonList, lcCpuInfo);
                updatePsql(info.serverPsqlObjectListKeeper);
                updateIoTop(info.serverIoTopObjectKeeper);
                updateNetLog(info.serverNetLogObjectKeeper.serverNetLogList);
                break;
            }
        }
    }


    @Override
    public void updateTcpInfo(SingleServer info) {
        runOnUiThread(() -> {
            if (!info.ip.equals(ip)) {
                return;
            }
            switch (info.dataInfo) {
                case NET:
                    ChartUtil.updateNetList(info.serverNetObjectKeeper.serverNetList, lcNet);
                    break;
                case DF:
                    updateDiskInfo(info.serverDFObjectKeeper);
                    break;
                case TOP:
                    updateTop(info.serverTOP);
                    ChartUtil.updateCpuList(info.serverTOP.serverCommonList, lcCpuInfo);
                    break;
                case PSQL:
                    updatePsql(info.serverPsqlObjectListKeeper);
                    break;
                case IOTOP:
                    updateIoTop(info.serverIoTopObjectKeeper);
                    break;
                case NET_LOG:
                    updateNetLog(info.serverNetLogObjectKeeper.serverNetLogList);
                    break;
            }
        });

    }

    @Override
    public void createTcpInfo(InfoHolder holder) {

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
        ChartUtil.updateTopBars(serverTOP.serverMem, bcTopMem, true);
        ChartUtil.updateTopBars(serverTOP.serverSWAP, bcTopSwap, true);
        ChartUtil.updateTopBars(serverTOP.serverCPU, bcTopCpu, false);
        ChartUtil.updateServerTasksBars(serverTOP.serverTasks, pcTopTasks);
        ChartUtil.updateServerCommonText(serverTOP.lastServerCommon, tvTopCommon);
        ChartUtil.buildProcessesTable(serverTOP.serverProcesses, tlProcesses, DetailedActivity.this);

    }

    private void updateDiskInfo(ServerDFObjectKeeper serverDFObjectKeeper) {
        if (serverDFObjectKeeper == null || serverDFObjectKeeper.singleServerDFList.size() == 0) {
            return;
        }
        if (clDf.getVisibility() == View.GONE) {
            clDf.setVisibility(View.VISIBLE);
        }
        ChartUtil.updateDiskInfoBars(serverDFObjectKeeper, bcDiskInfo);
        ivDfPdf.setOnClickListener(v -> {
            ChoosePdfDialog choosePdfDialog = new ChoosePdfDialog(serverDFObjectKeeper, ip);
            choosePdfDialog.show(getSupportFragmentManager(), "choose_df_dialog");
        });
    }

    private void updatePsql(ServerPsqlObjectListKeeper serverPsqlObjectListKeeper) {
        if (serverPsqlObjectListKeeper == null || serverPsqlObjectListKeeper.serverPsqlObjectKeeperList.size() == 0
                || serverPsqlObjectListKeeper.serverPsqlObjectKeeperList.get(0).serverPsqlList.size() == 0) {
            return;
        }
        if (clPsql.getVisibility() == View.GONE) {
            clPsql.setVisibility(View.VISIBLE);
        }
        ChartUtil.updatePsqlList(serverPsqlObjectListKeeper, lcPsqlXac, true);
        ChartUtil.updatePsqlList(serverPsqlObjectListKeeper, lcPsqlNbe, false);
        ivPsqlPdf.setOnClickListener(v -> {
            ChoosePdfDialog choosePdfDialog = new ChoosePdfDialog(serverPsqlObjectListKeeper.serverPsqlObjectKeeperList.get(0).serverPsqlList, ip);
            choosePdfDialog.show(getSupportFragmentManager(), "choose_psql_dialog");
        });
    }

    private void updateIoTop(ServerIoTopObjectKeeper serverIoTopLists) {
        if (serverIoTopLists == null || serverIoTopLists.serverIoTopList.size() == 0) {
            return;
        }
        if (clIoTop.getVisibility() == View.GONE) {
            clIoTop.setVisibility(View.VISIBLE);
        }
        ChartUtil.updateIoTopBars(serverIoTopLists.serverIoTopList, bcIoTopSpeed, true);
        ChartUtil.updateIoTopBars(serverIoTopLists.serverIoTopList, bcIoTopTotal, false);
        ivIoTopPdf.setOnClickListener(v -> {
            ChoosePdfDialog choosePdfDialog = new ChoosePdfDialog(serverIoTopLists, ip);
            choosePdfDialog.show(getSupportFragmentManager(), "choose_iotop_dialog");
        });
    }

    private void updateNetLog(List<ServerNetLog> serverNetLogs) {
        if (serverNetLogs.size() == 0) {
            return;
        }
        if (clNetLog.getVisibility() == View.GONE) {
            clNetLog.setVisibility(View.VISIBLE);
        }
        ChartUtil.updateNetLogList(serverNetLogs, lcNetLog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App app = (App) getApplication();
        app.removeTcpChangeListener(this);
    }

    @Override
    public void onGlobalLayout() {
        int btmx = (bcTopMem.getWidth() - 5 - ivMemPdf.getMeasuredWidth());
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
        int lcpx = (lcPsqlXac.getWidth() - 25 - ivPsqlPdf.getMeasuredWidth());
        lcPsqlXac.getDescription().setText("PSQL XACT_COMMITS");
        lcPsqlXac.getDescription().setPosition(lcpx, 20);
        int lcpn = (int) (lcPsqlNbe.getWidth() - lcPsqlNbe.getViewPortHandler().offsetRight());
        lcPsqlNbe.getDescription().setText("NUMBACKENDS");
        lcPsqlNbe.getDescription().setPosition(lcpn, 20);
    }

    @Override
    public void onConnectAttempt(String address) {
        btnReconnect.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}

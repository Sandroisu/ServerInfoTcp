package ru.slatinin.serverinfotcp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import java.util.List;

import ru.slatinin.serverinfotcp.App;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.server.ServerPSQL;
import ru.slatinin.serverinfotcp.server.SingleInfo;
import ru.slatinin.serverinfotcp.server.serverdf.ServerDFList;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopList;
import ru.slatinin.serverinfotcp.server.servertop.ServerTOP;

import static ru.slatinin.serverinfotcp.server.SingleInfo.DF;
import static ru.slatinin.serverinfotcp.server.SingleInfo.IOTOP;
import static ru.slatinin.serverinfotcp.server.SingleInfo.NET;
import static ru.slatinin.serverinfotcp.server.SingleInfo.PSQL;
import static ru.slatinin.serverinfotcp.server.SingleInfo.TOP;


public class DetailedActivity extends AppCompatActivity implements OnTcpInfoReceived, ViewTreeObserver.OnGlobalLayoutListener, OnConnectAttempt {

    private LineChart lcNetInfo;

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

    private ConstraintLayout clPsql;
    private ConstraintLayout clDf;
    private ConstraintLayout clIoTop;

    private ImageView ivCpuPdf;
    private ImageView ivMemPdf;
    private ImageView ivNetPdf;
    private ImageView ivDfPdf;

    private String ip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);
        App app = (App) getApplication();
        app.addTcpChangeListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lcNetInfo = findViewById(R.id.da_net);
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
        ivCpuPdf = findViewById(R.id.da_cpu_load_pdf);
        ivMemPdf = findViewById(R.id.da_top_mem_pdf);
        ivNetPdf = findViewById(R.id.da_net_pdf);
        ivDfPdf = findViewById(R.id.da_df_pdf);

        bcTopMem.getViewTreeObserver().addOnGlobalLayoutListener(this);

        ip = getIntent().getStringExtra(MainActivity.IP);

        ChartUtil.initLineChart(lcNetInfo, true, false, true);
        ChartUtil.initLineChart(lcCpuInfo, true, false, true);
        ChartUtil.initLineChart(lcPsqlXac, false, true, false);
        ChartUtil.initLineChart(lcPsqlNbe, false, true, false);
        ChartUtil.initBarChart(bcTopMem, true, true, false, true, Legend.LegendForm.CIRCLE);
        ChartUtil.initBarChart(bcTopSwap, true, true, false, true, Legend.LegendForm.CIRCLE);
        ChartUtil.initBarChart(bcTopCpu, false, true, false, true, Legend.LegendForm.CIRCLE);
        ChartUtil.initBarChart(bcIoTopSpeed, true, false, true, false, Legend.LegendForm.NONE);
        ChartUtil.initBarChart(bcIoTopTotal, true, false, true, false, Legend.LegendForm.NONE);
        ChartUtil.initPieChart(pcTopTasks);
        ChartUtil.initBarChart(bcDiskInfo, true, false, true, false, Legend.LegendForm.NONE);

        btnReconnect = findViewById(R.id.da_reconnect);
        btnReconnect.setOnClickListener(v -> {
            ServerInfoDialog reconnectServerInfoDialog = new ServerInfoDialog(this);
            reconnectServerInfoDialog.show(getSupportFragmentManager(), "map-help-dialog");
        });
        ivCpuPdf.setOnClickListener(v -> openBrowser(app, "cpu"));
        ivMemPdf.setOnClickListener(v -> openBrowser(app, "top"));
        ivNetPdf.setOnClickListener(v -> openBrowser(app, "net"));
        for (int i = 0; i < app.getInfoHolder().getSingleInfoList().size(); i++) {
            if (app.getInfoHolder().getSingleInfoList().get(i).ip.equals(ip)){
                SingleInfo info = app.getInfoHolder().getSingleInfoList().get(i);
                ChartUtil.updateNetList(info.getServerNETList(), lcNetInfo);
                updateDiskInfo(info.getServerDFList());
                updateTop(info.getServerTOP());
                ChartUtil.updateCpuList(info.getServerCommonList(), lcCpuInfo);
                updatePsql(info.getServerPsqlLists());
                updateIoTop(info.getServerIoTopList());
                break;
            }
        }
    }


    @Override
    public void updateTcpInfo(SingleInfo info, String dataInfo) {
        runOnUiThread(() -> {
            if (!info.ip.equals(ip)) {
                return;
            }
            switch (dataInfo) {
                case NET:
                    ChartUtil.updateNetList(info.getServerNETList(), lcNetInfo);
                    break;
                case DF:
                    updateDiskInfo(info.getServerDFList());
                    break;
                case TOP:
                    updateTop(info.getServerTOP());
                    ChartUtil.updateCpuList(info.getServerCommonList(), lcCpuInfo);
                    break;
                case PSQL:
                    updatePsql(info.getServerPsqlLists());
                    break;
                case IOTOP:
                    updateIoTop(info.getServerIoTopList());
                    break;
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
        if (serverTOP == null){
            return;
        }
        ChartUtil.updateTopBarChart(serverTOP.serverMem, bcTopMem, true);
        ChartUtil.updateTopBarChart(serverTOP.serverSWAP, bcTopSwap, true);
        ChartUtil.updateTopBarChart(serverTOP.serverCPU, bcTopCpu, false);
        ChartUtil.updateServerTasks(serverTOP.serverTasks, pcTopTasks);
        ChartUtil.updateServerCommon(serverTOP.serverCommon, tvTopCommon);
    }

    private void updateDiskInfo(ServerDFList serverDFList){
        if (serverDFList == null || serverDFList.singleServerDFList.size()==0){
            return;
        }
        if (clDf.getVisibility() == View.GONE) {
            clDf.setVisibility(View.VISIBLE);
        }
        ChartUtil.updateDiskInfo(serverDFList, bcDiskInfo);
        ivDfPdf.setOnClickListener(v -> {
            ChooseDfPdfDialog chooseDfPdfDialog = new ChooseDfPdfDialog(serverDFList, ip);
            chooseDfPdfDialog.show(getSupportFragmentManager(), "map-help-dialog");
        });
    }

    private void updatePsql(List<List<ServerPSQL>> serverPSQLS){
        if (serverPSQLS == null || serverPSQLS.size()==0){
            return;
        }
        if (clPsql.getVisibility() == View.GONE) {
            clPsql.setVisibility(View.VISIBLE);
        }
        ChartUtil.updatePsqlList(serverPSQLS, lcPsqlXac, true);
        ChartUtil.updatePsqlList(serverPSQLS, lcPsqlNbe, false);
    }

    private void updateIoTop(ServerIoTopList serverIoTopLists){
        if (serverIoTopLists == null){
            return;
        }
        if (clIoTop.getVisibility() == View.GONE) {
            clIoTop.setVisibility(View.VISIBLE);
        }
        ChartUtil.updateIoTopList(serverIoTopLists.serverIoTopList, bcIoTopSpeed, true);
        ChartUtil.updateIoTopList(serverIoTopLists.serverIoTopList, bcIoTopTotal, false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        App app = (App) getApplication();
        app.removeTcpChangeListener(this);
    }

    @Override
    public void onGlobalLayout() {
        int btmx = (int) (bcTopMem.getWidth() - 5 - ivMemPdf.getMeasuredWidth());
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
        int lcpx = (int) (lcPsqlXac.getWidth() - lcPsqlXac.getViewPortHandler().offsetRight());
        lcPsqlXac.getDescription().setText("PSQL XACT_COMMITS");
        lcPsqlXac.getDescription().setPosition(lcpx, 20);
        int lcpn = (int) (lcPsqlNbe.getWidth() - lcPsqlNbe.getViewPortHandler().offsetRight());
        lcPsqlNbe.getDescription().setText("NUMBACKENDS");
        lcPsqlNbe.getDescription().setPosition(lcpn, 20);
    }

    @Override
    public void onConnectAttempt() {
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

    private void openBrowser(App app, String type) {
        if (app.getServerArgs() != null) {
            String url = App.BASE_URL + app.getServerArgs().repos + "/%3Ahome%3Atcp%3A" + type + "-monitor.prpt/generatedContent?c_server="
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
}

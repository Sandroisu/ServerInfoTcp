package ru.slatinin.serverinfotcp.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import ru.slatinin.serverinfotcp.DownloadPdfView;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.TimeUtil;
import ru.slatinin.serverinfotcp.UrlUtil;
import ru.slatinin.serverinfotcp.server.SingleServer;
import ru.slatinin.serverinfotcp.ui.ChartUtil;

import static ru.slatinin.serverinfotcp.server.SingleServer.NET;
import static ru.slatinin.serverinfotcp.server.SingleServer.TOP;

public class ServerInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final LineChart lcCPU;
    private final BarChart bcMem;
    private final TextView tvIp;
    private final TableLayout tlIotop;
    private final TextView tvNet;
    private final TextView tvTime;
    private final ImageView ivSignal;
    private final ConstraintLayout clNet;
    private final ConstraintLayout clIoTop;
    private final DownloadPdfView dpvDownloadPdf;
    private String ip;
    private final OnServerInfoHolderClickListener listener;
    private final Context mContext;

    public ServerInfoHolder(@NonNull View itemView, OnServerInfoHolderClickListener listener) {
        super(itemView);
        this.listener = listener;
        mContext = itemView.getContext();
        lcCPU = itemView.findViewById(R.id.item_server_cpu_info);
        tvIp = itemView.findViewById(R.id.item_server_ip);
        bcMem = itemView.findViewById(R.id.item_mem);
        tlIotop = itemView.findViewById(R.id.item_server_iotop);
        tvNet = itemView.findViewById(R.id.item_server_net);
        tvTime = itemView.findViewById(R.id.item_server_time);
        ivSignal = itemView.findViewById(R.id.item_server_signal);
        clNet = itemView.findViewById(R.id.item_server_net_block);
        clIoTop = itemView.findViewById(R.id.item_server_iotop_block);
        dpvDownloadPdf = itemView.findViewById(R.id.item_server_pdf);
        ChartUtil.initLineChart(lcCPU, true);
        ChartUtil.initBarChart(bcMem, false, false,
                false, true);
        tvIp.setOnClickListener(this);
    }

    protected void bind(SingleServer info) {
        tvTime.setText(TimeUtil.formatMillisToHours(info.time));
        ivSignal.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_server_ok_signal_24));
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> listener.onRedSignal(ivSignal), 1000);
        if (ip == null) {
            ip = info.ip;
            tvIp.setText(ip);
            String url = UrlUtil.getUrl(ip, "tcp", mContext);
            dpvDownloadPdf.setUrl(url, "tcp-monitor.PDF");
        }
        switch (info.dataInfo) {
            case NET:
                if (clNet.getVisibility() == View.GONE) {
                    clNet.setVisibility(View.VISIBLE);
                }
                String netInfo = info.serverNetObjectKeeper.lastServerNet.getInfoString();
                tvNet.setText(netInfo);
                break;
            case TOP:
                ChartUtil.updateTopBars(info.serverTOP.serverMem, bcMem, true);
                ChartUtil.updateCpuList(info.serverTOP.serverCommonList, lcCPU);
                break;
        }
        if (info.serverIoTopObjectKeeper != null && info.serverIoTopObjectKeeper.serverIoTopList.size() > 0) {
            ChartUtil.buildTable(info.serverIoTopObjectKeeper, clIoTop, tlIotop, mContext);
        }
    }

    @Override
    public void onClick(View v) {
        if (listener != null && ip != null) {
            listener.onClicked(ip);
        }
    }

    public interface OnServerInfoHolderClickListener {
        void onClicked(String ip);

        void onRedSignal(ImageView signal);
    }

}

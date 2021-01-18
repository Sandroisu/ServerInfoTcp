package ru.slatinin.serverinfotcp.ui;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;

import java.util.List;

import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.server.SingleInfo;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopList;
import ru.slatinin.serverinfotcp.server.serveriotop.SingleIoTop;

import static ru.slatinin.serverinfotcp.server.SingleInfo.IOTOP;
import static ru.slatinin.serverinfotcp.server.SingleInfo.NET;
import static ru.slatinin.serverinfotcp.server.SingleInfo.TOP;

public class ServerInfoAdapter extends RecyclerView.Adapter<ServerInfoAdapter.ServerInfoHolder> {
    private final List<SingleInfo> singleInfoList;
    protected OnServerInfoHolderClickListener onServerInfoHolderClickListener;

    public ServerInfoAdapter(List<SingleInfo> list, OnServerInfoHolderClickListener listener) {
        onServerInfoHolderClickListener = listener;
        singleInfoList = list;
    }

    @NonNull
    @Override
    public ServerInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_server_info, parent, false);
        return new ServerInfoHolder(view, onServerInfoHolderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerInfoHolder holder, int position) {
        holder.bind(singleInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return singleInfoList.size();
    }

    protected static class ServerInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LineChart lcCPU;
        private final BarChart bcMem;
        private final TextView tvIp;
        private final TableLayout tlIotop;
        private final TextView tvNet;
        private final TextView tvTime;
        private final ImageView ivSignal;
        private final ConstraintLayout clNet;
        private final ConstraintLayout clIoTop;
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
            ImageView ivPdf = itemView.findViewById(R.id.item_server_pdf);
            ChartUtil.initLineChart(lcCPU, true, false, true);
            ChartUtil.initBarChart(bcMem, false, false, false, true, Legend.LegendForm.CIRCLE);
            itemView.setOnClickListener(this);
            lcCPU.setOnClickListener(this);
            bcMem.setOnClickListener(this);
            ivPdf.setOnClickListener(this);
        }

        protected void bind(SingleInfo info) {
            tvTime.setText(ChartUtil.formatMillis(info.time));
            ivSignal.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_server_ok_signal_24));
            Handler handler = new Handler();
            handler.postDelayed(() -> listener.onRedSignal(ivSignal), 1000);
            if (ip == null) {
                ip = info.ip;
                tvIp.setText(ip);
            }
            switch (info.dataInfo) {
                case NET:
                    if (clNet.getVisibility() == View.GONE) {
                        clNet.setVisibility(View.VISIBLE);
                    }
                    String netInfo = info.getLastServerNET().getInfoString();
                    tvNet.setText(netInfo);
                    break;
                case TOP:
                    ChartUtil.updateTopBarChart(info.getServerTOP().serverMem, bcMem, true);
                    ChartUtil.updateCpuList(info.getServerCommonList(), lcCPU);
                    break;
                case IOTOP:
                    if (clIoTop.getVisibility() == View.GONE) {
                        clIoTop.setVisibility(View.VISIBLE);
                    }
                    tlIotop.removeAllViews();
                    TableRow namesRow = new TableRow(mContext);
                    for (int i = 0; i < ServerIoTopList.columnNames.length; i++) {
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 10, 0);
                        if (i > 0) {
                            layoutParams.gravity = Gravity.END;
                        }
                        TextView name = new TextView(mContext);
                        name.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border));
                        String text = ServerIoTopList.columnNames[i] + " ";
                        name.setText(text);
                        name.setLayoutParams(layoutParams);
                        name.setPadding(0, 0, 1,0);
                        name.setTextSize(12);
                        namesRow.addView(name);
                    }
                    tlIotop.addView(namesRow);
                    List<SingleIoTop> singleIoTopList = info.getServerIoTopList().serverIoTopList;
                    for (int i = 0; i < singleIoTopList.size(); i++) {
                        if (singleIoTopList.get(i).isMoreThenOne) {
                            List<String> values = singleIoTopList.get(i).serverIoTopMapList;
                            TableRow valueRow = new TableRow(mContext);
                            for (int j = 0; j < values.size(); j++) {
                                TableRow.LayoutParams params = new TableRow.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                                params.setMargins(0, 1, 10, 0);
                                TextView value = new TextView(mContext);
                                if (isNumeric(values.get(j))) {
                                    params.gravity = Gravity.END;
                                }
                                value.setLayoutParams(params);
                                value.setPadding(0, 0, 1,0);
                                value.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border));
                                String text = values.get(j) + " ";
                                value.setText(text);
                                value.setTextSize(12);
                                valueRow.addView(value);
                            }
                            tlIotop.addView(valueRow);
                        }
                    }
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null && ip != null) {
                if (v.getId() == R.id.item_server_pdf) {
                    listener.onOpenBrowser(ip);
                } else {
                    listener.onClicked(ip);
                }
            }
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public interface OnServerInfoHolderClickListener {
        void onClicked(String ip);

        void onOpenBrowser(String ip);

        void onRedSignal(ImageView signal);
    }
}

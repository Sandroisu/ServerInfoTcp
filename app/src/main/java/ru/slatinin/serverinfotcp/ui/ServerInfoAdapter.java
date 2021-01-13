package ru.slatinin.serverinfotcp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;

import java.util.List;

import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.server.SingleInfo;

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
        private final TextView tvIotop;
        private final TextView tvNet;
        private String ip;
        private final OnServerInfoHolderClickListener listener;

        public ServerInfoHolder(@NonNull View itemView, OnServerInfoHolderClickListener listener) {
            super(itemView);
            this.listener = listener;
            lcCPU = itemView.findViewById(R.id.item_server_cpu_info);
            tvIp = itemView.findViewById(R.id.item_server_ip);
            bcMem = itemView.findViewById(R.id.item_mem);
            tvIotop = itemView.findViewById(R.id.item_server_iotop);
            tvNet = itemView.findViewById(R.id.item_server_net);
            ChartUtil.initLineChart(lcCPU);
            ChartUtil.initBarChart(bcMem, false, false, false, true, Legend.LegendForm.CIRCLE);
            itemView.setOnClickListener(this);
            lcCPU.setOnClickListener(this);
            bcMem.setOnClickListener(this);
        }

        protected void bind(SingleInfo info) {
            if (ip == null) {
                ip = info.ip;
                tvIp.setText(ip);
            }
            switch (info.dataInfo) {
                case NET:
                    if (tvNet.getVisibility()==View.GONE){
                        tvNet.setVisibility(View.VISIBLE);
                    }
                    String netInfo =  info.getLastServerNET().getInfoString();
                    tvNet.setText(netInfo);
                    break;
                case TOP:
                    ChartUtil.updateTopBarChart(info.getServerTOP().serverMem, bcMem, true);
                    ChartUtil.updateCpuList(info.getServerCommonList(), lcCPU);
                    break;
                case IOTOP:
                    if (tvIotop.getVisibility()==View.GONE){
                        tvIotop.setVisibility(View.VISIBLE);
                    }
                    String ioTopInfo = info.getServerIoTopList().getInfoString();
                    tvIotop.setText(ioTopInfo);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null && ip != null) {
                listener.onClicked(ip);
            }
        }
    }

    public interface OnServerInfoHolderClickListener {
        void onClicked(String ip);
    }
}

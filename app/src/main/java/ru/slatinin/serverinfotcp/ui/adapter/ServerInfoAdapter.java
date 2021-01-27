package ru.slatinin.serverinfotcp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.server.SingleServer;

public class ServerInfoAdapter extends RecyclerView.Adapter<ServerInfoHolder> {
    private final List<SingleServer> singleServerList;
    protected final ServerInfoHolder.OnServerInfoHolderClickListener onServerInfoHolderClickListener;

    public ServerInfoAdapter(List<SingleServer> list, ServerInfoHolder.OnServerInfoHolderClickListener listener) {
        onServerInfoHolderClickListener = listener;
        singleServerList = list;
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
        holder.bind(singleServerList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return singleServerList.size();
    }


}

package ru.slatinin.serverinfotcp.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;
import java.util.Objects;

import ru.slatinin.serverinfotcp.DownloadPdfView;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.UrlUtil;
import ru.slatinin.serverinfotcp.server.serverpsql.ServerPsql;
import ru.slatinin.serverinfotcp.server.serverdf.ServerDFObjectKeeper;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopObjectKeeper;

public class ChoosePdfDialog extends DialogFragment implements DownloadPdfView.OnCloseDialogListener {
    private final String ip;
    private final String monitor;
    private final String parameter;
    private final String title;
    private final String [] names;
    private final int size;

    public ChoosePdfDialog(ServerDFObjectKeeper serverDFObjectKeeper, String ip) {
        this.ip = ip;
        monitor = "df";
        parameter = "&c_disk=";
        size = serverDFObjectKeeper.singleServerDFList.size();
        names = new String[size];
        for (int i = 0; i < size; i++) {
            names [i] = serverDFObjectKeeper.singleServerDFList.get(i).c_name;
        }
        title = "Выберите диск";
    }

    public ChoosePdfDialog(List<ServerPsql> serverPsqlList, String ip) {
        this.ip = ip;
        monitor = "psql";
        parameter = "&c_db=";
        size = serverPsqlList.size();
        names = new String[size];
        for (int i = 0; i < size; i++) {
            names [i] = serverPsqlList.get(i).c_datname;
        }
        title = "Выберите базу данных";
    }

    public ChoosePdfDialog(ServerIoTopObjectKeeper serverIoTopObjectKeeper, String ip) {
        this.ip = ip;
        monitor = "iotop";
        parameter = "&c_disk=";
        size = serverIoTopObjectKeeper.serverIoTopList.size();
        names = new String[size];
        for (int i = 0; i < size; i++) {
            names [i] = serverIoTopObjectKeeper.serverIoTopList.get(i).c_device;
        }
        title = "Выберите диск";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_choose_pdf, container, false);
        TextView tvTitle = v.findViewById(R.id.cp_title);
        tvTitle.setText(title);
        LinearLayout verticalContainer = v.findViewById(R.id.cp_vertical_container);
        for (int i = 0; i < size; i++) {
            String name = names[i];
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(25, 25, 25, 25);
            LinearLayout linearLayout = new LinearLayout(requireContext());
            linearLayout.setGravity(Gravity.END);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            verticalContainer.addView(linearLayout);
            LinearLayout.LayoutParams ivLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ivLayoutParams.setMargins(20, 25, 0, 0);
            TextView textView = new TextView(requireContext());
            textView.setText(name);
            textView.setLayoutParams(ivLayoutParams);
            linearLayout.addView(textView);
            DownloadPdfView downloadPdfView = new DownloadPdfView(requireActivity(),null);
            downloadPdfView.setLayoutParams(ivLayoutParams);
            linearLayout.addView(downloadPdfView);
            downloadPdfView.setOnCloseDialogListener(this);
            String fileName = ip + monitor + name + ".pdf";
            if (fileName.contains("/")){
                fileName = fileName.replace("/", "");
            }
            downloadPdfView.setUrl(UrlUtil.getUrl(ip, monitor, parameter + name, requireContext()), fileName);
        }
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getDialog())).getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onCloseDialog() {
        dismiss();
    }
}
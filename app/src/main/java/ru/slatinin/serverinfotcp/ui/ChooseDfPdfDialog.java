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
import ru.slatinin.serverinfotcp.server.ServerPSQL;
import ru.slatinin.serverinfotcp.server.serverdf.ServerDFList;

public class ChooseDfPdfDialog extends DialogFragment implements DownloadPdfView.OnCloseDialogListener {
    private ServerDFList serverDFList;
    private List<ServerPSQL> serverPSQLList;
    private final String ip;

    public ChooseDfPdfDialog(ServerDFList serverDFList, String ip) {
        this.serverDFList = serverDFList;
        this.ip = ip;
    }

    public ChooseDfPdfDialog(List<ServerPSQL> serverPSQLList, String ip) {
        this.serverPSQLList = serverPSQLList;
        this.ip = ip;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_choose_pdf, container, false);

        LinearLayout verticalContainer = v.findViewById(R.id.cp_vertical_container);
        boolean isDF = serverPSQLList == null;
        if (!isDF){
            TextView tvTitle = v.findViewById(R.id.cp_title);
            tvTitle.setText("Выберите базу данных");
        }
        int size = isDF ? serverDFList.singleServerDFList.size() : serverPSQLList.size();
        String monitor = isDF ? "df" : "psql";
        String parameter = isDF ? "&c_disk=" : "&c_db=";
        for (int i = 0; i < size; i++) {
            String name;
            if (isDF) {
                name = serverDFList.singleServerDFList.get(i).c_name;
            } else {
                name = serverPSQLList.get(i).c_datname;
            }
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
            downloadPdfView.setUrl(UrlUtil.getUrl(ip, monitor, parameter + name, requireContext()), ip + monitor + name + ".pdf");
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
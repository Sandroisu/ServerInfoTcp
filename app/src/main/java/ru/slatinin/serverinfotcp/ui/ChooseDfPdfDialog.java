package ru.slatinin.serverinfotcp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.List;
import java.util.Objects;

import ru.slatinin.serverinfotcp.App;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.server.ServerPSQL;
import ru.slatinin.serverinfotcp.server.serverdf.ServerDFList;

public class ChooseDfPdfDialog extends DialogFragment {
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
            ImageView imageView = new ImageView(requireContext());
            imageView.setLayoutParams(ivLayoutParams);
            imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf));
            linearLayout.addView(imageView);
            imageView.setOnClickListener(v1 -> {
                App app = (App) requireActivity().getApplication();
                if (app.getServerArgs() != null) {
                    String url = App.BASE_URL + app.getServerArgs().repos + "/%3Ahome%3Atcp%3A" + monitor + "-monitor.prpt/generatedContent?c_server="
                            + ip + parameter + name + "&userid=tcp&password=monitor-0&output-target=pageable/pdf";
                    if (url.contains("//")) {
                        url = url.replace("//", "/");
                    }
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(app, "Невозможно получить файл", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            });
        }
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getDialog())).getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

}
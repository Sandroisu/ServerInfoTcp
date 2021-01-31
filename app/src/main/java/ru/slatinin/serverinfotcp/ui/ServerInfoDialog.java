package ru.slatinin.serverinfotcp.ui;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

import ru.slatinin.serverinfotcp.App;
import ru.slatinin.serverinfotcp.R;

import static android.content.Context.MODE_PRIVATE;
import static ru.slatinin.serverinfotcp.ui.MainActivity.ADDRESS;
import static ru.slatinin.serverinfotcp.ui.MainActivity.PORT;
import static ru.slatinin.serverinfotcp.ui.MainActivity.SHARED_PREFS;

public class ServerInfoDialog extends DialogFragment {
    private final OnConnectAttempt onConnectAttempt;

    public ServerInfoDialog(OnConnectAttempt onConnectAttempt) {
        this.onConnectAttempt = onConnectAttempt;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_server_info, container, false);
        try {
            PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            String version = pInfo.versionName;
            TextView tvVersion = v.findViewById(R.id.dialog_fragment_version);
            tvVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final Button accept = v.findViewById(R.id.dialog_fragment_accept);
        final AutoCompleteTextView serverAddress = v.findViewById(R.id.dialog_fragment_address);
        final EditText serverPort = v.findViewById(R.id.dialog_fragment_port);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String address = sharedPreferences.getString(ADDRESS, "");
        String port = sharedPreferences.getString(PORT, "");
        if (!address.isEmpty()) {
            String [] addresses = address.split("\\)\\(");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, addresses);
            serverAddress.setAdapter(arrayAdapter);
            serverAddress.setThreshold(1);
            serverAddress.setOnFocusChangeListener((view, hasFocus) -> {
                if (view.getId() == R.id.dialog_fragment_address && hasFocus){
                    AutoCompleteTextView a = (AutoCompleteTextView) view;
                    a.showDropDown();
                }
            });
        }
        if (!port.isEmpty()) {
            serverPort.setText(port);
        }else {
            serverPort.setText(getResources().getString(R.string.default_port));
        }
        accept.setOnClickListener(v1 -> {
            if (serverAddress.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Заполните поле адрес сервера", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverAddress.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Заполните поле порт сервера", Toast.LENGTH_SHORT).show();
                return;
            }
            FragmentActivity activity = requireActivity();
            App app = (App) activity.getApplication();
            app.connect(serverAddress.getText().toString(), serverPort.getText().toString());
            onConnectAttempt.onConnectAttempt(serverAddress.getText().toString());
            dismiss();
        });
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getDialog())).getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

}

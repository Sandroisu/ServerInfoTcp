package ru.slatinin.serverinfotcp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

import ru.slatinin.serverinfotcp.App;
import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.ui.MainActivity;

import static android.content.Context.MODE_PRIVATE;
import static ru.slatinin.serverinfotcp.ui.MainActivity.ADDRESS;
import static ru.slatinin.serverinfotcp.ui.MainActivity.PORT;
import static ru.slatinin.serverinfotcp.ui.MainActivity.SHARED_PREFS;

public class ServerInfoDialog extends DialogFragment {
    private OnConnectAttempt onConnectAttempt;

    public ServerInfoDialog(OnConnectAttempt onConnectAttempt) {
        this.onConnectAttempt = onConnectAttempt;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_server_info, container, false);
        final Button close = v.findViewById(R.id.dialog_fragment_accept);
        final EditText serverAddress = v.findViewById(R.id.dialog_fragment_address);
        final EditText serverPort = v.findViewById(R.id.dialog_fragment_port);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String address = sharedPreferences.getString(ADDRESS, "");
        String port = sharedPreferences.getString(PORT, "");
        if (!emptyOrNull(address)) {
            serverAddress.setText(address);
        }
        if (!emptyOrNull(port)) {
            serverPort.setText(port);
        }
        close.setOnClickListener(v1 -> {
            if (emptyOrNull(serverAddress.getText().toString())) {
                Toast.makeText(requireContext(), "Заполните поле адрес сервера", Toast.LENGTH_SHORT).show();
                return;
            }
            if (emptyOrNull(serverAddress.getText().toString())) {
                Toast.makeText(requireContext(), "Заполните поле порт сервера", Toast.LENGTH_SHORT).show();
                return;
            }
            FragmentActivity activity = requireActivity();
            if (activity != null) {
                App app = (App) activity.getApplication();
                app.connect(serverAddress.getText().toString(), serverPort.getText().toString());
                onConnectAttempt.onConnectAttempt();
                dismiss();
            }
        });
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getDialog())).getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private boolean emptyOrNull(String toBeChecked) {
        return toBeChecked.isEmpty() || toBeChecked == null;
    }
}

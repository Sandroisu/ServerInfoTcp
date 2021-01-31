package ru.slatinin.serverinfotcp.ui;

import androidx.appcompat.app.AppCompatActivity;

import ru.slatinin.serverinfotcp.App;

public class BaseActivity  extends AppCompatActivity {


    @Override
    protected void onDestroy() {
        super.onDestroy();
        App app = (App) getApplication();
        app.stopTcpService();
    }
}

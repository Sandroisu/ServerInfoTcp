package ru.slatinin.serverinfotcp.server.serverdf;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverdf.SingleServerDF;

public class ServerDFObjectKeeper {


    public final List<SingleServerDF> singleServerDFList;

    public ServerDFObjectKeeper() {
        singleServerDFList = new ArrayList<>();

    }

    public void update(JsonObject[] objects) {
        if (singleServerDFList.size() == 0) {
            for (JsonObject obj : objects) {
                singleServerDFList.add(new SingleServerDF(obj));
            }
            return;
        }
        if (objects.length == singleServerDFList.size()) {
            for (int i = 0; i < objects.length; i++) {
                singleServerDFList.set(i, new SingleServerDF(objects[i]));
            }
        } else {
            singleServerDFList.clear();
            update(objects);
        }
    }
}

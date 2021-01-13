package ru.slatinin.serverinfotcp.server.serverdf;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.server.serverdf.SingleServerDF;

public class ServerDFList{


    public final List<SingleServerDF> singleServerDFList;

    public ServerDFList(JsonObject[] object) {
        singleServerDFList = new ArrayList<>();
        for (JsonObject obj : object) {
            singleServerDFList.add(new SingleServerDF(obj));
        }
    }
}

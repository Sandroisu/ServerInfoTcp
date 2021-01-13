package ru.slatinin.serverinfotcp.server.serveriotop;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static ru.slatinin.serverinfotcp.server.SingleInfo.IOTOP;

public class ServerIoTopList {


    public final List<SingleIoTop> serverIoTopList;

    public ServerIoTopList(JsonObject[] object) {

        serverIoTopList = new ArrayList<>();
        for (JsonObject obj : object) {
            serverIoTopList.add(new SingleIoTop(obj));
        }
    }

    public String getInfoString (){
        StringBuilder builder = new StringBuilder();
        builder.append(IOTOP).append(":\t");
        for (SingleIoTop ioTop:serverIoTopList) {
            builder.append(ioTop.getInfoString()).append("\n");
        }
        return builder.toString();
    }
}

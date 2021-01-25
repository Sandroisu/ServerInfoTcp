package ru.slatinin.serverinfotcp;

import com.google.gson.JsonObject;

public class UpdateMessage {
    public final String ip;
    public final String dataInfo;
    public final JsonObject [] objects;

    public UpdateMessage(String ip, String dataInfo, JsonObject [] objects){
        this.ip = ip;
        this.dataInfo = dataInfo;
        this.objects = objects;
    }
}

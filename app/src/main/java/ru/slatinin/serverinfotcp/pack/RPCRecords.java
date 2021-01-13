package ru.slatinin.serverinfotcp.pack;

import com.google.gson.JsonObject;

public class RPCRecords {
    /**
     * список записей
     */
    public JsonObject[] records;

    /**
     * количество записей
     */
    public int total;
}

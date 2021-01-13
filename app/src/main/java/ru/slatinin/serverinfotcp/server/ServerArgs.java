package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;

public class ServerArgs {
    private final static String PORT = "port";
    private final static String HOST = "host";
    private final static String CONNECTION_STRING = "connection_string";
    private final static String DEBUG = "debug";
    private final static String REPOS = "repos";

    public final int port;
    public final String host;
    public final String connection_string;
    public final boolean debug;
    public final String repos;

    public ServerArgs(JsonObject object){
        port = JsonUtil.getInt(object, PORT);
        host = JsonUtil.getString(object, HOST);
        connection_string = JsonUtil.getString(object, CONNECTION_STRING);
        debug = JsonUtil.getBoolean(object, DEBUG);
        repos = JsonUtil.getString(object, REPOS);
    }
}

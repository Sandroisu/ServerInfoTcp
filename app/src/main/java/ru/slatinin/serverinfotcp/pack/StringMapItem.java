package ru.slatinin.serverinfotcp.pack;

import com.google.gson.annotations.Expose;

public class StringMapItem {
    @Expose
    public final int length;
    @Expose
    public final String name;

    public StringMapItem(String name, int length) {
        this.name = name;
        this.length = length;
    }
}

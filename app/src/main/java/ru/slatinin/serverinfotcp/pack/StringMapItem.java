package ru.slatinin.serverinfotcp.pack;

import com.google.gson.annotations.Expose;

public class StringMapItem {
    @Expose
    public int length;
    @Expose
    public String name;

    public StringMapItem(String name, int length) {
        this.name = name;
        this.length = length;
    }
}

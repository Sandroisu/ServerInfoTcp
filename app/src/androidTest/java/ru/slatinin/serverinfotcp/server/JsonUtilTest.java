package ru.slatinin.serverinfotcp.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
public class JsonUtilTest {
    private JsonObject jsonIntAndString;
    private JsonObject jsonFloatWithComma;

    @Before
    public void setUp() {
        String intAndString = "{\"time\":\"15:31:47\",\"up_hours\":\"2:10\",\"up_days\":\"13\",\"user\":\"0\",\"load_average\":[\"0,12\",\"0,06\",\"0,01\"]}";
        jsonIntAndString = JsonParser.parseString(intAndString).getAsJsonObject();
        String floatWithComma = "{\"us\":\"0,2\",\"sy\":\"0,1\",\"ni\":\"0,0\",\"id\":\"99,7\",\"wa\":\"0,0\",\"hi\":\"0,0\",\"si\":\"0,0\",\"st\":\"0,0\"}";
        jsonFloatWithComma = JsonParser.parseString(floatWithComma).getAsJsonObject();
    }

    @Test
    public void getString() {
        String time = JsonUtil.getString(jsonIntAndString, "time");
        assertEquals("15:31:47", time);
        String empty = JsonUtil.getString(jsonIntAndString, "nothing");
        assertEquals("", empty);
    }

    @Test
    public void getInt() {
        int days = JsonUtil.getInt(jsonIntAndString, "up_days");
        assertEquals(13, days);
        int zero = JsonUtil.getInt(jsonIntAndString, "nothing");
        assertEquals(0, zero);
    }

    @Test
    public void getFloat() {
        float us = JsonUtil.getFloat(jsonFloatWithComma, "us");
        assertEquals(0.2f, us, 0f);
        float zero = JsonUtil.getFloat(jsonFloatWithComma, "nothing");
        assertEquals(0f, zero, 0f);
    }


}
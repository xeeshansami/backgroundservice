package com.paxees.sms.network.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonProvider {

    private static Gson gson = null;

    public static Gson getInstance() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Long.class, new LongTypeAdapter())
                    .registerTypeAdapter(Integer.class, new IntTypeAdapter())
                    .registerTypeAdapter(Float.class, new FloatTypeAdapter())
                    .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
                    .setLenient()
                    .create();
        }
        return gson;
    }


    private GsonProvider() {

    }
}

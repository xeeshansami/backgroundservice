package com.paxees.sms.network;

import okhttp3.logging.HttpLoggingInterceptor;

public class Config {
    public static final String BASE_URL = "https://www.metazuhu.com/";
    public static final long timeoutNormal = 60;
    public static final long API_CONNECT_TIMEOUT = timeoutNormal;
    public static final long API_PDF_CONNECT_TIME_OUT = timeoutNormal;
    public static final HttpLoggingInterceptor.Level LOG_LEVEL_API = HttpLoggingInterceptor.Level.BODY;
}

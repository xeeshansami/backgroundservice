package com.hbl.hblaccountopeningapp.network.retrofitBuilder

import android.content.Context
import com.paxees.sms.network.APIInterface
import com.hbl.hblaccountopeningapp.network.enums.RetrofitEnums
import com.paxees.sms.network.Config
import com.paxees.sms.network.gson.GsonProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private val retrofitHashMap = HashMap<String, APIInterface>()
    fun getRetrofitInstance(context: Context, url: RetrofitEnums, timeout: Long): APIInterface {
        val baseUrl = url.url
//        val okHttpClient =
//            SafeSLLOkHttpsClient.getUnsafeOkHttpClient(context,
//                enableNetworkInterceptor(baseUrl),
//                timeout)
    val okHttpClient =
            getOkHttpClient(context,
                enableNetworkInterceptor(baseUrl),
                timeout)
        if (!retrofitHashMap.containsKey(baseUrl)
            || retrofitHashMap[baseUrl] == null
        ) {
            synchronized(this) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GsonProvider.getInstance()))
                    .client(okHttpClient)
                val restAPI = retrofit.build().create<APIInterface>(APIInterface::class.java)
                retrofitHashMap[baseUrl] = restAPI
                return restAPI
            }
        }
        return retrofitHashMap[baseUrl]!!
    }

    private fun enableNetworkInterceptor(baseUrl: String): Boolean {
        return baseUrl == RetrofitEnums.URL_HBL.url
    }

    private fun getOkHttpClient(context: Context, isHblLink: Boolean, timeout: Long): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = Config.LOG_LEVEL_API
        }
        val builder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
//            .addInterceptor(ChuckInterceptor(context))
            .callTimeout(timeout, TimeUnit.SECONDS)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
        builder.addNetworkInterceptor(NetworkInterceptorHBL())
        return builder.build()
    }

    public class NetworkInterceptorHBL() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val builder = original.newBuilder()
            val request = builder
                .removeHeader(APIInterface.HEADER_TAG)
                .method(original.method, original.body)
                .build()
            val response = chain.proceed(request)
            return response
        }

    }
}

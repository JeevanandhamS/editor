package com.pratilipi.editor.rest;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by jeeva on 16-09-2016.
 */
public class BaseRestService {

    public Retrofit createRetrofitWithGsonConverter(String baseUrl) {
        GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        return createRetrofit(baseUrl, GsonConverterFactory.create(gsonBuilder.create()));
    }

    private Retrofit createRetrofit(String baseUrl, Converter.Factory converterFactory) {
        if(!baseUrl.endsWith("/")){
            baseUrl = baseUrl + "/";
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .client(createHttpClient())
                .build();
        return retrofit;
    }

    public OkHttpClient createHttpClient() {
        // Create OkHttpClient
        return new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder requestBuilder = chain.request().newBuilder();
                        requestBuilder.header("Content-Type", "application/json");
                        return chain.proceed(requestBuilder.build());
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }
}
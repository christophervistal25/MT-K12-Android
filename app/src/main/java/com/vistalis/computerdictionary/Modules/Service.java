package com.vistalis.computerdictionary.Modules;
import android.content.Context;


import com.vistalis.computerdictionary.R;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {
    public static Retrofit RetrofitInstance(Context context)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.baseUrl))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
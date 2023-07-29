package com.example.positocabs.Remote;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static Retrofit getInstance() {

        return instance == null ? new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build() : instance;
    }
}

package com.example.chatbox.services;

// RetrofitClient.java
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetrofitClient {
    private static final String BASE_URL = "https://mad-400997217786.europe-west1.run.app/";
    private static Retrofit retrofitInstance;

    public static Retrofit getRetrofitInstance() {
        if (retrofitInstance == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitInstance;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
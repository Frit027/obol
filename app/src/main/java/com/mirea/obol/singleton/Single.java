package com.mirea.obol.singleton;

import android.app.Application;

import com.mirea.obol.utilities.Constants;
import com.mirea.obol.api.CurrencyAPI;
import com.mirea.obol.api.Rate;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Single extends Application {

    private static Single instance;
    private CurrencyAPI currencyApi;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        currencyApi = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CurrencyAPI.class);
    }

    public static Single getInstance() {
        return instance;
    }

    public Call<Rate> getRate(String currency) {
        return currencyApi.getRate(currency, Constants.RUB);
    }
}

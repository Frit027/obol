package com.mirea.obol.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrencyAPI {
    @GET("latest?")
    Call<Rate> getRate(@Query("base") String base,
                       @Query("symbols") String symbol);
}

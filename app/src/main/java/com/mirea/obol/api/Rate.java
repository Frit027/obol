package com.mirea.obol.api;

import com.google.gson.annotations.SerializedName;

public class Rate {
    @SerializedName("rates")
    public Ruble ruble;

    public class Ruble {
        @SerializedName("RUB")
        public double rub;
    }
}

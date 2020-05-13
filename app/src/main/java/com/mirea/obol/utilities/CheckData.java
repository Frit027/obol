package com.mirea.obol.utilities;

import android.content.Context;

public class CheckData {

    public static boolean checkName(String name) {
        return name.matches("^[\\s0-9A-Za-zА-Яа-я]+$");
    }

    public static boolean checkPrice(String price) {
        return price.length() == 0;
    }

    public static boolean checkData(Context context, String name, String price) {
        if (checkPrice(price)) {
            MyToast.showToast(context, Constants.TOAST_PRICE_TEXT);
            return false;
        }
        if (!checkName(name)) {
            MyToast.showToast(context, Constants.TOAST_NAME_TEXT);
            return false;
        }
        return true;
    }
}

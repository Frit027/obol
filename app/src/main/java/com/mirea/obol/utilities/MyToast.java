package com.mirea.obol.utilities;

import android.content.Context;
import android.view.Gravity;

public class MyToast {

    public static void showToast(Context context, String text) {
        android.widget.Toast toast = android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0,550);
        toast.show();
    }
}

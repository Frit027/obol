package com.mirea.obol.utilities;

public class Constants {
    public static final String FOOD = "Продукты";
    public static final String CLOTHES = "Одежда";
    public static final String TRANSPORT = "Транспорт";
    public static final String CAFE_AND_RESTAURANTS = "Кафе и рестораны";
    public static final String HOME = "Дом";
    public static final String DEVICE = "Техника";
    public static final String ENTERTAINMENT = "Развлечения";

    public static final String BASE_URL = "https://api.exchangeratesapi.io/";
    public static final String RUB = "RUB";
    public static final String USD = "USD";
    public static final String EUR = "EUR";
    public static final char SIGN_RUB = '\u20BD';
    public static final char SIGN_USD = '\u0024';
    public static final char SIGN_EUR = '\u20AC';

    public static final String[] SIGN_ARR = new String[] {"RUB", "USD", "EUR"};

    public static final String TAG_DIALOG_DESCRIPTION = "DIALOG_DESCRIPTION";
    public static final String TAG_DIALOG_NEW_ITEM = "DIALOG_NEW_ITEM";
    public static final String TAG_DIALOG_ITEM = "DIALOG_ITEM";
    public static final String TAG_DIALOG_INCOME = "DIALOG_INCOME";
    public static final String TAG_DIALOG_CATEGORY = "DIALOG_CATEGORY";

    public static final String FRAGMENT_ITEMS_TAG = "FRAGMENT_ITEMS_TAG";

    public static final boolean WITH_SIGN = true;
    public static final boolean NO_SIGN = false;

    public static final String TOAST_NAME_TEXT = "Название может состоять только из русских или английских букв и цифр.";
    public static final String TOAST_PRICE_TEXT = "Поле цены должно быть заполнено.";
    public static final String TOAST_INCOME_TEXT = "Поле дохода должно быть заполнено.";
    public static final String TOAST_INTERNET = "Проблемы с доступом в Интернет.";
}

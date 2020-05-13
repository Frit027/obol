package com.mirea.obol.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mirea.obol.utilities.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Database {

    private static final String DATABASE_NAME = "finance.db";
    private static final int SCHEMA = 1;

    private static final String ITEM_TABLE = "item";
    private static final String CATEGORY_TABLE = "category";
    private static final String BUDGET_TABLE = "budget";
    private static final String SPENT_TABLE = "spent";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_RUB_PRICE = "rub_price";
    private static final String COLUMN_CURRENCY = "currency";
    private static final String COLUMN_DATE = "date";

    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_CATEGORY_ID = "category_id";

    private static final String COLUMN_BUDGET = "budget";

    private static final String COLUMN_SPENT = "spent";

    private Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase dbReader;
    private SQLiteDatabase dbWriter;

    public Database(Context context) {
        this.context = context;
        open();
    }

    private void open() {
        databaseHelper = new DatabaseHelper(context);
        dbReader = databaseHelper.getReadableDatabase();
        dbWriter = databaseHelper.getWritableDatabase();
    }

    public void close() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    public void insertItem(String name, double rubPrice, String price, String category, String currency) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_PRICE, Double.valueOf(price));
        cv.put(COLUMN_RUB_PRICE, rubPrice);
        cv.put(COLUMN_CATEGORY_ID, getIdOfCategory(category));
        cv.put(COLUMN_CURRENCY, currency);

        dbWriter.insert(ITEM_TABLE, null, cv);
        insertSpent(rubPrice);
    }

    public void insertBudget(String budgetStr) {
        double budget = Double.valueOf(budgetStr);
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_BUDGET +
                " from " + BUDGET_TABLE,null);

        cursor.moveToFirst();
        budget += cursor.getDouble(0);
        cursor.close();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BUDGET, budget);
        dbWriter.update(BUDGET_TABLE, cv, COLUMN_ID + "=1", null);
    }

    private void insertSpent(double price) {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_SPENT +
                                             " from " + SPENT_TABLE,null);

        cursor.moveToFirst();
        price += cursor.getDouble(0);
        cursor.close();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SPENT, price);
        dbWriter.update(SPENT_TABLE, cv, COLUMN_ID + "=1", null);
    }

    public void insertCategory(String category) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CATEGORY, category);

        dbWriter.insert(CATEGORY_TABLE, null, cv);
    }

    public String getTotalPriceForCurrentMonth(String currency) {
        DateFormat dateFormat = new SimpleDateFormat("MM", Locale.US);
        Date date = new Date();
        String month = dateFormat.format(date);

        double totalPrice = 0.0;
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_PRICE + " from " + ITEM_TABLE + " where "  +
                                            COLUMN_CURRENCY + " = '" + currency + "' and " +
                                            "strftime('%m', " + COLUMN_DATE + ") = '" + month + "'",null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double price = cursor.getDouble(0);
                totalPrice += price;
                cursor.moveToNext();
            }
        }

        cursor.close();
        return String.format(Locale.getDefault(), "%.2f", totalPrice)
                     .replace('.', ',') + " " + getSignCurrency(currency);
    }

    public int getCountItems() {
        return (int) DatabaseUtils.queryNumEntries(dbReader, ITEM_TABLE);
    }

    public List<Integer> getAllIdDesc() {
        List<Integer> list = new ArrayList<>();
        Cursor cursor = dbReader.query(ITEM_TABLE, new String[] { COLUMN_ID },
                            null, null, null,
                              null, COLUMN_DATE + " DESC");

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                list.add(id);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return list;
    }

    public String getNameById(int id) {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_NAME + " from " + ITEM_TABLE + " where " +
                COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String name = cursor.getString(0);
        cursor.close();

        return name;
    }

    public String getPriceById(int id, boolean isWithSign) {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_PRICE +
                                            " from " + ITEM_TABLE +
                                            " where " + COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        double price = cursor.getDouble(0);
        String currency = getCurrencyById(id);
        cursor.close();

        String priceStr = String.format(Locale.getDefault(), "%.2f", price);
        return isWithSign ? priceStr.replace('.', ',') + " " + getSignCurrency(currency)
                          : priceStr.replace(',', '.');
    }

    private double getRubPriceById(int id) {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_RUB_PRICE +
                                            " from " + ITEM_TABLE +
                                            " where " + COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        double price = cursor.getDouble(0);
        cursor.close();

        return price;
    }

    public String getCurrencyById(int id) {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_CURRENCY +
                                            " from " + ITEM_TABLE +
                                            " where " + COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String currency = cursor.getString(0);
        cursor.close();

        return currency;
    }


    public String getCategoryById(int id) {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_CATEGORY_ID + " from " + ITEM_TABLE + " where " +
                                    COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        int category_id = cursor.getInt(0);

        cursor = dbReader.rawQuery("select " + COLUMN_CATEGORY + " from " + CATEGORY_TABLE + " where " +
                COLUMN_ID + "=?", new String[]{String.valueOf(category_id)});
        cursor.moveToFirst();
        String category = cursor.getString(0);
        cursor.close();

        return category;
    }

    private int getIdOfCategory(String category) {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_ID + " from " + CATEGORY_TABLE + " where " +
                COLUMN_CATEGORY + " = '" + category + "'", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);

        cursor.close();
        return id;
    }

    public List<String> getCategoriesList() {
        List<String> categories = new ArrayList<>();
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_CATEGORY +
                                            " from " + CATEGORY_TABLE,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String category = cursor.getString(0);
                categories.add(category);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return categories;
    }

    public String getDate(int id) {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_DATE + " from " + ITEM_TABLE + " where " +
                                    COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String date = cursor.getString(0);
        cursor.close();

        return date;
    }

    public String getPriceForMonth() {
        DateFormat dateFormat = new SimpleDateFormat("MM", Locale.US);
        Date date = new Date();
        String month = dateFormat.format(date);

        double totalPrice = 0.0;
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_RUB_PRICE + " from " + ITEM_TABLE +
                                        " where strftime('%m', " + COLUMN_DATE + ") = '" + month + "'",null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double price = cursor.getDouble(0);
                totalPrice += price;
                cursor.moveToNext();
            }
        }

        cursor.close();
        return String.format(Locale.getDefault(), "%.2f", totalPrice)
                .replace('.', ',') + " " + Constants.SIGN_RUB;
    }

    private double getSpent() {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_SPENT +
                " from " + SPENT_TABLE,null);

        cursor.moveToFirst();
        double spent = cursor.getDouble(0);
        cursor.close();

        return spent;
    }

    private double getBudget() {
        Cursor cursor = dbReader.rawQuery("select " + COLUMN_BUDGET +
                                             " from " + BUDGET_TABLE,null);

        cursor.moveToFirst();
        double budget = cursor.getDouble(0);
        cursor.close();

        return budget;
    }

    public String getBalance() {
        return String.format(Locale.getDefault(), "%.2f", getBudget() - getSpent())
                .replace('.', ',') + " " + Constants.SIGN_RUB;
    }

    private char getSignCurrency(String currency) {
        char cur = ' ';
        switch (currency) {
            case Constants.RUB:
                cur = Constants.SIGN_RUB;
                break;
            case Constants.USD:
                cur = Constants.SIGN_USD;
                break;
            case Constants.EUR:
                cur = Constants.SIGN_EUR;
                break;
        }
        return cur;
    }

    public void updateItem(int id, String name, String price, double rubPrice, String currency, String category) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_PRICE, Double.valueOf(price));
        cv.put(COLUMN_RUB_PRICE, rubPrice);
        cv.put(COLUMN_CURRENCY, currency);
        cv.put(COLUMN_CATEGORY_ID, getIdOfCategory(category));

        insertBudget(String.valueOf(getRubPriceById(id)));
        insertSpent(rubPrice);
        dbWriter.update(ITEM_TABLE, cv, COLUMN_ID + "=" + id, null);
    }

    public void deleteItemById(int id) {
        insertBudget(String.valueOf(getRubPriceById(id)));
        dbWriter.delete(ITEM_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, SCHEMA);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + ITEM_TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PRICE + " REAL, " +
                    COLUMN_RUB_PRICE + " REAL, " +
                    COLUMN_CURRENCY + " TEXT, " +
                    COLUMN_CATEGORY_ID + " INTEGER, " +
                    COLUMN_DATE + " DATETIME DEFAULT CURRENT_DATE);");

            db.execSQL("CREATE TABLE " + CATEGORY_TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CATEGORY + " TEXT);");

            db.execSQL("CREATE TABLE " + BUDGET_TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_BUDGET + " REAL);");

            db.execSQL("CREATE TABLE " + SPENT_TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_SPENT + " REAL);");

            fillCategoryTable(db);
            fillBudgetTable(db);
            fillSpentTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + BUDGET_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SPENT_TABLE);
            onCreate(db);
        }

        private void fillCategoryTable(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();

            String[] arr = new String[] { Constants.FOOD, Constants.CLOTHES, Constants.TRANSPORT,
                                               Constants.CAFE_AND_RESTAURANTS, Constants.HOME,
                                               Constants.DEVICE, Constants.ENTERTAINMENT };
            for (String str : arr) {
                cv.put(COLUMN_CATEGORY, str);
                db.insert(CATEGORY_TABLE, null, cv);
            }
        }

        private void fillBudgetTable(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ID, 1);
            cv.put(COLUMN_BUDGET, 0.0);
            db.insert(BUDGET_TABLE, null, cv);
        }

        private void fillSpentTable(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ID, 1);
            cv.put(COLUMN_SPENT, 0.0);
            db.insert(SPENT_TABLE, null, cv);
        }
    }
}

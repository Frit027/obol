package com.mirea.obol.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mirea.obol.utilities.Constants;
import com.mirea.obol.utilities.MyToast;
import com.mirea.obol.R;
import com.mirea.obol.singleton.Single;
import com.mirea.obol.api.Rate;
import com.mirea.obol.database.Database;
import com.mirea.obol.dialogs.DialogAddition;
import com.mirea.obol.dialogs.DialogItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DialogAddition.DialogAdditionListener,
                                                                DialogItem.DialogItemListener {
    private Database db;
    private OnActivityDataListener mAddListener;

    public interface OnActivityDataListener {
        void onActivityDataListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(getApplicationContext());

        final FragmentManager fm = getSupportFragmentManager();
        final FragmentItems fragmentItems = new FragmentItems();

        final FragmentAddition fragmentAddition = new FragmentAddition();

        if (savedInstanceState == null) {
            mAddListener = fragmentItems;
            fm.beginTransaction()
              .add(R.id.container, fragmentItems, Constants.FRAGMENT_ITEMS_TAG)
              .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_list:
                        fm.beginTransaction()
                                .replace(R.id.container, fragmentItems, Constants.FRAGMENT_ITEMS_TAG)
                                .commit();
                        break;
                    case R.id.action_add:
                        fm.beginTransaction()
                                .replace(R.id.container, fragmentAddition)
                                .commit();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onDialogAdditionPositiveClick(String name, String price,
                                              String category, String currency) {
        if (!currency.equals(Constants.RUB)) {
            insertItem(name, price, category, currency);
        } else {
            db.insertItem(name, Double.valueOf(price), price, category, currency);
        }
    }

    @Override
    public void onDialogItemDeleteClick() {
        updateFragment();
    }

    @Override
    public void onDialogItemUpdateClick(int id, String name, String price,
                                        String category, String currency) {
        if (!currency.equals(Constants.RUB)) {
            updateItem(id, name, price, category, currency);
        } else {
            db.updateItem(id, name, price, Double.valueOf(price), currency, category);
            updateList();
        }
    }

    private void insertItem(final String name, final String price,
                             final String category, final String currency) {
        Call<Rate> rate = Single.getInstance().getRate(currency);
        rate.enqueue(new Callback<Rate>() {
            @Override
            public void onResponse(@NonNull Call<Rate> call, @NonNull Response<Rate> response) {
                double rubPrice = currencyTransfer(price, response.body().ruble.rub);
                db.insertItem(name, rubPrice, price, category, currency);
                updateList();
            }

            @Override
            public void onFailure(@NonNull Call<Rate> call, @NonNull Throwable t) {
                MyToast.showToast(getApplicationContext(), Constants.TOAST_INTERNET);
            }
        });
    }

    private void updateItem(final int id, final String name, final String price,
                            final String category, final String currency) {
        Call<Rate> rate = Single.getInstance().getRate(currency);
        rate.enqueue(new Callback<Rate>() {
            @Override
            public void onResponse(@NonNull Call<Rate> call, @NonNull Response<Rate> response) {
                double rubPrice = currencyTransfer(price, response.body().ruble.rub);
                db.updateItem(id, name, price, rubPrice, currency, category);
                updateList();
            }

            @Override
            public void onFailure(@NonNull Call<Rate> call, @NonNull Throwable t) {
                MyToast.showToast(getApplicationContext(), Constants.TOAST_INTERNET);
            }
        });
    }

    private void updateFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_ITEMS_TAG);
        if (currentFragment instanceof FragmentItems) {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }
    }

    private double currencyTransfer(String price, double rub) {
        return Double.valueOf(price) * rub;
    }

    private void updateList() {
        mAddListener.onActivityDataListener();
        updateFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}

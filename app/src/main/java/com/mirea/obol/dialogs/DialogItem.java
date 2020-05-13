package com.mirea.obol.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mirea.obol.utilities.CheckData;
import com.mirea.obol.utilities.Constants;
import com.mirea.obol.R;
import com.mirea.obol.database.Database;

import java.util.Arrays;

public class DialogItem extends DialogFragment {

    private String category;
    private String currency;
    private Database db;
    private int id;
    private DialogItemListener mListener;

    public DialogItem(Database db, int id) {
        this.db = db;
        this.id = id;
    }

    public interface DialogItemListener {
        void onDialogItemDeleteClick();
        void onDialogItemUpdateClick(int id, String name, String price,
                                     String category, String currency);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (DialogItemListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogAdditionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_new_item, null);

        final EditText nameBox = view.findViewById(R.id.nameBox);
        nameBox.setText(db.getNameById(id));

        final EditText priceBox = view.findViewById(R.id.priceBox);
        priceBox.setText(db.getPriceById(id, Constants.NO_SIGN));

        setSpinners(view);

        builder.setTitle("Изменение товара")
                .setView(view)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = nameBox.getText().toString();
                        String price = priceBox.getText().toString();

                        if (CheckData.checkData(getContext(), name, price)) {
                            mListener.onDialogItemUpdateClick(id, name, price, category, currency);
                        }
                    }
                })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.deleteItemById(id);
                        mListener.onDialogItemDeleteClick();
                        dialogInterface.cancel();
                    }
                });

        return builder.create();
    }

    private void setSpinners(View view) {
        Spinner spinnerCategories = view.findViewById(R.id.spinnerCategories);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item,
                                                            db.getCategoriesList());

        spinnerCategories.setAdapter(adapter);
        spinnerCategories.setSelection(db.getCategoriesList().indexOf(db.getCategoryById(id)));
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        Spinner spinnerCurrencies = view.findViewById(R.id.spinnerCurrencies);

        ArrayAdapter<String> adapterCurrencies = new ArrayAdapter<>(getContext(), R.layout.spinner_item,
                Constants.SIGN_ARR);

        spinnerCurrencies.setAdapter(adapterCurrencies);
        spinnerCurrencies.setSelection(Arrays.asList(Constants.SIGN_ARR).indexOf(db.getCurrencyById(id)));
        spinnerCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currency = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }
}

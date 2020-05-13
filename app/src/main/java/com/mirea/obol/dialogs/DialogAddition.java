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

public class DialogAddition extends DialogFragment {

    private String category;
    private String currency;

    public interface DialogAdditionListener {
        void onDialogAdditionPositiveClick(String name, String price,
                                           String category, String currency);
    }

    private DialogAdditionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (DialogAdditionListener) context;
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
        final EditText priceBox = view.findViewById(R.id.priceBox);

        setSpinners(view);

        builder.setTitle("Новая покупка")
               .setView(view)
               .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = nameBox.getText().toString();
                        String price = priceBox.getText().toString();

                        if (CheckData.checkData(getContext(), name, price)) {
                            mListener.onDialogAdditionPositiveClick(name, price, category, currency);
                        }
                    }
                })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.cancel();
                    }
                });

        return builder.create();
    }

    private void setSpinners(View view) {
        Spinner spinnerCategories = view.findViewById(R.id.spinnerCategories);

        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(getContext(), R.layout.spinner_item,
                                                                    new Database(getContext()).getCategoriesList());

        spinnerCategories.setAdapter(adapterCategories);
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

package com.mirea.obol.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirea.obol.utilities.Constants;
import com.mirea.obol.R;
import com.mirea.obol.dialogs.DialogAddition;
import com.mirea.obol.dialogs.DialogCategory;
import com.mirea.obol.dialogs.DialogDescription;
import com.mirea.obol.dialogs.DialogIncome;

public class FragmentAddition extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.addition_fragmet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button buttonDescription = view.findViewById(R.id.app);
        buttonDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDescription dialogDescription = new DialogDescription();
                dialogDescription.show(getFragmentManager(), Constants.TAG_DIALOG_DESCRIPTION);
            }
        });

        Button buttonItem = view.findViewById(R.id.buttonAddItem);
        buttonItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAddition dialogAddition = new DialogAddition();
                dialogAddition.show(getFragmentManager(), Constants.TAG_DIALOG_NEW_ITEM);
            }
        });

        Button buttonIncome = view.findViewById(R.id.buttonAddIncome);
        buttonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogIncome dialogIncome = new DialogIncome();
                dialogIncome.show(getFragmentManager(), Constants.TAG_DIALOG_INCOME);
            }
        });

        Button buttonCategory = view.findViewById(R.id.buttonAddCategory);
        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogCategory dialogCategory = new DialogCategory();
                dialogCategory.show(getFragmentManager(), Constants.TAG_DIALOG_CATEGORY);
            }
        });
    }
}

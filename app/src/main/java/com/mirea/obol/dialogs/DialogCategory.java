package com.mirea.obol.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mirea.obol.utilities.CheckData;
import com.mirea.obol.utilities.Constants;
import com.mirea.obol.utilities.MyToast;
import com.mirea.obol.R;
import com.mirea.obol.database.Database;

public class DialogCategory extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_category, null);

        final EditText categoryBox = view.findViewById(R.id.categoryBox);

        builder.setTitle("Новая категория")
                .setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String category = categoryBox.getText().toString();
                        Database db = new Database(getContext());

                        if (!CheckData.checkName(category)) {
                            MyToast.showToast(getContext(), Constants.TOAST_NAME_TEXT);
                        } else {
                            db.insertCategory(category);
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
}

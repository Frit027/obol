package com.mirea.obol.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirea.obol.utilities.Constants;
import com.mirea.obol.R;
import com.mirea.obol.adapters.RecyclerViewAdapter;
import com.mirea.obol.database.Database;
import com.mirea.obol.dialogs.DialogItem;

import java.util.List;

public class FragmentItems extends Fragment implements MainActivity.OnActivityDataListener,
                                                        RecyclerViewAdapter.ItemClickListener {
    private RecyclerViewAdapter adapter;
    private List<Integer> listID;
    private Database db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = new Database(getContext());
        listID = db.getAllIdDesc();

        setTextView(view, db);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        adapter = new RecyclerViewAdapter(getActivity(), db, listID);
        adapter.setClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityDataListener() {
        updateListAndAdapter();
    }

    @Override
    public void onItemClick(View view, int position) {
        DialogItem dialogItem = new DialogItem(db, adapter.getIdItem(position));
        dialogItem.show(getFragmentManager(), Constants.TAG_DIALOG_ITEM);
    }

    private void updateListAndAdapter() {
        listID = db.getAllIdDesc();

        adapter.setListID(listID);
        adapter.notifyDataSetChanged();
    }

    private void setTextView(View view,  Database db) {
        TextView textSpentRub = view.findViewById(R.id.rub);
        textSpentRub.setText(db.getTotalPriceForCurrentMonth(Constants.RUB));

        TextView textSpentUsd = view.findViewById(R.id.usd);
        textSpentUsd.setText(db.getTotalPriceForCurrentMonth(Constants.USD));

        TextView textSpentEur = view.findViewById(R.id.eur);
        textSpentEur.setText(db.getTotalPriceForCurrentMonth(Constants.EUR));

        TextView textBalance = view.findViewById(R.id.balance);
        textBalance.setText(db.getBalance());

        TextView textBalanceForMonth = view.findViewById(R.id.spent_month);
        textBalanceForMonth.setText(db.getPriceForMonth());
    }
}

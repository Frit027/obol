package com.mirea.obol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirea.obol.utilities.Constants;
import com.mirea.obol.R;
import com.mirea.obol.database.Database;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Database db;
    private List<Integer> listID;
    private ItemClickListener mClickListener;

    public RecyclerViewAdapter(Context context, Database db, List<Integer> listID) {
        this.db = db;
        this.inflater = LayoutInflater.from(context);
        this.listID = listID;
    }

    public void setListID(List<Integer> listID) {
        this.listID = listID;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int id = listID.get(position);

        holder.nameView.setText(db.getNameById(id));
        holder.priceView.setText(db.getPriceById(id, Constants.WITH_SIGN));
        holder.categoryView.setText(db.getCategoryById(id));
        holder.dateView.setText(db.getDate(id));

        switch (db.getCategoryById(id)) {
            case Constants.FOOD:
                holder.imageView.setImageResource(R.drawable.ic_food);
                break;
            case Constants.CLOTHES:
                holder.imageView.setImageResource(R.drawable.ic_clothes);
                break;
            case Constants.TRANSPORT:
                holder.imageView.setImageResource(R.drawable.ic_transport);
                break;
            case Constants.CAFE_AND_RESTAURANTS:
                holder.imageView.setImageResource(R.drawable.ic_cafe);
                break;
            case Constants.HOME:
                holder.imageView.setImageResource(R.drawable.ic_home);
                break;
            case Constants.DEVICE:
                holder.imageView.setImageResource(R.drawable.ic_device);
                break;
            case Constants.ENTERTAINMENT:
                holder.imageView.setImageResource(R.drawable.ic_entertainment);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.ic_item);
        }
    }

    @Override
    public int getItemCount() {
        return db.getCountItems();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final TextView nameView, priceView, categoryView, dateView;

        ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.icon);
            nameView = view.findViewById(R.id.name);
            priceView = view.findViewById(R.id.price);
            categoryView = view.findViewById(R.id.category);
            dateView = view.findViewById(R.id.date);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public int getIdItem(int id) {
        return listID.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

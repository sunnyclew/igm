package com.igm.igm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.igm.igm.R;
import com.igm.igm.listener.RecyclerViewClickListener;
import com.igm.igm.model.Item;
import com.igm.igm.model.ItemSelectionStatus;

import java.util.ArrayList;

class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView textView;
    public ImageView imageView;
    private RecyclerViewClickListener listener;

    public void setItemClickListener(RecyclerViewClickListener listener) {
        this.listener = listener;
    }

    ViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.card_text);
        imageView = itemView.findViewById(R.id.card_img);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition());
    }
}

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<Item> list;
    private Context context;

    private int rowIndex = -1;

    public RecyclerAdapter(ArrayList<Item> list, Context context) {
        this.list = list;
        this.context = context;
//        for (Item item : list) {
//            Log.w(TAG, item.toString());
//        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_bluetooth, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getName());
        holder.setItemClickListener((view, position1) -> {
            rowIndex = position1;
            ItemSelectionStatus.currentItem = list.get(position1);
            notifyDataSetChanged(); // made effect on Recycler View's Adapter
        });

        if (rowIndex == position) {
            holder.itemView.setBackgroundResource(R.color.black_mask);
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.imageView.setColorFilter(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.itemView.setBackgroundResource(R.color.white);
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.black_mask));
            holder.imageView.setColorFilter(ContextCompat.getColor(context, R.color.black_mask));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

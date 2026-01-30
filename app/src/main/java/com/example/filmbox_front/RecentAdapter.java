package com.example.filmbox_front;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.VH> {

    public interface OnRecentClickListener {
        void onRecentClick(String query);
    }

    private final List<String> items = new ArrayList<>();
    private OnRecentClickListener listener;

    public void setOnRecentClickListener(OnRecentClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<String> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        String text = items.get(position);
        h.tv.setText(text);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecentClick(text);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvRecentText);
        }
    }
}

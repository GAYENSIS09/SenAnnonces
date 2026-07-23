package com.example.senannonces.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.senannonces.R;
import com.example.senannonces.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private int selectedPosition = -1;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int old = selectedPosition;
        selectedPosition = position;
        if (old >= 0) notifyItemChanged(old);
        notifyItemChanged(position);
    }

    public void clearSelection() {
        int old = selectedPosition;
        selectedPosition = -1;
        if (old >= 0) notifyItemChanged(old);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvEmoji.setText(category.getEmoji());
        holder.tvNom.setText(category.getNom());

        if (position == selectedPosition) {
            holder.container.setBackgroundResource(R.drawable.bg_category_chip_selected);
            holder.tvNom.setTextColor(holder.itemView.getContext().getColor(R.color.acid));
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_category_chip);
            holder.tvNom.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvNom;
        LinearLayout container;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_emoji);
            tvNom = itemView.findViewById(R.id.tv_nom);
            container = itemView.findViewById(R.id.ll_category);
        }
    }
}

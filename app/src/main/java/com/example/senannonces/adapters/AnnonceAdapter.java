package com.example.senannonces.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.senannonces.R;
import com.example.senannonces.models.Annonce;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceAdapter.AnnonceViewHolder> {

    private List<Annonce> annonces = new ArrayList<>();
    private final OnAnnonceClickListener listener;
    private final Context context;

    public interface OnAnnonceClickListener {
        void onAnnonceClick(Annonce annonce);
    }

    public AnnonceAdapter(Context context, OnAnnonceClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setAnnonces(List<Annonce> annonces) {
        this.annonces = annonces;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_annonce, parent, false);
        return new AnnonceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnonceViewHolder holder, int position) {
        Annonce annonce = annonces.get(position);
        holder.bind(annonce);
    }

    @Override
    public int getItemCount() {
        return annonces.size();
    }

    class AnnonceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAnnonce;
        TextView tvTitre, tvPrix, tvCategorie, tvQuartier, tvDate, tvVendeur;

        AnnonceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAnnonce = itemView.findViewById(R.id.iv_annonce);
            tvTitre = itemView.findViewById(R.id.tv_titre);
            tvPrix = itemView.findViewById(R.id.tv_prix);
            tvCategorie = itemView.findViewById(R.id.tv_categorie_badge);
            tvQuartier = itemView.findViewById(R.id.tv_quartier);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvVendeur = itemView.findViewById(R.id.tv_vendeur);
        }

        void bind(Annonce annonce) {
            tvTitre.setText(annonce.getTitre());
            tvPrix.setText(formatPrice(annonce.getPrix()));
            tvCategorie.setText(annonce.getCategorie());
            tvQuartier.setText(annonce.getQuartier());
            tvDate.setText(formatDate(annonce.getDate()));
            tvVendeur.setText(annonce.getVendeur());

            if (annonce.getImageUrl() != null && !annonce.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(annonce.getImageUrl())
                        .transform(new CenterCrop(), new RoundedCorners(16))
                        .placeholder(R.drawable.bg_card)
                        .error(R.drawable.bg_card)
                        .into(ivAnnonce);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAnnonceClick(annonce);
                }
            });
        }

        private String formatPrice(int price) {
            return String.format(Locale.FRANCE, "%,d FCFA", price).replace(',', ' ');
        }

        private String formatDate(String isoDate) {
            if (isoDate == null) return "";
            try {
                return isoDate.substring(0, 10);
            } catch (Exception e) {
                return isoDate;
            }
        }
    }
}

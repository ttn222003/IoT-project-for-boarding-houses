package com.example.smartmotel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {
    private final ArrayList<InformationSearchClass> InformationSearchList;
    private final Context context;

    public AdapterSearch(ArrayList<InformationSearchClass> InformationSearchList, Context context) {
        this.InformationSearchList = InformationSearchList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_smart_motel_recycler_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(InformationSearchList.get(position).getImageURL()).into(holder.recylerImage);
        String addressDisplay = "";

        for(int i = 0; i < InformationSearchList.get(position).getAddress().length(); i++)
        {
            if(InformationSearchList.get(position).getAddress().charAt(i) != '-') addressDisplay += InformationSearchList.get(position).getAddress().charAt(i);
            else                                                                  addressDisplay += "/";
        }
        holder.recylerText.setText(addressDisplay);

        holder.recyclerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToDetailedInformationSearch = new Intent(context, DetailedInformationSearchActivity.class);
                intentToDetailedInformationSearch.putExtra("Address", InformationSearchList.get(holder.getAdapterPosition()).getAddress());
                intentToDetailedInformationSearch.putExtra("Name", InformationSearchList.get(holder.getAdapterPosition()).getUsername());
                intentToDetailedInformationSearch.putExtra("Username Login", InformationSearchList.get(holder.getAdapterPosition()).getUsernameLogin());
                context.startActivity(intentToDetailedInformationSearch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return InformationSearchList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView recylerImage;
        TextView recylerText;
        CardView recyclerCardView;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            recylerImage = itemView.findViewById(R.id.searchSmartMotelRecyclerImageView);
            recylerText = itemView.findViewById(R.id.addressTextView);
            recyclerCardView = itemView.findViewById(R.id.searchSmartMotelRecyclerCardView);
        }
    }
}

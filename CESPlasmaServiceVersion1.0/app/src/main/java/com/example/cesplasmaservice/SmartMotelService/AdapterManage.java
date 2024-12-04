package com.example.cesplasmaservice.SmartMotelService;

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
import com.example.cesplasmaservice.R;

import java.util.ArrayList;

public class AdapterManage extends RecyclerView.Adapter<AdapterManage.MyViewHolder>{
    private final ArrayList<InformationManageClass> informationManageList;
    private final Context context;

    public AdapterManage(ArrayList<InformationManageClass> informationManageList, Context context) {
        this.informationManageList = informationManageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_motel_recycler_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(informationManageList.get(position).getImageURL()).into(holder.recylerImage);
        String addressDisplay = "";

        for(int i = 0; i < informationManageList.get(position).getAddress().length(); i++)
        {
            if(informationManageList.get(position).getAddress().charAt(i) != '-') addressDisplay += informationManageList.get(position).getAddress().charAt(i);
            else                                                                  addressDisplay += "/";
        }
        holder.recylerText.setText(addressDisplay);

        holder.recyclerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToDetailedInformationSearch = new Intent(context, DetailedInformationActivity.class);
                intentToDetailedInformationSearch.putExtra("Address", informationManageList.get(holder.getAdapterPosition()).getAddress());
                intentToDetailedInformationSearch.putExtra("Name", informationManageList.get(holder.getAdapterPosition()).getUsername());
                context.startActivity(intentToDetailedInformationSearch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return informationManageList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView recylerImage;
        TextView recylerText;
        CardView recyclerCardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recylerImage = itemView.findViewById(R.id.manageSmartMotelRecyclerImageView);
            recylerText = itemView.findViewById(R.id.addressTextView);
            recyclerCardView = itemView.findViewById(R.id.manageSmartMotelRecyclerCardView);
        }
    }
}

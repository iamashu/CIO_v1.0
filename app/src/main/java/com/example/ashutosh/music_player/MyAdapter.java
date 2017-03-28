package com.example.ashutosh.music_player;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ashutosh on 17/2/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{
    ArrayList<String> list ;
    ArrayList<String> list1 ;
    private Context context ;
    private ItemClickListener clickListener ;

    public MyAdapter(ArrayList<String> list, ArrayList<String> list1, Context context)
    {
        this.list = list ;
        this.list1 = list1 ;
        this.context = context ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item1,parent,false) ;
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvHead.setText(list.get(position).toString());
        Picasso.with(context)
        .load(list1.get(position).toString())
        .fit()
        .centerCrop()
        .into(holder.tvImg);

    /*    holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(list.get(position).toString()) ;
            }
        }); */
    }

    @Override
    public int getItemCount()
    {
        return list.size() ;
    }

    public void setClickListener(ItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener ;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvHead ;
        public ImageView tvImg ;

        public ViewHolder(View v)
        {
            super(v);

            tvHead = (TextView) v.findViewById(R.id.tvHead) ;
            tvImg = (ImageView) v.findViewById(R.id.tvImg) ;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(clickListener != null)
                clickListener.onClick(v, getAdapterPosition());
        }
    }

}

package com.example.ashutosh.music_player;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ashutosh on 2/2/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{
    public MyAdapter(List<ListItem> litems, Context context) {
        this.litems = litems;
        this.context = context;
    }

    private List<ListItem> litems ;
    private Context context ;

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item1,parent,false) ;

        return new ViewHolder(v) ;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        ListItem listItem = litems.get(position) ;

        holder.tvHead.setText(listItem.getHead());
        holder.tvDisc.setText(listItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return litems.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvHead ;
        public TextView tvDisc ;

        public ViewHolder(View itemView)
        {
            super(itemView);

            tvHead = (TextView) itemView.findViewById(R.id.tvHead) ;
            tvDisc = (TextView) itemView.findViewById(R.id.tvDisc) ;
        }


    }

}

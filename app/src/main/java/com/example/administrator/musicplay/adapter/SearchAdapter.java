package com.example.administrator.musicplay.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.musicplay.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private List<String> keyWordList;
    private OnItemClickListener monItemClickListener;

    public SearchAdapter(List<String> keyWordList) {
        this.keyWordList = keyWordList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View clickView;
        TextView searchAdapterName;

        public ViewHolder(View itemView) {
            super(itemView);
            clickView=itemView;
            searchAdapterName = (TextView) itemView.findViewById(R.id.searchAdapterName);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchadapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.searchAdapterName.setText(keyWordList.get(position));

        holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(),"1",Toast.LENGTH_SHORT).show();
                monItemClickListener.onClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyWordList.size();
    }

    public interface OnItemClickListener{
        void onClick(View view, int position);
    }
    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener ){
        this.monItemClickListener=onItemClickListener;
    }
}

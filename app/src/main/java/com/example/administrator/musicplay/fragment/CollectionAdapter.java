package com.example.administrator.musicplay.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.musicplay.DBHelper.CollectionBook;
import com.example.administrator.musicplay.R;

import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<CollectionBook> showMusicList;
    private OnItemClickListener monItemClickListener;

    public CollectionAdapter(List<CollectionBook> showMusicList) {
        this.showMusicList = showMusicList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View clickView;
        TextView musicname;
        TextView musicartist;
        TextView musictime;

        public ViewHolder(View itemView) {
            super(itemView);
            clickView=itemView;
            musicartist = (TextView) itemView.findViewById(R.id.artist);
            musicname = (TextView) itemView.findViewById(R.id.name);
            musictime = (TextView) itemView.findViewById(R.id.songtime);
        }
    }
    @Override
    public CollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_adapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CollectionAdapter.ViewHolder holder, final int position) {
        holder.musicname.setText(showMusicList.get(position).getSongName());
        holder.musicartist.setText(showMusicList.get(position).getSingerName());
        int songtime=Integer.parseInt(showMusicList.get(position).getDuration());
        int second=songtime%60;
        int minute=songtime/60;
        holder.musictime.setText(String.valueOf(minute)+":"+String.valueOf(second));

        holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monItemClickListener.onClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showMusicList.size();
    }

    public interface OnItemClickListener{
        void onClick(View view, int position);
    }
    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener ){
        this.monItemClickListener=onItemClickListener;
    }
}

package com.example.administrator.musicplay.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.administrator.musicplay.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder>{
    private List<ShowItem> showMusicList;
    private OnItemClickListener monItemClickListener;

    public ShowAdapter(List<ShowItem> showMusicList) {
        this.showMusicList = showMusicList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View clickView;
        TextView musicname;
        TextView musicartist;
        TextView musictime;
        ImageButton download;
        ImageButton collection;

        public ViewHolder(View itemView) {
            super(itemView);
            clickView=itemView;
            musicname = (TextView) itemView.findViewById(R.id.name);
            musicartist = (TextView) itemView.findViewById(R.id.artist);
            musictime = (TextView) itemView.findViewById(R.id.songtime);
            download = (ImageButton) itemView.findViewById(R.id.download);
            collection = (ImageButton) itemView.findViewById(R.id.collection);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showadapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.musicname.setText(showMusicList.get(position).getSongname());
        holder.musicartist.setText(showMusicList.get(position).getSingername());
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
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monItemClickListener.downloadClick(v,position);
            }
        });
        holder.collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monItemClickListener.collectionClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showMusicList.size();
    }

    public interface OnItemClickListener{
        void onClick(View view, int position);

        void downloadClick(View view, int position);

        void collectionClick(View view, int position);
    }
    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener ){
        this.monItemClickListener=onItemClickListener;
    }

}

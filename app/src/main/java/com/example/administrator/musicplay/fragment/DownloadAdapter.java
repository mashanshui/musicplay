package com.example.administrator.musicplay.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.musicplay.DBHelper.DownloadBook;
import com.example.administrator.musicplay.R;

import java.util.List;

/**
 * Created by Administrator on 2017/1/11.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    private List<DownloadBook> showMusicList;
    private OnItemClickListener monItemClickListener;

    public DownloadAdapter(List<DownloadBook> showMusicList) {
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
            musicname = (TextView) itemView.findViewById(R.id.name);
            musicartist= (TextView) itemView.findViewById(R.id.artist);
            musictime= (TextView) itemView.findViewById(R.id.songtime);
        }
    }

    @Override
    public DownloadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_adapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DownloadAdapter.ViewHolder holder, final int position) {
        holder.musicname.setText(showMusicList.get(position).getSongName());
        holder.musicartist.setText(showMusicList.get(position).getSingerName());
        int timeLength=showMusicList.get(position).getDuration();
        int minute=(int)timeLength/60;
        int second=timeLength%60;
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

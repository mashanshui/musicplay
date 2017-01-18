package com.example.administrator.musicplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.musicplay.R;

import java.util.List;

import static android.media.CamcorderProfile.get;

/**
 * Created by Administrator on 2016/11/21.
 */

public class MusicAdapter extends BaseAdapter {
    private List<MusicItem> list;
    private LayoutInflater mLayoutInflater;

    public MusicAdapter(List<MusicItem> list, Context context) {
        this.list = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=mLayoutInflater.inflate(R.layout.music,null);
            holder=new ViewHolder();
            holder.musicIcon= (ImageView) convertView.findViewById(R.id.imageView);
            holder.musicName = (TextView) convertView.findViewById(R.id.name);
            holder.musicArtist = (TextView) convertView.findViewById(R.id.artist);
            holder.musictime = (TextView) convertView.findViewById(R.id.musictime);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.musicIcon.setImageResource(R.drawable.logo);
        holder.musicName.setText(list.get(position).getMusicName());
        holder.musicArtist.setText(list.get(position).getMusicArtist());
        int time=list.get(position).getMusicduration();
        int second=(time/1000)%60;
        int minute=(time/1000)/60;
        holder.musictime.setText(String.valueOf(minute)+":"+String.valueOf(second));
        return convertView;
    }
    class ViewHolder{
        public ImageView musicIcon;
        public TextView musicName;
        public TextView musicArtist;
        public TextView musictime;
    }
}

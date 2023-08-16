package com.example.iothome;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SpeakerAdapter extends ArrayAdapter<String> {

    Context myContext;
    LayoutInflater inflater;
    List<String> DataList,DataList2;
    private SparseBooleanArray mSelectedItemsIds;

    public SpeakerAdapter(Context context, int resourceId, List<String> items, List<String> items2) {
        super(context, resourceId, items);
        mSelectedItemsIds = new SparseBooleanArray();
        myContext = context;
        DataList = items;
        DataList2 = items2;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView textView;
        TextView textView2;
        ImageView imageView;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.speaker_item, null);

            holder.textView = (TextView) view.findViewById(R.id.song_title);
            holder.textView2 = (TextView) view.findViewById(R.id.singer_name);

            holder.imageView = (ImageView) view.findViewById(R.id.image);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(DataList.get(position).toString());
        holder.textView2.setText(DataList2.get(position).toString());
        holder.imageView.setImageResource(R.drawable.baseline_music_note_24);

        return view;
    }

    public String getTitle(int position){
        return DataList.get(position);
    }

    public String getSinger(int position){
        return DataList2.get(position);
    }
}

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

public class MoodAdapter extends ArrayAdapter<String> {
    Context myContext;
    LayoutInflater inflater;
    List<String> DataList,DataList2;
    List<Integer> DataList3;
    private SparseBooleanArray mSelectedItemsIds;

    public MoodAdapter(Context context, int resourceId, List<String> items, List<String> items2, List<Integer> items3) {
        super(context, resourceId, items);
        mSelectedItemsIds = new SparseBooleanArray();
        myContext = context;
        DataList = items;
        DataList2 = items2;
        DataList3 = items3;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView textView;
        TextView textView2;
        ImageView imageView;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final MoodAdapter.ViewHolder holder;

        if (view == null) {
            holder = new MoodAdapter.ViewHolder();
            view = inflater.inflate(R.layout.mood_item, null);

            holder.textView = (TextView) view.findViewById(R.id.moodTitle);
            holder.textView2 = (TextView) view.findViewById(R.id.moodSubText);

            holder.imageView = (ImageView) view.findViewById(R.id.moodImage);

            view.setTag(holder);
        } else {
            holder = (MoodAdapter.ViewHolder) view.getTag();
        }

        holder.textView.setText(DataList.get(position).toString());
        holder.textView2.setText(DataList2.get(position).toString());
        holder.imageView.setImageResource(DataList3.get(position));

        return view;
    }
    public String getTitle(int position){
        return DataList.get(position);
    }
}

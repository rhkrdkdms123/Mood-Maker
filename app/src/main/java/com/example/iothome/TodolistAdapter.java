package com.example.iothome;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class TodolistAdapter extends ArrayAdapter<String> {
    Context myContext;
    LayoutInflater inflater;
    List<String> DataList;
    private SparseBooleanArray mSelectedItemsIds;

    public TodolistAdapter(Context context, int resourceId, List<String> items) {
        super(context, resourceId, items);
        mSelectedItemsIds = new SparseBooleanArray();
        myContext = context;
        DataList = items;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView textView;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.todolist_item, null);
            holder.textView = (TextView) view.findViewById(R.id.todolist_item_textView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(DataList.get(position).toString());

        return view;
    }

    public View getView(int position){
        final ViewHolder holder;

        holder = new ViewHolder();
        View view = inflater.inflate(R.layout.todolist_item, null);
        holder.textView = (TextView) view.findViewById(R.id.todolist_item_textView);
        view.setTag(holder);

        holder.textView.setText(DataList.get(position).toString());

        return view;
    }
    
    // 추가
    @Override
    public void add(String object){
        DataList.add(object);
        notifyDataSetChanged();
    }

    public void update(String targetObject, String object){
        int position = DataList.indexOf(targetObject);
        DataList.set(position, object);
        notifyDataSetChanged();
    }

    @Override
    public void remove(String object){
        DataList.remove(object);
        notifyDataSetChanged();
    }

    //업데이트 혹은 삭제 후 목록 가져오기
    public List<String> getMyList(){
        return DataList;
    }

    public void toggleSelection(int position){
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection(){
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value){
        if(value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
    public int getSelectedCount(){
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds(){
        return mSelectedItemsIds;
    }

}

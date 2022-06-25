package com.example.mobiledatabase.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mobiledatabase.R;

import java.util.List;

public class DatabaseAdapter extends BaseAdapter {

    private List<String> list;
    private LayoutInflater inflater;

    public DatabaseAdapter(Context context, List<String> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    //get number
    @Override
    public int getCount() {
        int ret = 0;
        if (list != null) {
            ret = list.size();
        }
        return ret;
    }

    //get object
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    //get id
    @Override
    public long getItemId(int position) {
        return position;
    }

    //get the view list
    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = list.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder.dbName = convertView.findViewById(R.id.tvName);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.dbName.setText(item);
        return convertView;
    }

    public static class ViewHolder {
        public TextView dbName;
    }
}

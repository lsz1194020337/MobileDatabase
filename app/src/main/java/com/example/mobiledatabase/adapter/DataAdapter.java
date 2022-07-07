package com.example.mobiledatabase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.bean.Table;

import java.util.List;

public class DataAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Table> c1;

    public DataAdapter(Context context, List<Table> tableList) {
        this.context = context;
        this.c1 = tableList;
        inflater = LayoutInflater.from(context);
    }

    public static class ViewHolder {
        public TextView column1;
        public TextView column2;
        public TextView column3;
        public TextView column4;
        public TextView column5;
        public TextView column6;
        public TextView column7;
        public TextView column8;
        public TextView column9;
        public TextView column10;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (c1 != null) {
            ret = c1.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return c1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Table table = c1.get(position);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_data,null);
            viewHolder.column1 = convertView.findViewById(R.id.tv1);
            viewHolder.column2 = convertView.findViewById(R.id.tv2);
            viewHolder.column3 = convertView.findViewById(R.id.tv3);
            viewHolder.column4 = convertView.findViewById(R.id.tv4);
            viewHolder.column5 = convertView.findViewById(R.id.tv5);
            viewHolder.column6 = convertView.findViewById(R.id.tv6);
            viewHolder.column7 = convertView.findViewById(R.id.tv7);
            viewHolder.column8 = convertView.findViewById(R.id.tv8);
            viewHolder.column9 = convertView.findViewById(R.id.tv9);
            viewHolder.column10 = convertView.findViewById(R.id.tv10);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.column1.setText(table.getColumn1());
        viewHolder.column2.setText(table.getColumn2());
        viewHolder.column3.setText(table.getColumn3());
        viewHolder.column4.setText(table.getColumn4());
        viewHolder.column5.setText(table.getColumn5());
        viewHolder.column6.setText(table.getColumn6());
        viewHolder.column7.setText(table.getColumn7());
        viewHolder.column8.setText(table.getColumn8());
        viewHolder.column9.setText(table.getColumn9());
        viewHolder.column10.setText(table.getColumn10());

        return convertView;
    }
}

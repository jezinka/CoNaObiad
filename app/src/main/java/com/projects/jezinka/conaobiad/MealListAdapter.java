package com.projects.jezinka.conaobiad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MealListAdapter extends BaseAdapter {

    private ArrayList<String> searchArrayList;

    private LayoutInflater mInflater;

    public MealListAdapter(Context context, ArrayList<String> initialResults) {
        this.searchArrayList = initialResults;
        mInflater = LayoutInflater.from(context);
    }

    public void updateResults(ArrayList<String> results) {
        searchArrayList = results;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);
            holder = new ViewHolder();
            holder.titleNameView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleNameView.setText(searchArrayList.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView titleNameView;
    }
}

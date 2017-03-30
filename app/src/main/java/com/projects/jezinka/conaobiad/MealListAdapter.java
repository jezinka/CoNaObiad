package com.projects.jezinka.conaobiad;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.model.Meal;

public class MealListAdapter extends ArrayAdapter<Meal> {

    Meal[] data;
    Context context;
    int layoutResourceId;

    public MealListAdapter(Context context, int layoutResourceId, Meal[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public void updateResults(Meal[] results) {
        data = results;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Meal getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return data[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.titleNameView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleNameView.setText(data[position].getName());
        return convertView;
    }

    static class ViewHolder {
        TextView titleNameView;
    }
}

package com.projects.jezinka.conaobiad;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.model.Dinner;

class DinnerListAdapter extends ArrayAdapter<Dinner> {

    private Dinner[] data;

    private Context context;
    private int layoutResourceId;

    DinnerListAdapter(Context context, int layoutResourceId, Dinner[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    void updateResults(Dinner[] results) {
        this.data = results;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Dinner getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return data[position].getId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.titleNameView = (TextView) convertView.findViewById(R.id.dinners_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleNameView.setText(data[position].getMeal().getName());
        holder.date.setText(data[position].getDateString());
        return convertView;
    }

    private static class ViewHolder {
        TextView date;
        TextView titleNameView;
    }
}

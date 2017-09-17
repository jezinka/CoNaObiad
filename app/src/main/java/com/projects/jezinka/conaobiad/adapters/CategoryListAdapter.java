package com.projects.jezinka.conaobiad.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.models.Category;

public class CategoryListAdapter extends ArrayAdapter<Category> implements Filterable {

    private Category[] data;

    private Context context;
    private int layoutResourceId;

    public boolean showCheckboxes = false;

    public CategoryListAdapter(Context context, int layoutResourceId, Category[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public void updateResults(Category[] results) {
        this.data = results;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Category getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return data[position].getId();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.titleNameView = (TextView) convertView.findViewById(R.id.text1);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleNameView.setText(data[position].getName());

        if (holder.checkBox != null) {
            holder.checkBox.setVisibility(showCheckboxes ? View.VISIBLE : View.GONE);

            if (showCheckboxes) {
                holder.checkBox.setChecked(data[position].isChecked());
                holder.checkBox.setOnClickListener(getOnClickListener(position, (ListView) parent));
            }

            holder.titleNameView.setOnClickListener(getOnClickListener(position, (ListView) parent));
        }
        return convertView;
    }

    @NonNull
    private View.OnClickListener getOnClickListener(final int position, @NonNull final ListView parent) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.performItemClick(v, position, data[position].getId());
            }
        };
    }

    private static class ViewHolder {
        TextView titleNameView;
        CheckBox checkBox;
    }
}
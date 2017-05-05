package com.projects.jezinka.conaobiad.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.models.Meal;

import java.util.ArrayList;

public class MealListAdapter extends ArrayAdapter<Meal> implements Filterable {

    private Meal[] data;
    private Meal[] filteredData;

    private Context context;
    private int layoutResourceId;

    public boolean showCheckboxes;

    public MealListAdapter(Context context, int layoutResourceId, Meal[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.filteredData = data;
    }

    public void updateResults(Meal[] results) {
        this.data = results;
        this.filteredData = results;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredData.length;
    }

    @Override
    public Meal getItem(int position) {
        return filteredData[position];
    }

    @Override
    public long getItemId(int position) {
        return filteredData[position].getId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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

        holder.titleNameView.setText(filteredData[position].getName());

        if (holder.checkBox != null) {
            holder.checkBox.setVisibility(showCheckboxes ? View.VISIBLE : View.GONE);

            if (showCheckboxes) {
                holder.checkBox.setChecked(filteredData[position].isChecked());
                holder.checkBox.setOnClickListener(getOnClickListener(position, (ListView) parent));
            }

            holder.titleNameView.setOnClickListener(getOnClickListener(position, (ListView) parent));
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView titleNameView;
        CheckBox checkBox;
    }

    @NonNull
    private View.OnClickListener getOnClickListener(final int position, @NonNull final ListView parent) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.performItemClick(v, position, filteredData[position].getId());
            }
        };
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                if (charSequence == null || charSequence.length() == 0) {
                    results.values = data;
                    results.count = data.length;
                } else {
                    ArrayList<Meal> filterResultsData = new ArrayList<>();

                    for (Meal meal : data) {
                        if (meal.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filterResultsData.add(meal);
                        }
                    }

                    results.values = filterResultsData.toArray(new Meal[filterResultsData.size()]);
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (Meal[]) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}

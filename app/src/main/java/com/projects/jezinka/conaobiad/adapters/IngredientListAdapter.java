package com.projects.jezinka.conaobiad.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.models.Ingredient;

import java.util.ArrayList;

public class IngredientListAdapter extends ArrayAdapter<Ingredient> implements Filterable {

    private Ingredient[] data;
    private Ingredient[] filteredData;

    private Context context;
    private int layoutResourceId;

    public IngredientListAdapter(Context context, int layoutResourceId, Ingredient[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.filteredData = data;
    }

    public void updateResults(Ingredient[] results) {
        this.data = results;
        this.filteredData = results;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredData.length;
    }

    @Override
    public Ingredient getItem(int position) {
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
            holder.titleNameView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleNameView.setText(filteredData[position].getName());
        return convertView;
    }

    private static class ViewHolder {
        TextView titleNameView;
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
                    ArrayList<Ingredient> filterResultsData = new ArrayList<>();

                    for (Ingredient ingredient : data) {
                        if (ingredient.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filterResultsData.add(ingredient);
                        }
                    }

                    results.values = filterResultsData.toArray(new Ingredient[filterResultsData.size()]);
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (Ingredient[]) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}

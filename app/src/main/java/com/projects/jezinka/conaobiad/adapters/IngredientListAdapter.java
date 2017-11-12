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
import com.projects.jezinka.conaobiad.models.Ingredient;

import java.util.ArrayList;

public class IngredientListAdapter extends ArrayAdapter<Ingredient> implements Filterable, CheckboxesArrayAdapterInterface {

    private Ingredient[] data;
    private Ingredient[] filteredData;

    private Context context;
    private int layoutResourceId;

    private boolean showCheckboxes = false;

    public boolean isShowCheckboxes() {
        return showCheckboxes;
    }

    public void setShowCheckboxes(boolean showCheckboxes) {
        this.showCheckboxes = showCheckboxes;
    }

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
    public void notifyDataSetChanged() {
        setNotifyOnChange(false);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredData.length;
    }

    public int getCount(boolean unfilter) {
        if (unfilter) {
            return data.length;
        }
        return filteredData.length;
    }

    @Override
    public Ingredient getItem(int position) {
        return filteredData[position];
    }

    public Ingredient getItem(int position, boolean unfilter) {
        if (unfilter) {
            return data[position];
        }
        return filteredData[position];
    }

    @Override
    public long getItemId(int position) {
        return filteredData[position].getId();
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

    @NonNull
    private View.OnClickListener getOnClickListener(final int position, @NonNull final ListView parent) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.performItemClick(v, position, filteredData[position].getId());
            }
        };
    }

    private static class ViewHolder {
        TextView titleNameView;
        CheckBox checkBox;
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

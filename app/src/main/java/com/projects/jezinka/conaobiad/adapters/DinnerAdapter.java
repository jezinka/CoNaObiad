package com.projects.jezinka.conaobiad.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.activities.MainActivity;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Dinner;
import com.projects.jezinka.conaobiad.models.tables.DinnerContract;
import com.projects.jezinka.conaobiad.utils.TimeUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;


public class DinnerAdapter extends BaseAdapter {
    private Context mContext;
    private List<Date> dates;
    private TreeMap<Date, List<Dinner>> dinners;
    private CoNaObiadDbHelper dbHelper;

    public DinnerAdapter(Context c) {
        this.mContext = c;
        this.dbHelper = ((MainActivity) c).getDbHelper();

        this.dinners = getDinners();
        this.dates = new ArrayList<>(this.dinners.keySet());
    }

    public void updateResults() {
        this.dinners = this.getDinners();
        this.dates = new ArrayList<>(this.dinners.keySet());
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return dinners.keySet().size();
    }

    public Date getItem(int position) {
        return this.dates.get(position);
    }

    public List<Dinner> getDinners(Date date) {
        return this.dinners.get(date);
    }

    public boolean isDayEmpty(Date date) {
        return getDinners(date).isEmpty();
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final DinnerContract dinnerContract = new DinnerContract();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dinner_textview, null);

            holder = new ViewHolder();

            holder.dateTextView = (TextView) convertView.findViewById(R.id.dinner_date);
            holder.dinnersTextView = (TextView) convertView.findViewById(R.id.text_view);
            holder.trashButton = (ImageButton) convertView.findViewById(R.id.trash_button);
            holder.addButton = (ImageButton) convertView.findViewById(R.id.add_dinner_button);
            holder.recipeButton = (ImageButton) convertView.findViewById(R.id.show_recipe_button);
            holder.ingredientsButton = (ImageButton) convertView.findViewById(R.id.show_ingredients_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dateTextView.setText(getTextForDate(position));
        holder.dinnersTextView.setText(getTextForTile(position));

        holder.trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = getItem(position);
                dinnerContract.delete(date.getTime(), DinnerContract.COLUMN_DATE, dbHelper);
                updateResults();
            }
        });

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = getItem(position);
                ((MainActivity) mContext).showNewDinnerDialog(date);
            }
        });

        holder.recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = getItem(position);
                ((MainActivity) mContext).showRecipeDialog(dinnerContract.getDinners(dbHelper, date));
            }
        });
        holder.ingredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = getItem(position);
                ((MainActivity) mContext).showIngredients(dinnerContract.getDinners(dbHelper, date));
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView dateTextView;
        TextView dinnersTextView;
        ImageButton trashButton;
        ImageButton addButton;
        ImageButton recipeButton;
        ImageButton ingredientsButton;
    }

    private String getTextForDate(int position) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));
        return df.format(getItem(position));
    }

    @NonNull
    private String getTextForTile(int position) {
        StringBuilder text = new StringBuilder();

        Date date = getItem(position);

        for (Dinner dinner : getDinners(date)) {
            text.append(dinner.getMealName());
            text.append("\n");
        }
        return text.toString();
    }

    private TreeMap<Date, List<Dinner>> getDinners() {
        DinnerContract dinnerContract = new DinnerContract();
        Dinner[] dinnersForActualWeek = dinnerContract.getDinnersForActualWeek(dbHelper);
        TreeMap<Date, List<Dinner>> preparedRows = new TreeMap<>();

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(TimeUtils.getWeekStartDate(new Date()));

        for (int i = 0; i < MainActivity.getPlanLength(); i++) {
            Date date = calendarInstance.getTime();

            List<Dinner> dinnersList = new ArrayList<>();
            for (Dinner dinner : dinnersForActualWeek) {
                if (dinner.getDate().getTime() == date.getTime()) {
                    dinnersList.add(dinner);
                }
            }
            preparedRows.put(date, dinnersList);

            calendarInstance.add(Calendar.DATE, 1);
        }
        return preparedRows;
    }
}

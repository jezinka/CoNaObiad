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
import com.projects.jezinka.conaobiad.models.tableDefinitions.DinnerContract;
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

    public Dinner getDinner(Date date) {
        List<Dinner> dinners = this.dinners.get(date);
        if (dinners.size() > 0) {
            return dinners.get(0);
        }
        return null;
    }

    public List<Dinner> getDinners(Date date) {
        return this.dinners.get(date);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final Date date = getItem(position);
        final Dinner dinner = getDinner(date);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dinner_textview, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text_view);
        textView.setText(getTextForTile(position));

        ImageButton button = (ImageButton) convertView.findViewById(R.id.trash_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DinnerContract dinnerContract = new DinnerContract();
                dinnerContract.delete(date.getTime(), DinnerContract.columnDate, dbHelper);
                updateResults();
            }
        });

        ImageButton add_button = (ImageButton) convertView.findViewById(R.id.add_dinner_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).showNewDinnerDialog(date);
            }
        });
        ImageButton recipe_button = (ImageButton) convertView.findViewById(R.id.show_recipe_button);
        recipe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).showRecipeDialog(dinner);
            }
        });
        ImageButton ingredients_button = (ImageButton) convertView.findViewById(R.id.show_ingredients_button);
        ingredients_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).showIngredients(dinner);
            }
        });

        return convertView;
    }

    @NonNull
    private String getTextForTile(int position) {
        StringBuffer text = new StringBuffer();
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));
        Date date = getItem(position);
        text.append(df.format(date));
        text.append("\n");
        text.append("\n");
        for (Dinner dinner : getDinners(date)) {
            text.append(dinner.getMealName());
            text.append("\n");
        }
        return text.toString();
    }

    private TreeMap<Date, List<Dinner>> getDinners() {
        DinnerContract dinnerContract = new DinnerContract();
        Dinner[] dinners = dinnerContract.getDinners(dbHelper);
        TreeMap<Date, List<Dinner>> preparedRows = new TreeMap<>();

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(TimeUtils.getWeekStartDate(new Date()));

        for (int i = 0; i < MainActivity.getPlanLength(); i++) {
            Date date = calendarInstance.getTime();

            List<Dinner> dinnersList = new ArrayList<>();
            for (Dinner dinner : dinners) {
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

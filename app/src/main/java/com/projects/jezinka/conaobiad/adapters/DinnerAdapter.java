package com.projects.jezinka.conaobiad.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.activities.MainActivity;
import com.projects.jezinka.conaobiad.models.Dinner;
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

    public DinnerAdapter(Context c, Dinner[] dinners) {
        mContext = c;
        this.dinners = getPreparedHashMap(dinners);
        this.dates = new ArrayList<>(this.dinners.keySet());
    }

    public void updateResults(Dinner[] dinners) {
        this.dinners = getPreparedHashMap(dinners);
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

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dinner_textview, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text_view);
        textView.setText(getTextForTile(position));

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

    private TreeMap<Date, List<Dinner>> getPreparedHashMap(Dinner[] dinners) {
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

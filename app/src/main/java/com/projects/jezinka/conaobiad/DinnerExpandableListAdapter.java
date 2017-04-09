package com.projects.jezinka.conaobiad;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.model.Dinner;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class DinnerExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Date> expandableListTitle;
    private TreeMap<Date, List<Dinner>> expandableListDetail;

    public DinnerExpandableListAdapter(Context context, Dinner[] dinners) {
        this.context = context;
        this.expandableListDetail = getPreparedHashMap(dinners);
        this.expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
    }

    void updateResults(Dinner[] dinners) {
        TreeMap<Date, List<Dinner>> expandableListDetail = getPreparedHashMap(dinners);
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        this.notifyDataSetChanged();
    }

    @Override
    public Dinner getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition).getId();
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Dinner expandedListText = getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText.getMeal().getName());
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Date getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return this.expandableListTitle.get(listPosition).getTime();
    }

    public String getChildsName(Date key) {
        List<Dinner> dinners = this.expandableListDetail.get(key);
        List<String> dinnersNames = new ArrayList<>();
        for (Dinner dinner : dinners) {
            dinnersNames.add(dinner.getMeal().getName());
        }
        return TextUtils.join(",", dinnersNames);
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));

        Date listTitle = getGroup(listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(df.format(listTitle) + ":" + getChildsName(listTitle));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }


    private TreeMap<Date, List<Dinner>> getPreparedHashMap(Dinner[] dinners) {
        TreeMap<Date, List<Dinner>> preparedRows = new TreeMap<>();

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(TimeUtils.getSaturdayDate(new Date()));

        for (int i = 0; i < TimeUtils.DAYS_IN_PLANNER; i++) {
            Date date = calendarInstance.getTime();

            List<Dinner> dinnersList = new ArrayList<>();
            for (Dinner dinner : dinners) {
                if (dinner.getDate().getTime() == date.getTime()) {
                    dinnersList.add(dinner);
                }
            }
            preparedRows.put(date, dinnersList);

            calendarInstance.roll(Calendar.DATE, true);
        }
        return preparedRows;
    }
}
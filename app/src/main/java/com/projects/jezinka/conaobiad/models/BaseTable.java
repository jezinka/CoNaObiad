package com.projects.jezinka.conaobiad.models;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;

public abstract class BaseTable {

    String TABLE_NAME;
    String SQL_CREATE_ENTRIES;

    String getTableName() {
        return this.TABLE_NAME;
    }

    public String getDropTableQuery() {
        return "drop table if exists " + this.TABLE_NAME;
    }

    public String getCreateEntriesQuery() {
        return this.SQL_CREATE_ENTRIES;
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(this.getTableName(), ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(this.getTableName(), id);
    }
}

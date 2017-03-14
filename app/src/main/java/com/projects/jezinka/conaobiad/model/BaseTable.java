package com.projects.jezinka.conaobiad.model;

/**
 * Created by jezinka on 12.03.17.
 */

public abstract class BaseTable {

    String TABLE_NAME;
    String SQL_CREATE_ENTRIES;

    public String getDeleteEntriesQuery() {
        return "delete from " + this.TABLE_NAME;
    }

    public String getDropTableQuery() {
        return "drop table if exists " + this.TABLE_NAME;
    }

    public String getCreateEntriesQuery() {
        return this.SQL_CREATE_ENTRIES;
    }
}

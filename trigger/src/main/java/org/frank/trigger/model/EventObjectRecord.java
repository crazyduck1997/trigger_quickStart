package org.frank.trigger.model;

import com.alibaba.fastjson.JSONObject;

public class EventObjectRecord {

    private JSONObject before;

    private JSONObject after;

    private Object[] changeColumns;

    public Object[] getChangeColumns() {
        return changeColumns;
    }

    public void setChangeColumns(Object[] changeColumns) {
        this.changeColumns = changeColumns;
    }

    public JSONObject getBefore() {
        return before;
    }

    public void setBefore(JSONObject before) {
        this.before = before;
    }

    public JSONObject getAfter() {
        return after;
    }

    public void setAfter(JSONObject after) {
        this.after = after;
    }


}

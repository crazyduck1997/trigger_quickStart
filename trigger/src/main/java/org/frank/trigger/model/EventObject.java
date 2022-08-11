package org.frank.trigger.model;


import java.util.List;

public class EventObject {

    private String table;

    private String eventType;

    private List<EventObjectRecord> eventObjectRecordList;


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<EventObjectRecord> getEventObjectRecordList() {
        return eventObjectRecordList;
    }

    public void setEventObjectRecordList(List<EventObjectRecord> eventObjectRecordList) {
        this.eventObjectRecordList = eventObjectRecordList;
    }
}

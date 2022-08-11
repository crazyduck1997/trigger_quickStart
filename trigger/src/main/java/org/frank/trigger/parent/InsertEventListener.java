package org.frank.trigger.parent;

import org.frank.trigger.constant.EventType;
import org.frank.trigger.model.EventRecord;

public interface InsertEventListener<T> {

    String TYPE = EventType.INSERT;

    void execute(EventRecord<T> eventRecord);

}

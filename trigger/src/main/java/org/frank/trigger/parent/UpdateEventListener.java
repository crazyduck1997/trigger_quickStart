package org.frank.trigger.parent;

import org.frank.trigger.constant.EventType;
import org.frank.trigger.model.EventRecord;

public interface UpdateEventListener<T> {

    String TYPE = EventType.UPDATE;

    void execute(EventRecord<T> eventRecord);

}

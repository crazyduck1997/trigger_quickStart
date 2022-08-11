package org.frank.trigger.handle;

import org.frank.trigger.model.EventObjectRecord;
import org.frank.trigger.model.EventRecord;

import java.util.List;

public interface OperateRoute {

    EventRecord<?> execute(List<EventObjectRecord> eventObjectRecordList, Class<?> aClass);

}

package org.frank.trigger.handle;

import org.frank.trigger.model.EventObjectRecord;
import org.frank.trigger.model.EventRecord;

import java.util.List;

public class OperateContext {

    private final OperateRoute operateRoute;

    public OperateContext(OperateRoute operateRoute) {
        this.operateRoute = operateRoute;
    }

    public EventRecord<?> execute(List<EventObjectRecord> eventObjectRecordList, Class<?> aClass){
        return this.operateRoute.execute(eventObjectRecordList, aClass);
    }

}

package org.frank.trigger.handle;

import com.alibaba.fastjson.JSON;
import org.frank.trigger.model.EventObjectRecord;
import org.frank.trigger.model.EventRecord;

import java.util.List;

public class UpdateOperate implements OperateRoute{

    @Override
    public EventRecord<?> execute(List<EventObjectRecord> eventObjectRecordList, Class<?> aClass) {
        EventRecord<Object> eventRecord = new EventRecord();
        for (EventObjectRecord record : eventObjectRecordList) {
            eventRecord.setAfter(JSON.parseObject(record.getAfter().toJSONString(), aClass));
            eventRecord.setBefore(JSON.parseObject(record.getBefore().toJSONString(), aClass));
        }
        return eventRecord;
    }
}

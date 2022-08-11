package com.example.demo.triggerTest;

import com.example.demo.entity.User;
import org.frank.trigger.annotations.Trigger;
import org.frank.trigger.model.EventRecord;
import org.frank.trigger.parent.UpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Trigger(table = "User",columns = {"name"})
public class UpdateTriggerTest implements UpdateEventListener<User> {

    Logger logger = LoggerFactory.getLogger(InsertTriggerTest.class);

    @Override
    public void execute(EventRecord<User> eventRecord) {
        logger.info(">>>>>>>update trigger execute");
        logger.info(">>>>>>>update user name: {}",eventRecord.getAfter().getName());

    }
}

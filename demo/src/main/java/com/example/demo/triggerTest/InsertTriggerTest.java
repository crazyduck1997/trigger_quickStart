package com.example.demo.triggerTest;

import com.example.demo.entity.User;
import org.frank.trigger.annotations.Trigger;
import org.frank.trigger.model.EventRecord;
import org.frank.trigger.parent.InsertEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Trigger(table = "user")
public class InsertTriggerTest implements InsertEventListener<User> {

    Logger logger = LoggerFactory.getLogger(InsertTriggerTest.class);

    @Override
    public void execute(EventRecord<User> eventRecord) {
        logger.info(">>>>>>>add trigger execute");
        User after = eventRecord.getAfter();
        logger.info(">>>>>>> insert user name = {}",after.getName());
    }
}

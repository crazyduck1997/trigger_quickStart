package org.frank.trigger.handle;

import org.frank.trigger.annotations.Trigger;
import org.frank.trigger.canal.CanalConnectUtil;
import org.frank.trigger.constant.CommonConstant;
import org.frank.trigger.constant.EventType;
import org.frank.trigger.model.EventObject;
import org.frank.trigger.model.EventObjectRecord;
import org.frank.trigger.model.EventRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.util.*;


@Component
public class TriggerHandle implements ApplicationRunner {

    Logger logger = LoggerFactory.getLogger(TriggerHandle.class);


    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void run(ApplicationArguments args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        while (true) {
            EventObject data = CanalConnectUtil.getData();
            if (Objects.isNull(data)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                execute(data);
            }
        }
    }

    private void execute(EventObject eventObject) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        String table = eventObject.getTable();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Trigger.class);

        Collection<Object> values = beansWithAnnotation.values();

        for (Object value : values) {
            Class<?> clz = value.getClass();
            Trigger trigger = clz.getDeclaredAnnotation(Trigger.class);
            if (judge(trigger, eventObject, clz)) {
                operateExecute(clz, value, eventObject);
            }
        }
    }

    private boolean judge(Trigger trigger, EventObject eventObject, Class<?> clz) throws NoSuchFieldException, IllegalAccessException {
        return trigger.table().equalsIgnoreCase(eventObject.getTable()) &&
                getInterfaceType(clz).equalsIgnoreCase(eventObject.getEventType()) &&
                judgeColumn(trigger, eventObject);
    }

    private boolean judgeColumn(Trigger trigger, EventObject eventObject) {
        String[] columns = trigger.columns();
        if (columns.length == 0) {
            return true;
        }

        boolean flag = false;

        EventObjectRecord record = eventObject.getEventObjectRecordList().get(0);
        for (Object changeColumn : record.getChangeColumns()) {
            if (Arrays.asList(columns).contains(changeColumn.toString())) {
                flag = true;
            }
        }
        return flag;
    }


    private void operateExecute(Class<?> clz, Object value, EventObject eventObject) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {

        Class<?> aClass = parseGenericsValue(clz);

        String type = (String) clz.getInterfaces()[0].getDeclaredField(CommonConstant.TYPE).get(CommonConstant.TYPE);

        EventRecord<?> eventRecord;

        OperateContext operateContext;

        switch (type) {
            case EventType.INSERT:
                operateContext = new OperateContext(new InsertOperate());
                break;
            case EventType.UPDATE:
                operateContext = new OperateContext(new UpdateOperate());
                break;
            default:
                operateContext = null;
        }

        eventRecord = operateContext.execute(eventObject.getEventObjectRecordList(), aClass);

        Method[] methods = clz.getMethods();
        Method method = methods[0];
        method.invoke(value, eventRecord);
    }

    private Class<?> parseGenericsValue(Class<?> clz) throws ClassNotFoundException {
        Type[] interfaceTypeArray = clz.getGenericInterfaces();
        Type type = interfaceTypeArray[0];
        if (type instanceof ParameterizedType) {
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            Type type1 = parameterizedType[0];
            String className = type1.toString();
            if (className.startsWith(CommonConstant.TYPE_NAME_PREFIX)) {
                className = className.substring(CommonConstant.TYPE_NAME_PREFIX.length());
            }
            return Class.forName(className.trim());
        }
        //todo: throw exception
        return null;
    }

    private String getInterfaceType(Class<?> clz) throws IllegalAccessException, NoSuchFieldException {
        Field field = clz.getField(CommonConstant.TYPE);
        return (String) field.get(null);
    }
}

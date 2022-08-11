package org.frank.trigger.canal;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.frank.trigger.constant.EventType;
import org.frank.trigger.model.EventObject;
import org.frank.trigger.model.EventObjectRecord;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class CanalConnectUtil {

    private static Properties props;

    private static CanalConnector connector;

    @SneakyThrows
    public static void initConnect() {
        // 创建链接
        connector = CanalConnectors.newSingleConnector(new InetSocketAddress(props.get("host").toString(),
                Integer.parseInt((String)props.get("port"))), props.get("destination").toString(), props.get("username").toString(), props.get("password").toString());
        connector.connect();
        connector.subscribe(".*\\..*");
        connector.rollback();
    }

    static {
        // 要读取的文件名， 文件放在\src\main\resources\mappers 下
        String fileName = "application.yml";
        props = new Properties();
        try {
            props.load(new InputStreamReader(Objects.requireNonNull(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)), "UTF-8"));
        } catch (IOException e) {
        }
        initConnect();
    }

    // 根据key 的到 value
    public static String getProperty(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return value.trim();
    }

    // 可传入默认值
    // 如果根据 key 得到的 value 是空，则使用默认值
    public static String getProperty(String key, String defaultValue) {

        String value = props.getProperty(key.trim());
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value.trim();
    }

    @SneakyThrows
    public static EventObject getData() {
        Message message = connector.getWithoutAck(120); // 获取指定数量的数据
        long batchId = message.getId();
        int size = message.getEntries().size();
        if (batchId == -1 || size == 0) {
            return null;
        }
        EventObject eventObject = printEntry(message.getEntries());
        connector.ack(batchId); // 提交确认
        return eventObject;
        // connector.rollback(batchId); // 处理失败, 回滚数据
    }

    private static EventObject printEntry(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry,
                        e);
            }
            CanalEntry.EventType eventType = rowChage.getEventType();
            EventObject eventObject = new EventObject();
            List<EventObjectRecord> eventObjectRecords = Lists.newArrayListWithCapacity(rowChage.getRowDatasList().size());
            String triggerEventType = "";
            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                EventObjectRecord record = new EventObjectRecord();
                record.setBefore(printColumn(rowData.getBeforeColumnsList()));
                record.setAfter(printColumn(rowData.getAfterColumnsList()));
                record.setChangeColumns(
                        !CollectionUtils.isEmpty(rowData.getAfterColumnsList())?
                                rowData.getAfterColumnsList().stream().filter(CanalEntry.Column::getUpdated).map(CanalEntry.Column::getName).toArray():
                                null);
                if (eventType == CanalEntry.EventType.DELETE) {
                    record.setBefore(printColumn(rowData.getBeforeColumnsList()));
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    triggerEventType = EventType.INSERT;
                } else if (eventType == CanalEntry.EventType.UPDATE) {
                    triggerEventType = EventType.UPDATE;
                } else {
                    //todo: add more type
                }
                eventObjectRecords.add(record);
            }
            eventObject.setTable(entry.getHeader().getTableName());
            eventObject.setEventObjectRecordList(eventObjectRecords);
            eventObject.setEventType(triggerEventType);
            return eventObject;
        }
        return new EventObject();
    }


    private static JSONObject printColumn(List<CanalEntry.Column> columns) {
        if(CollectionUtils.isEmpty(columns)){
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        for (CanalEntry.Column column : columns) {
            jsonObject.put(column.getName(), column.getValue());
        }
        return jsonObject;
    }

}

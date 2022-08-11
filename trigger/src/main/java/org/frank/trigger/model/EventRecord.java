package org.frank.trigger.model;

import lombok.Data;

@Data
public class EventRecord<T> {

    private T before;

    private T after;

}

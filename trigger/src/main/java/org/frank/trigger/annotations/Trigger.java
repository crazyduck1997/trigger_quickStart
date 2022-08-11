package org.frank.trigger.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Trigger {

    /**
     * table name
     */
    String table();


    String[] columns() default "";

    /**
     * include columns
     */
    String[] include() default "";

    /**
     * exclude columns
     */
    String[] exclude() default "";


}

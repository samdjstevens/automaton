package dev.samstevens.automaton.action.provider.annotate.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Trigger.List.class)
@Target(ElementType.METHOD)
public @interface Trigger {
    String value() default "";
    boolean caseSensitive() default false;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface List {
        Trigger[] value();
    }
}

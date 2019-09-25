package dev.samstevens.automaton.action.provider.annotate.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Channel.List.class)
@Target(ElementType.METHOD)
public @interface Channel {
    String value() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface List {
        Channel[] value();
    }
}

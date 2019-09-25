package dev.samstevens.automaton.action.provider.annotate.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Sender.List.class)
@Target(ElementType.METHOD)
public @interface Sender {
    String value() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface List {
        Sender[] value();
    }
}

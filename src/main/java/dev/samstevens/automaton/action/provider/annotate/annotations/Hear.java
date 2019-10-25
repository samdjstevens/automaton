package dev.samstevens.automaton.action.provider.annotate.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Hear.List.class)
@Target(ElementType.METHOD)
public @interface Hear {
    String value() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface List {
        Hear[] value();
    }
}

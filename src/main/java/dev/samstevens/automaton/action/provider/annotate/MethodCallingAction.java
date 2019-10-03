package dev.samstevens.automaton.action.provider.annotate;

import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.payload.Payload;
import lombok.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
class MethodCallingAction implements Action {

    private final Object instanceToCallOn;

    private final Method method;

    @Singular
    private List<String> triggers;

    @Singular
    private List<String> senders;

    @Singular
    private List<String> channels;

    private final boolean isFallback;

    @Override
    public void execute(Payload payload, String[] matches, MessageSender messageSender) {
        try {
            Object[] args = getMethodArguments(payload, matches, messageSender);
            method.invoke(instanceToCallOn, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke method action: " + e.getMessage());
        }
    }

    private Object[] getMethodArguments(Payload payload, String[] matches, MessageSender messageSender) {

        // Create an array for the arguments to invoke the method with
        Object[] args = new Object[method.getParameters().length];

        // Get the parameters the method expects
        Parameter[] params = method.getParameters();

        // Populate the arguments with either the Payload object or the matches
        int index = 0;
        for (Parameter param : params) {
            if (param.getType().equals(Payload.class)) {
                args[index] = payload;
            }

            if (param.getType().equals(MessageSender.class)) {
                args[index] = messageSender;
            }

            if (param.getType().isArray()) {
                args[index] = matches;
            }

            index++;
        }

        return args;
    }
}

package dev.samstevens.automaton.action.provider.annotate;

import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.action.provider.ActionProvider;
import dev.samstevens.automaton.action.provider.annotate.annotations.*;
import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.payload.Payload;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MethodActionProvider implements ActionProvider {

    private final Object actionsObject;
    private List<Action> actions = new ArrayList<>();

    public MethodActionProvider(Object actionsObject) {
        // Class must be public so we can execute its methods without reflection tricks
        if (! Modifier.isPublic(actionsObject.getClass().getModifiers())) {
            throw new RuntimeException("Class must be public.");
        }

        this.actionsObject = actionsObject;
        this.processActionsObject();
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    private void processActionsObject() {
        // Read each method defined on the object being processed
        for (Method method : actionsObject.getClass().getDeclaredMethods()) {

            // If the method has the @FallbackAction command
            if (isFallback(method)) {
                validateMethodIsValidAction(method);
                MethodCallingAction methodCommand = MethodCallingAction.builder()
                        .instanceToCallOn(actionsObject)
                        .method(method)
                        .isFallback(true)
                        .build();
                actions.add(methodCommand);
                continue;
            }

            // Read the @Hear annotations for the method (if any exist)
            List<String> hearTriggers = getHearTriggers(method);

            // Read the @Respond annotations for the method (if any exist)
            List<String> respondTriggers = getRespondTriggers(method);

            // If the method does not have a @Hear or @Respond annotation, not treated as an action
            if (hearTriggers.size() == 0 && respondTriggers.size() == 0) {
                continue;
            }

            validateMethodIsValidAction(method);

            // Add the action
            MethodCallingAction methodCallingAction = MethodCallingAction.builder()
                    .instanceToCallOn(actionsObject)
                    .method(method)
                    .hearTriggers(hearTriggers)
                    .respondTriggers(respondTriggers)
                    .senders(getSenders(method))
                    .channels(getChannels(method))
                    .isFallback(false)
                    .build();

            actions.add(methodCallingAction);
        }
    }

    private List<String> getHearTriggers(Method method) {
        List<String> triggers = new ArrayList<>();

        if (method.isAnnotationPresent(Hear.class)) {
            Hear trigger = method.getAnnotation(Hear.class);
            triggers.add(trigger.value());
        }

        Hear[] theTriggers = method.getAnnotationsByType(Hear.class);
        for (Hear trigger : theTriggers) {
            triggers.add(trigger.value());
        }

        return triggers;
    }

    private List<String> getRespondTriggers(Method method) {
        List<String> triggers = new ArrayList<>();

        if (method.isAnnotationPresent(Respond.class)) {
            Respond trigger = method.getAnnotation(Respond.class);
            triggers.add(trigger.value());
        }

        Respond[] theTriggers = method.getAnnotationsByType(Respond.class);
        for (Respond trigger : theTriggers) {
            triggers.add(trigger.value());
        }

        return triggers;
    }

    private List<String> getSenders(Method method) {
        List<String> senders = new ArrayList<>();

        if (method.isAnnotationPresent(Sender.class)) {
            Sender sender = method.getAnnotation(Sender.class);
            senders.add(sender.value());
        }

        Sender[] theSenders = method.getAnnotationsByType(Sender.class);
        for (Sender sender : theSenders) {
            senders.add(sender.value());
        }

        return senders;
    }

    private List<String> getChannels(Method method) {
        List<String> channels = new ArrayList<>();

        if (method.isAnnotationPresent(Channel.class)) {
            Channel channel = method.getAnnotation(Channel.class);
            channels.add(channel.value());
        }

        Channel[] theChannels = method.getAnnotationsByType(Channel.class);
        for (Channel channel : theChannels) {
            channels.add(channel.value());
        }

        return channels;
    }

    private boolean isFallback(Method method) {
        return method.isAnnotationPresent(FallbackAction.class);
    }

    private void validateMethodIsValidAction(Method method) {
        // Method must be public so we can execute it without reflection tricks
        if (! Modifier.isPublic(method.getModifiers())) {
            throw new RuntimeException("Method must be public.");
        }

        // Check the parameters for the method are of the supported types
        List<Type> supportedParamTypes = new ArrayList<>();
        supportedParamTypes.add(Payload.class);
        supportedParamTypes.add(java.lang.String[].class);
        supportedParamTypes.add(MessageSender.class);

        for (Parameter parameter : method.getParameters()) {
            if (!supportedParamTypes.contains(parameter.getType())) {
                throw new RuntimeException("Unsupported parameter type in action method.");
            }
        }
    }
}

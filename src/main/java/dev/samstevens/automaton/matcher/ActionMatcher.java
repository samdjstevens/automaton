package dev.samstevens.automaton.matcher;

import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.payload.Payload;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionMatcher {

    private List<Action> actions;

    public ActionMatcher(List<Action> actions) {
        this.actions = actions;
    }

    public MatchedAction getMatchingAction(Payload payload) {

        for (Action action : actions) {
            MatchedAction matchedAction = matches(payload, action);
            if (matchedAction != null) {
                return matchedAction;
            }
        }

        Action fallback = getFallbackAction();

        if (fallback != null) {
            return MatchedAction.builder().action(fallback).build();
        }

        return null;
    }

    private MatchedAction matches(Payload payload, Action action) {

        // Check the channels
        List<String> channels = action.getChannels();
        if (channels.size() > 0 && ! channels.contains(payload.getChannel())) {
            return null;
        }

        // Check the senders
        List<String> senders = action.getSenders();
        if (senders.size() > 0 && ! senders.contains(payload.getSender())) {
            return null;
        }

        return matchesTrigger(action, payload);
    }

    private MatchedAction matchesTrigger(Action action, Payload payload) {
        String message = payload.getMessage();
        List<String> triggers = payload.isMention() ? action.getRespondTriggers() : action.getHearTriggers();

        // No triggers, can't match
        if (triggers == null || triggers.size() == 0) {
            return null;
        }

        Pattern pattern;

        // For each trigger, see if it matches anywhere *within* the message
        for (String trigger : triggers) {
            pattern = Pattern.compile(trigger, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {

                String[] matches = new String[matcher.groupCount()];
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    matches[i - 1] = matcher.group(i);
                }

                return MatchedAction.builder()
                    .action(action)
                    .captures(matches)
                    .trigger(trigger)
                    .build();
            }
        }

        return null;
    }

    private Action getFallbackAction() {
        for (Action action : actions) {
            if (action.isFallback()) {
                return action;
            }
        }

        return null;
    }
}

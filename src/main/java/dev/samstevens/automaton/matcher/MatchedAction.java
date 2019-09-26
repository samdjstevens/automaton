package dev.samstevens.automaton.matcher;

import dev.samstevens.automaton.action.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
class MatchedAction {
    @NonNull
    private final Action action;
    private final String trigger;
    private final String[] captures;
}

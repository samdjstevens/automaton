package dev.samstevens.automaton.action.provider;

import dev.samstevens.automaton.action.Action;
import java.util.List;

public interface ActionProvider {
    List<Action> getActions();
}

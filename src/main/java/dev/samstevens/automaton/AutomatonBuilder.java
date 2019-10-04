package dev.samstevens.automaton;

import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.action.provider.ActionProvider;
import dev.samstevens.automaton.driver.Driver;
import dev.samstevens.automaton.matcher.ActionMatcher;
import java.util.ArrayList;
import java.util.List;

public class AutomatonBuilder {

    private List<ActionProvider> actionProviders =  new ArrayList<>();
    private List<Driver> drivers =  new ArrayList<>();

    public AutomatonBuilder actionProvider(ActionProvider actionProvider) {
        actionProviders.add(actionProvider);
        return this;
    }

    public AutomatonBuilder driver(Driver driver) {
        drivers.add(driver);
        return this;
    }

    public Automaton build()  {
        List<Action> actions = new ArrayList<>();
        for (ActionProvider actionProvider : actionProviders) {
            actions.addAll(actionProvider.getActions());
        }

        ActionMatcher actionMatcher = new ActionMatcher(actions);

        return new Automaton(actionMatcher, drivers);
    }
}

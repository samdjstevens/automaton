package dev.samstevens.automaton;

import dev.samstevens.automaton.driver.Driver;
import dev.samstevens.automaton.matcher.ActionMatcher;
import dev.samstevens.automaton.matcher.MatchedAction;
import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.payload.Payload;
import dev.samstevens.automaton.payload.PayloadRequestTransformingException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class Automaton {

    private final ActionMatcher actionMatcher;
    private final List<Driver> drivers;

    public Automaton(ActionMatcher actionMatcher, List<Driver> drivers) {
        this.actionMatcher = actionMatcher;
        this.drivers = drivers;
    }

    public void handleRequest(HttpServletRequest request) throws PayloadRequestTransformingException {
        for (Driver driver : drivers) {
            if (driver.getPayloadRequestTransformer().shouldTransformRequest(request)) {
                Payload payload = driver.getPayloadRequestTransformer().transformRequest(request);
                handlePayload(driver, payload);
            }
        }
    }

    private void handlePayload(Driver driver, Payload payload)  {
        MatchedAction matchedAction = actionMatcher.getMatchingAction(payload);

        if (matchedAction == null) {
            return;
        }

        // create a message sender based on the payload type + payload,
        MessageSender sender = driver.createMessageSender(payload.getChannel());

        // execute the action
        matchedAction.getAction().execute(payload, matchedAction.getCaptures(), sender);
    }
}

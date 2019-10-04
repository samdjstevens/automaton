package dev.samstevens.automaton.driver;

import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.payload.PayloadRequestTransformer;

public interface Driver {
    PayloadRequestTransformer getPayloadRequestTransformer();
    MessageSender createMessageSender(String channel);
}

package dev.samstevens.automaton.matcher;

import dev.samstevens.automaton.Automaton;
import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.payload.Payload;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import java.util.List;

@Getter
@Builder
public class TestAction implements Action {
    @Singular
    private List<String> triggers;
    @Singular
    private List<String> senders;
    @Singular
    private List<String> channels;
    private final boolean isFallback;
    @Override
    public void execute(Automaton automaton, Payload payload, String[] matches, MessageSender messageSender) {
    }
}
package dev.samstevens.automaton.action;

import dev.samstevens.automaton.Automaton;
import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.payload.Payload;
import java.util.List;

public interface Action {
    /**
     * @return The list of texts/regexes that trigger the action when @ing the bot.
     */
    List<String> getRespondTriggers();

    /**
     * @return The list of texts/regexes that trigger the action.
     */
    List<String> getHearTriggers();

    /**
     * @return The list of channels that the triggering message must come from to apply.
     * If the list is empty, any channel will be accepted.
     */
    List<String> getChannels();

    /**
     * @return The list of senders that the triggering message must come from to apply.
     * If the list is empty, any sender will be accepted.
     */
    List<String> getSenders();

    /**
     * @return True if the action should trigger when no other action is triggered.
     */
    boolean isFallback();

    /**
     * @param payload The payload of the message that triggered the action.
     * @param matches Any regex capture groups in the triggering regex.
     */
    void execute(Automaton automaton, Payload payload, String[] matches, MessageSender messageSender);
}
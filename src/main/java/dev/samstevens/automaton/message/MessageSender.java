package dev.samstevens.automaton.message;

public interface MessageSender {
    void send(String message);
    void send(String message, String channel);
}

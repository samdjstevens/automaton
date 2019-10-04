package dev.samstevens.automaton.memory;

public interface Memory {
    void remember(String key, Object value);
    Object recall(String key);
    void forget(String key);
    void forgetAll();
}
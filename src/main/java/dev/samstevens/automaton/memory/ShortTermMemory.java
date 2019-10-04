package dev.samstevens.automaton.memory;

import java.util.HashMap;

public class ShortTermMemory implements Memory {

    private HashMap<String, Object> memory = new HashMap<>();

    @Override
    public void remember(String key, Object value) {
        memory.put(key, value);
    }

    @Override
    public Object recall(String key) {
        return memory.get(key);
    }

    @Override
    public void forget(String key) {
        memory.remove(key);
    }

    @Override
    public void forgetAll() {
        memory.clear();
    }
}

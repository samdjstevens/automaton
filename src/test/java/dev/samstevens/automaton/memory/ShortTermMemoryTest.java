package dev.samstevens.automaton.memory;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class ShortTermMemoryTest {

    @Test
    public void testCanRememberAndRecallThings() {
        Memory memory = new ShortTermMemory();

        memory.remember("name", "John Doe");
        memory.remember("age", 27);
        memory.remember("isValid", Boolean.FALSE);
        List list = new ArrayList<>();
        memory.remember("list", list);

        assertSame("John Doe", memory.recall("name"));
        assertSame(27, memory.recall("age"));
        assertSame(Boolean.FALSE, memory.recall("isValid"));
        assertSame(list, memory.recall("list"));
        assertSame(null, memory.recall("unknown"));
    }

    @Test
    public void testCanForgetThings() {
        Memory memory = new ShortTermMemory();

        memory.remember("name", "John Doe");
        assertSame("John Doe", memory.recall("name"));

        memory.remember("name", "John Doe");
        assertSame("John Doe", memory.recall("name"));

        memory.forget("name");
        assertSame(null, memory.recall("name"));
    }

    @Test
    public void testCanForgetAll() {
        Memory memory = new ShortTermMemory();

        memory.remember("name", "John Doe");
        memory.remember("age", 27);
        assertSame("John Doe", memory.recall("name"));
        assertSame(27, memory.recall("age"));

        memory.remember("name", "John Doe");
        assertSame("John Doe", memory.recall("name"));

        memory.forgetAll();
        assertSame(null, memory.recall("name"));
        assertSame(null, memory.recall("age"));
    }
}

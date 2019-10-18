package dev.samstevens.automaton;

import dev.samstevens.automaton.action.provider.ActionProvider;
import dev.samstevens.automaton.driver.Driver;
import dev.samstevens.automaton.memory.Memory;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AutomatonBuilderTest {

    @Test
    public void testBuilder() {
        Memory memory = mock(Memory.class);
        Automaton automaton = new AutomatonBuilder()
            .memory(memory)
            .actionProvider(mock(ActionProvider.class))
            .driver(mock(Driver.class))
            .build();

        assertNotNull(automaton);
        assertEquals(memory, automaton.getMemory());
    }
}

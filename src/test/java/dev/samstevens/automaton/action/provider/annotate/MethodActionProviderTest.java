package dev.samstevens.automaton.action.provider.annotate;

import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.action.provider.ActionProvider;
import dev.samstevens.automaton.action.provider.annotate.annotations.*;
import dev.samstevens.automaton.payload.Payload;
import org.junit.Test;
import java.util.Collection;
import java.util.List;
import static org.junit.Assert.*;

public class MethodActionProviderTest {

    public class TestActions {
        @Trigger("test-trigger")
        @Trigger("another-trigger")
        @Sender("MrUser")
        @Sender("AnotherUser")
        @Channel("the-channel")
        @Channel("another-channel")
        public String myAction() {
            return "Hello, world!";
        }

        @FallbackAction
        public String defaultAction() {
            return "I don't understand.";
        }
    }

    public class ActionsWithWrongReturnType {
        @Trigger("test")
        public Object theAction() {
            return null;
        }
    }

    public class ActionsWithWrongParameterTypes {
        @Trigger("test")
        public String theAction(Payload payload, Collection<String> wrongArgType) {
            return null;
        }
    }

    private class PrivateClass {
        @Trigger("test")
        public String theAction() {
            return null;
        }
    }

    public class PrivateMethodClass {
        @Trigger("test")
        private String theAction() {
            return null;
        }
    }

    @Test
    public void testParsesAnnotations() {
        ActionProvider provider = new MethodActionProvider(new TestActions());
        List<Action> actions = provider.getActions();

        assertSame(2, actions.size());

        // ensure same order for testing
        actions.sort((a, b) -> 0);

        // first method/action
        Action firstAction = actions.get(0);
        assertEquals("test-trigger", firstAction.getTriggers().get(0));
        assertEquals("another-trigger", firstAction.getTriggers().get(1));
        assertEquals("MrUser", firstAction.getSenders().get(0));
        assertEquals("AnotherUser", firstAction.getSenders().get(1));
        assertEquals("the-channel", firstAction.getChannels().get(0));
        assertEquals("another-channel", firstAction.getChannels().get(1));
        assertFalse(firstAction.isFallback());

        // second method/action
        Action secondAction = actions.get(1);
        assertTrue(secondAction.isFallback());
    }

    @Test(expected = RuntimeException.class)
    public void testActionMethodThatDoesNotReturnStringThrowsException() {
        new MethodActionProvider(new ActionsWithWrongReturnType());
    }

    @Test(expected = RuntimeException.class)
    public void testActionMethodThatHasInvalidParametersThrowsException() {
        new MethodActionProvider(new ActionsWithWrongParameterTypes());
    }

    @Test(expected = RuntimeException.class)
    public void testNonPublicClassThrowsException() {
        new MethodActionProvider(new PrivateClass());
    }

    @Test(expected = RuntimeException.class)
    public void testNonPublicMethodThrowsException() {
        new MethodActionProvider(new PrivateMethodClass());
    }
}

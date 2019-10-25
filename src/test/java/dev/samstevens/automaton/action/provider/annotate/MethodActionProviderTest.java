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
        @Hear("test-trigger")
        @Hear("another-trigger")
        @Respond("respond-trigger")
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

    public class ActionsWithWrongParameterTypes {
        @Hear("test")
        public String theAction(Payload payload, Collection<String> wrongArgType) {
            return null;
        }
    }

    private class PrivateClass {
        @Hear("test")
        public String theAction() {
            return null;
        }
    }

    public class PrivateMethodClass {
        @Hear("test")
        private String theAction() {
            return null;
        }
    }

    @Test
    public void testParsesAnnotations() {
        ActionProvider provider = new MethodActionProvider(new TestActions());
        List<Action> actions = provider.getActions();

        assertSame(2, actions.size());

        // pick out the normal/fallback action
        Action normalAction = actions.stream().filter(a -> ! a.isFallback()).findFirst().orElseThrow(RuntimeException::new);
        Action fallbackAction = actions.stream().filter(Action::isFallback).findFirst().orElseThrow(RuntimeException::new);

        // first method/action
        assertEquals("test-trigger", normalAction.getHearTriggers().get(0));
        assertEquals("another-trigger", normalAction.getHearTriggers().get(1));
        assertEquals("respond-trigger", normalAction.getRespondTriggers().get(0));
        assertEquals("MrUser", normalAction.getSenders().get(0));
        assertEquals("AnotherUser", normalAction.getSenders().get(1));
        assertEquals("the-channel", normalAction.getChannels().get(0));
        assertEquals("another-channel", normalAction.getChannels().get(1));
        assertFalse(normalAction.isFallback());

        // fallback method/action
        assertTrue(fallbackAction.isFallback());
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

package dev.samstevens.automaton.provider.annotate;

import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.payload.Payload;
import org.junit.Test;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

public class MethodCallingActionTest {

    private class TestActions {
        public String myAction() {
            return "Hello, world!";
        }

        public String anotherAction(Payload payload, String[] matches) {
            return payload.getMessage() + matches[0];
        }
    }

    @Test
    public void testBuilder() throws NoSuchMethodException {
        TestActions testActions = new TestActions();
        Method method = testActions.getClass().getMethod("myAction");

        MethodCallingAction action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .trigger("Hello")
                .trigger("World")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build();


        assertSame(testActions, action.getInstanceToCallOn());
        assertSame(method, action.getMethod());
        assertSame("Hello", action.getTriggers().get(0));
        assertSame("World", action.getTriggers().get(1));
        assertSame("the-channel", action.getChannels().get(0));
        assertSame("MrUser", action.getSenders().get(0));
        assertSame(false, action.isFallback());
    }

    @Test
    public void testMethodExecutes() throws NoSuchMethodException {
        TestActions testActions = new TestActions();
        Method method = testActions.getClass().getMethod("myAction");

        Action action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .trigger("Hello")
                .trigger("World")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build();

        Payload payload = Payload.builder().message("Test").build();

        String response = action.execute(payload, new String[]{});

        assertSame("Hello, world!", response);
    }

    @Test
    public void testMethodParametersPassed() throws NoSuchMethodException {
        TestActions testActions = new TestActions();
        Method method = testActions.getClass().getMethod("anotherAction", Payload.class, java.lang.String[].class);

        Action action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .trigger("Hello")
                .trigger("World")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build();

        Payload payload = Payload.builder().message("Test").build();

        String[] args = new String[]{"Test"};
        String response = action.execute(payload, args);

        assertEquals(payload.getMessage() + args[0], response);
    }
}

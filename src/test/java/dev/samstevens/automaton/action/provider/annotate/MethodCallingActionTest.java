package dev.samstevens.automaton.action.provider.annotate;

import dev.samstevens.automaton.Automaton;
import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.payload.Payload;
import org.junit.Test;
import java.lang.reflect.Method;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MethodCallingActionTest {

    private class TestActions {
        public void myAction() {
        }

        public void anotherAction(Payload payload, String[] args) {
        }

        private void privateAction() {
        }
    }

    @Test
    public void testBuilder() throws NoSuchMethodException {
        TestActions testActions = new TestActions();
        Method method = testActions.getClass().getMethod("myAction");

        MethodCallingAction action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .hearTrigger("Hello")
                .hearTrigger("World")
                .respondTrigger("Good morning")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build();

        assertSame(testActions, action.getInstanceToCallOn());
        assertSame(method, action.getMethod());
        assertSame("Hello", action.getHearTriggers().get(0));
        assertSame("World", action.getHearTriggers().get(1));
        assertSame("Good morning", action.getRespondTriggers().get(0));
        assertSame("the-channel", action.getChannels().get(0));
        assertSame("MrUser", action.getSenders().get(0));
        assertSame(false, action.isFallback());

        // test clearers
        action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .hearTrigger("Hello")
                .hearTrigger("World")
                .respondTrigger("Good morning")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .clearChannels()
                .clearHearTriggers()
                .clearRespondTriggers()
                .clearSenders()
                .build();

        assertEquals(0, action.getChannels().size());
        assertEquals(0, action.getHearTriggers().size());
        assertEquals(0, action.getRespondTriggers().size());
        assertEquals(0, action.getSenders().size());
    }

    @Test
    public void testMethodExecutes() throws NoSuchMethodException {
        TestActions testActions = mock(TestActions.class);
        Method method = testActions.getClass().getMethod("myAction");

        Action action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .hearTrigger("Hello")
                .hearTrigger("World")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build();

        Payload payload = Payload.builder().message("Test").build();

        // execute the action
        action.execute(mock(Automaton.class), payload, new String[]{}, mock(MessageSender.class));

        // assert that the underlying method was invoked
        verify(testActions, times(1)).myAction();
    }

    @Test(expected = RuntimeException.class)
    public void testInvocationTargetExceptionThrowsRuntimeException() throws NoSuchMethodException {
        TestActions testActions = new TestActions();
        Method method = testActions.getClass().getDeclaredMethod("privateAction");

        Action action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .hearTrigger("Hello")
                .hearTrigger("World")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build();

        Payload payload = Payload.builder().message("Test").build();

        // execute the action
        // should throw exception as the method is private
        action.execute(mock(Automaton.class), payload, new String[]{}, mock(MessageSender.class));
    }

    @Test
    public void testMethodParametersPassed() throws NoSuchMethodException {
        TestActions testActions = mock(TestActions.class);
        Method method = testActions.getClass().getMethod("anotherAction", Payload.class, java.lang.String[].class);

        Action action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .hearTrigger("Hello")
                .hearTrigger("World")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build();

        Payload payload = Payload.builder().message("Test").build();
        String[] args = new String[]{"Test"};
        MessageSender sender = mock(MessageSender.class);

        // execute the action
        action.execute(mock(Automaton.class), payload, args, sender);

        // verify the method was called with the correct parameters
        verify(testActions, times(1)).anotherAction(payload, args);
    }
}

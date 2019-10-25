package dev.samstevens.automaton.matcher;

import dev.samstevens.automaton.action.Action;
import dev.samstevens.automaton.payload.Payload;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class ActionMatcherTest {

    @Test
    public void testSimpleTriggerMatching() {
        Payload payload;
        ActionMatcher matcher = new ActionMatcher(getTestActions());

        payload = Payload.builder().message("Hello, bot").build();
        MatchedAction matchedAction = matcher.getMatchingAction(payload);

        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("Hello, bot", matchedAction.getTrigger());

        payload = Payload.builder().message("Hi, bot").build();
        MatchedAction secondMatchedAction = matcher.getMatchingAction(payload);

        assertNotNull(secondMatchedAction);
        assertNotNull(secondMatchedAction.getAction());
        assertEquals("Hi, bot", secondMatchedAction.getTrigger());

        assertSame(matchedAction.getAction(), secondMatchedAction.getAction());
    }

    @Test
    public void testRegexMatching() {
        Payload payload;
        MatchedAction matchedAction;
        ActionMatcher matcher = new ActionMatcher(getTestActions());

        payload = Payload.builder().message("Hello, bot").build();
        matchedAction = matcher.getMatchingAction(payload);

        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("Hello, bot", matchedAction.getTrigger());
        assertEquals(0, matchedAction.getCaptures().length);

        payload = Payload.builder().message("What time is it in Europe/London?").build();
        matchedAction = matcher.getMatchingAction(payload);

        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("What time is it in ([a-zA-Z]+/[a-zA-Z]+)\\?", matchedAction.getTrigger());
        assertEquals("Europe/London", matchedAction.getCaptures()[0]);

        payload = Payload.builder().message("Set an alarm for 07:30 on Friday").build();
        matchedAction = matcher.getMatchingAction(payload);

        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("Set an alarm for ([0-9]{2}:[0-9]{2}) on ([a-zA-Z]+)", matchedAction.getTrigger());
        assertEquals("07:30", matchedAction.getCaptures()[0]);
        assertEquals("Friday", matchedAction.getCaptures()[1]);
    }

    @Test
    public void testRespondTriggers() {
        Payload payload;
        MatchedAction matchedAction;
        ActionMatcher matcher = new ActionMatcher(getTestActions());

        payload = Payload.builder().message("@automaton show metrics").isMention(true).build();
        matchedAction = matcher.getMatchingAction(payload);

        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("show metrics", matchedAction.getTrigger());
    }

    @Test
    public void testChannelMustMatchWhenSet() {
        Payload payload;
        MatchedAction matchedAction;
        ActionMatcher matcher = new ActionMatcher(getTestActions());

        payload = Payload.builder().message("Tell me a joke").build();
        matchedAction = matcher.getMatchingAction(payload);
        assertNull(matchedAction);

        payload = Payload.builder().message("Tell me a joke").channel("support").build();
        matchedAction = matcher.getMatchingAction(payload);
        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("Tell me a joke", matchedAction.getTrigger());

        payload = Payload.builder().message("Tell me a story").channel("help").build();
        matchedAction = matcher.getMatchingAction(payload);
        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("Tell me a story", matchedAction.getTrigger());
    }

    @Test
    public void testSenderMustMatchWhenSet() {
        Payload payload;
        MatchedAction matchedAction;
        ActionMatcher matcher = new ActionMatcher(getTestActions());

        payload = Payload.builder().message("reset caches").build();
        matchedAction = matcher.getMatchingAction(payload);
        assertNull(matchedAction);

        payload = Payload.builder().message("reset caches").sender("MrAdmin").build();
        matchedAction = matcher.getMatchingAction(payload);
        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("reset caches", matchedAction.getTrigger());

        payload = Payload.builder().message("show profile").sender("user").build();
        matchedAction = matcher.getMatchingAction(payload);
        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertEquals("show profile", matchedAction.getTrigger());
    }

    @Test
    public void testFallbackActionReturned() {
        Payload payload;
        MatchedAction matchedAction;
        List<Action> actions = getTestActions();
        Action fallbackAction = TestAction.builder()
            .isFallback(true)
            .build();
        actions.add(fallbackAction);
        ActionMatcher matcher = new ActionMatcher(actions);

        payload = Payload.builder().message("unrecognised trigger").build();
        matchedAction = matcher.getMatchingAction(payload);
        assertNotNull(matchedAction);
        assertNotNull(matchedAction.getAction());
        assertTrue(matchedAction.getAction().isFallback());
        assertSame(fallbackAction, matchedAction.getAction());
    }

    private List<Action> getTestActions() {
        List<Action> actions = new ArrayList<>();

        // simple text trigger
        actions.add(TestAction.builder()
            .hearTrigger("Hello, bot")
            .hearTrigger("Hi, bot")
            .build()
        );

        // regex trigger with capture
        actions.add(TestAction.builder()
            .hearTrigger("What time is it in ([a-zA-Z]+/[a-zA-Z]+)\\?")
            .build()
        );

        // regex trigger with multiple captures
        actions.add(TestAction.builder()
            .hearTrigger("Set an alarm for ([0-9]{2}:[0-9]{2}) on ([a-zA-Z]+)")
            .build()
        );

        // support channel only action
        actions.add(TestAction.builder()
            .hearTrigger("Tell me a joke")
            .channel("support")
            .build()
        );

        // support or help channels only action
        actions.add(TestAction.builder()
            .hearTrigger("Tell me a story")
            .channel("support")
            .channel("help")
            .build()
        );

        // specific sender only action
        actions.add(TestAction.builder()
            .hearTrigger("reset caches")
            .sender("MrAdmin")
            .build()
        );

        // multiple sender only action
        actions.add(TestAction.builder()
            .hearTrigger("show profile")
            .sender("TheAdmin")
            .sender("user")
            .build()
        );

        // respond trigger only action
        actions.add(TestAction.builder()
            .respondTrigger("show metrics")
            .build()
        );

        return actions;
    }
}

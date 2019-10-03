package dev.samstevens.automaton.action.provider.annotate

import dev.samstevens.automaton.payload.Payload
import spock.lang.Specification

class MethodCallingActionSpec extends Specification {

    private class TestActions {
        String myAction() {
             return "Hello, world!"
        }

        String anotherAction(Payload payload, String[] matches) {
            return payload.getMessage() + matches[0]
        }
    }

    def "can build a method calling action object" () {
        given:
            def testActions = new TestActions()
            def method = testActions.getClass().getMethod("myAction")

        when:
            def action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .trigger("Hello")
                .trigger("World")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build()

        then:
            testActions == action.getInstanceToCallOn()
            method == action.getMethod()
            action.getTriggers().first() == "Hello"
            action.getTriggers().get(1) == "World"
            action.getChannels().first() == "the-channel"
            action.getSenders().first() == "MrUser"
            action.isFallback() == false
    }

    def "executing the action invokes the underlying method" () {
        given:
        def testActions = new TestActions()
        def method = testActions.getClass().getMethod("myAction")
        def payload = Payload.builder().message("Test").build()
        def action = MethodCallingAction.builder()
                .instanceToCallOn(testActions)
                .method(method)
                .trigger("Hello")
                .trigger("World")
                .channel("the-channel")
                .sender("MrUser")
                .isFallback(false)
                .build()

        when:
            def response = action.execute(payload, new String[0], null)

        then:
            response == "Hello, world!"
    }

    def "method parameters are correctly passed to underlying method" () {
        given:
            def testActions = new TestActions()
            def method = testActions
                    .getClass()
                    .getMethod("anotherAction", Payload.class, String[].class)
            def action = MethodCallingAction.builder()
                    .instanceToCallOn(testActions)
                    .method(method)
                    .trigger("Hello")
                    .trigger("World")
                    .channel("the-channel")
                    .sender("MrUser")
                    .isFallback(false)
                    .build()

            def payload = Payload.builder().message("Test").build()
            def args = new String[1]
            args[0] = "Testing"

        when:
            def response = action.execute(payload, args, null)

        then:
            response == payload.getMessage() + args[0]
    }
}
